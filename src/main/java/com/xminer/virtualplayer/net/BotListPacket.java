package com.xminer.virtualplayer.net;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Server -> client: names of the currently active virtual players. */
public class BotListPacket {
    private final List<String> names;

    public BotListPacket(List<String> names) {
        this.names = names;
    }

    public static void encode(BotListPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.names.size());
        for (String s : msg.names) {
            buf.writeString(s, 32);
        }
    }

    public static BotListPacket decode(PacketBuffer buf) {
        int n = buf.readVarInt();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < n && i < 256; i++) {
            list.add(buf.readString(32));
        }
        return new BotListPacket(list);
    }

    public static void handle(BotListPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        // BotClientState is a plain static holder: safe to touch from common code.
        ctx.enqueueWork(() -> BotClientState.setBots(msg.names));
        ctx.setPacketHandled(true);
    }
}
