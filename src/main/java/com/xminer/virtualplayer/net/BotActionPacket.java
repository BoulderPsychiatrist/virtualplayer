package com.xminer.virtualplayer.net;

import com.xminer.virtualplayer.server.BotManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: spawn / remove / list virtual players. */
public class BotActionPacket {
    public static final byte SPAWN = 0;
    public static final byte REMOVE = 1;
    public static final byte REMOVE_ALL = 2;
    public static final byte LIST = 3;

    private final byte action;
    private final String name;

    public BotActionPacket(byte action, String name) {
        this.action = action;
        this.name = name == null ? "" : name;
    }

    public static void encode(BotActionPacket msg, PacketBuffer buf) {
        buf.writeByte(msg.action);
        buf.writeString(msg.name, 32);
    }

    public static BotActionPacket decode(PacketBuffer buf) {
        byte action = buf.readByte();
        String name = buf.readString(32);
        return new BotActionPacket(action, name);
    }

    public static void handle(BotActionPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.getSender();
            if (sender == null) return;
            switch (msg.action) {
                case SPAWN:
                    BotManager.spawn(sender, msg.name);
                    break;
                case REMOVE:
                    BotManager.remove(sender, msg.name);
                    break;
                case REMOVE_ALL:
                    BotManager.removeAll(sender);
                    break;
                default:
                    break;
            }
            BotManager.sendList(sender);
        });
        ctx.setPacketHandled(true);
    }
}
