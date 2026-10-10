package com.xminer.virtualplayer.server;

import com.xminer.virtualplayer.VPConfig;
import com.xminer.virtualplayer.VirtualPlayerMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = VirtualPlayerMod.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        BotManager.clear();
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        BotManager.removeAllQuiet();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity p = event.getPlayer();
        if (p instanceof ServerPlayerEntity && !(p instanceof VirtualPlayerEntity)) {
            BotManager.sendTabEntriesTo((ServerPlayerEntity) p);
        }
    }

    /**
     * Vanilla needs EVERY non-spectator player to be asleep to skip the night, and a virtual
     * player never sleeps. So when only real players are left, skip the night for them ourselves.
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!VPConfig.FIX_SLEEP.get()) return;
        if (!(event.world instanceof ServerWorld)) return;
        ServerWorld world = (ServerWorld) event.world;
        if (world.getDimensionKey() != World.OVERWORLD) return;
        if (!BotManager.hasBotsIn(world)) return;

        List<ServerPlayerEntity> real = new ArrayList<ServerPlayerEntity>();
        for (ServerPlayerEntity p : world.getPlayers()) {
            if (!(p instanceof VirtualPlayerEntity) && !p.isSpectator()) real.add(p);
        }
        if (real.isEmpty()) return;
        for (ServerPlayerEntity p : real) {
            if (!(p.isSleeping() && p.getSleepTimer() >= 100)) return;
        }
        long t = world.getDayTime() + 24000L;
        ((IServerWorldInfo) world.getWorldInfo()).setDayTime(t - t % 24000L);
        for (ServerPlayerEntity p : real) {
            p.stopSleepInBed(false, false);
        }
    }
}
