package com.xminer.virtualplayer.server;

import com.mojang.authlib.GameProfile;
import com.xminer.virtualplayer.VPConfig;
import com.xminer.virtualplayer.net.BotListPacket;
import com.xminer.virtualplayer.net.Network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public final class BotManager {
    private static final Map<String, VirtualPlayerEntity> BOTS = new LinkedHashMap<String, VirtualPlayerEntity>();
    private static final Pattern NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");

    private BotManager() {}

    public static boolean canUse(ServerPlayerEntity p) {
        if (!VPConfig.REQUIRE_OP.get()) return true;
        return p.hasPermissionLevel(2) || p.server.isSinglePlayer();
    }

    private static void msg(ServerPlayerEntity p, String key, Object... args) {
        p.sendStatusMessage(new TranslationTextComponent(key, args), true);
    }

    private static void cleanup() {
        Iterator<VirtualPlayerEntity> it = BOTS.values().iterator();
        while (it.hasNext()) {
            VirtualPlayerEntity b = it.next();
            if (b.removed) it.remove();
        }
    }

    private static String autoName() {
        for (int i = 1; i < 1000; i++) {
            String n = "Bot_" + i;
            if (!BOTS.containsKey(n.toLowerCase(Locale.ROOT))) return n;
        }
        return "Bot_" + System.currentTimeMillis() % 100000;
    }

    public static void spawn(ServerPlayerEntity owner, String rawName) {
        if (!canUse(owner)) {
            msg(owner, "msg.virtualplayer.no_permission");
            return;
        }
        cleanup();
        if (BOTS.size() >= VPConfig.MAX_BOTS.get()) {
            msg(owner, "msg.virtualplayer.limit", VPConfig.MAX_BOTS.get());
            return;
        }
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            name = autoName();
        } else if (!NAME.matcher(name).matches()) {
            msg(owner, "msg.virtualplayer.bad_name");
            return;
        }
        MinecraftServer server = owner.server;
        String key = name.toLowerCase(Locale.ROOT);
        if (BOTS.containsKey(key) || server.getPlayerList().getPlayerByUsername(name) != null) {
            msg(owner, "msg.virtualplayer.name_taken", name);
            return;
        }

        ServerWorld world = owner.getServerWorld();
        GameProfile profile = new GameProfile(
                UUID.nameUUIDFromBytes(("VirtualPlayer:" + key).getBytes(StandardCharsets.UTF_8)), name);

        VirtualPlayerEntity bot = new VirtualPlayerEntity(server, world, profile);
        bot.connection = new ServerPlayNetHandler(server, new FakeNetworkManager(), bot);
        bot.setLocationAndAngles(owner.getPosX(), owner.getPosY(), owner.getPosZ(), owner.rotationYaw, owner.rotationPitch);
        bot.interactionManager.setGameType(GameType.SURVIVAL);
        bot.setInvulnerable(VPConfig.INVULNERABLE.get());
        bot.setNoGravity(true);

        // Tell clients about the player first, so the entity spawn packet that follows is valid.
        server.getPlayerList().sendPacketToAllPlayers(
                new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, bot));
        world.addNewPlayer(bot);
        BOTS.put(key, bot);
        msg(owner, "msg.virtualplayer.spawned", name);
    }

    public static void remove(ServerPlayerEntity owner, String rawName) {
        if (!canUse(owner)) {
            msg(owner, "msg.virtualplayer.no_permission");
            return;
        }
        cleanup();
        String name = rawName == null ? "" : rawName.trim();
        VirtualPlayerEntity bot = BOTS.remove(name.toLowerCase(Locale.ROOT));
        if (bot == null) {
            msg(owner, "msg.virtualplayer.not_found", name);
            return;
        }
        despawn(bot);
        msg(owner, "msg.virtualplayer.removed", name);
    }

    public static void removeAll(ServerPlayerEntity owner) {
        if (!canUse(owner)) {
            msg(owner, "msg.virtualplayer.no_permission");
            return;
        }
        int n = removeAllQuiet();
        msg(owner, "msg.virtualplayer.removed_all", n);
    }

    public static int removeAllQuiet() {
        int n = 0;
        for (VirtualPlayerEntity bot : new ArrayList<VirtualPlayerEntity>(BOTS.values())) {
            despawn(bot);
            n++;
        }
        BOTS.clear();
        return n;
    }

    private static void despawn(VirtualPlayerEntity bot) {
        bot.server.getPlayerList().sendPacketToAllPlayers(
                new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, bot));
        bot.remove();
    }

    public static void clear() {
        BOTS.clear();
    }

    public static boolean hasBotsIn(ServerWorld world) {
        cleanup();
        for (VirtualPlayerEntity b : BOTS.values()) {
            if (b.world == world) return true;
        }
        return false;
    }

    public static void sendTabEntriesTo(ServerPlayerEntity joiner) {
        cleanup();
        for (VirtualPlayerEntity b : BOTS.values()) {
            joiner.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, b));
        }
    }

    public static void sendList(ServerPlayerEntity to) {
        cleanup();
        List<String> names = new ArrayList<String>();
        for (VirtualPlayerEntity b : BOTS.values()) {
            names.add(b.getGameProfile().getName());
        }
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> to), new BotListPacket(names));
    }
}
