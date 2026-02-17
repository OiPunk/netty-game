package org.tinygame.valorlegend;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BroadcasterTest {
    @Test
    void shouldBroadcastToAllRegisteredChannels() {
        EmbeddedChannel ch1 = new EmbeddedChannel();
        int before = Broadcaster.countChannels();

        Broadcaster.addChannel(ch1);
        assertEquals(before + 1, Broadcaster.countChannels());

        Broadcaster.broadcast("hello");
        assertEquals(before + 1, Broadcaster.countChannels());

        Broadcaster.removeChannel(ch1);
        assertEquals(before, Broadcaster.countChannels());

        ch1.finishAndReleaseAll();
    }

    @Test
    void shouldIgnoreNullInputs() {
        int before = Broadcaster.countChannels();

        Broadcaster.addChannel(null);
        Broadcaster.removeChannel(null);
        Broadcaster.broadcast(null);

        assertEquals(before, Broadcaster.countChannels());
    }
}
