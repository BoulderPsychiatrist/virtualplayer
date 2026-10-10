package com.xminer.virtualplayer.client;

import com.xminer.virtualplayer.VirtualPlayerMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VirtualPlayerMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (ClientSetup.MENU_KEY == null) return;
        Minecraft mc = Minecraft.getInstance();
        while (ClientSetup.MENU_KEY.isPressed()) {
            if (mc.world != null && mc.currentScreen == null) {
                mc.displayGuiScreen(new BotScreen());
            }
        }
    }
}
