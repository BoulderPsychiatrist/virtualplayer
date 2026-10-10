package com.xminer.virtualplayer.server;

import com.xminer.virtualplayer.VirtualPlayerMod;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

/** A connection that goes nowhere: every packet sent to the virtual player is dropped. */
public class FakeNetworkManager extends NetworkManager {

    public FakeNetworkManager() {
        super(PacketDirection.SERVERBOUND);
        try {
            // field_150746_k = NetworkManager.channel; give it a dummy channel so nothing NPEs
            ObfuscationReflectionHelper.setPrivateValue(NetworkManager.class, this, new EmbeddedChannel(), "field_150746_k");
        } catch (Throwable t) {
            VirtualPlayerMod.LOGGER.warn("Could not set dummy channel for virtual player connection", t);
        }
    }

    @Override
    public void sendPacket(IPacket<?> packetIn) {
        // dropped
    }

    @Override
    public void sendPacket(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
        // dropped
    }

    @Override
    public boolean isChannelOpen() {
        return true;
    }
}
