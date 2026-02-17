package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.config.RuntimeConfig;
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * Game server bootstrap.
 */
public final class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    private ServerMain() {
    }

    public static void main(String[] argvArray) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));
        initializeDependencies();

        final int serverPort = RuntimeConfig.serverPort();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(
                        new HttpServerCodec(),
                        new HttpObjectAggregator(65535),
                        new WebSocketServerProtocolHandler("/websocket"),
                        new GameMsgDecoder(),
                        new GameMsgEncoder(),
                        new GameMsgHandler()
                    );
                }
            });

            ChannelFuture f = b.bind(serverPort).sync();
            if (f.isSuccess()) {
                LOGGER.info("Game server started on port {}", serverPort);
            }

            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            LOGGER.error("Failed to run game server", ex);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static void initializeDependencies() {
        CmdHandlerFactory.init();
        GameMsgRecognizer.init();

        if (RuntimeConfig.mysqlEnabled()) {
            MySqlSessionFactory.init();
        } else {
            LOGGER.info("MySQL integration is disabled");
        }

        if (RuntimeConfig.redisEnabled()) {
            RedisUtil.init();
        } else {
            LOGGER.info("Redis integration is disabled");
        }

        if (RuntimeConfig.rocketMqEnabled()) {
            MqProducer.init();
        } else {
            LOGGER.info("RocketMQ producer is disabled");
        }
    }
}
