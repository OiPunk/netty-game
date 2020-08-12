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
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * 游戏服务器类
 */
public class ServerMain {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 服务器端口号
     */
    static private final int SERVER_PORT = 12345;

    /**
     * 应用主函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 初始化命令处理器工厂
        CmdHandlerFactory.init();
        // 初始化消息识别器
        GameMsgRecognizer.init();
        // 初始化 MySql 会话工厂
        MySqlSessionFactory.init();
        // 初始化 Redis
        RedisUtil.init();
        // 初始化消息队列
        MqProducer.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();   // 拉客的, 也就是故事中的美女
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 干活的, 也就是故事中的服务生

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class); // 服务器信道的处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                    new HttpServerCodec(), // Http 服务器编解码器
                    new HttpObjectAggregator(65535), // 内容长度限制
                    new WebSocketServerProtocolHandler("/websocket"), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                    new GameMsgDecoder(),  // 自定义的消息解码器
                    new GameMsgEncoder(),  // 自定义的消息编码器
                    new GameMsgHandler()   // 自定义的消息处理器
                );
            }
        });

        try {
            // 绑定 12345 端口,
            // 注意: 实际项目中会使用 argvArray 中的参数来指定端口号
            ChannelFuture f = b.bind(SERVER_PORT).sync();

            if (f.isSuccess()) {
                LOGGER.info(">>> 游戏服务器启动成功! <<<");
            }

            // 等待服务器信道关闭,
            // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            // 关闭服务器, 大家都歇了吧
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
