package com.xminer.virtualplayer.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.server.ServerWorld;

/**
 * A real ServerPlayerEntity without a real client. Because it is a genuine player inside the
 * world's player list, vanilla keeps chunks around it loaded/ticking and spawns mobs near it.
 */
public class VirtualPlayerEntity extends ServerPlayerEntity {

    public VirtualPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile, new PlayerInteractionManager(world));
    }
}
