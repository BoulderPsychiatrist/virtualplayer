package com.xminer.virtualplayer.net;

import com.xminer.virtualplayer.VirtualPlayerMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class Network {
    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VirtualPlayerMod.MODID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private Network() {}

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, BotActionPacket.class,
                BotActionPacket::encode, BotActionPacket::decode, BotActionPacket::handle);
        CHANNEL.registerMessage(id++, BotListPacket.class,
                BotListPacket::encode, BotListPacket::decode, BotListPacket::handle);
    }
}
