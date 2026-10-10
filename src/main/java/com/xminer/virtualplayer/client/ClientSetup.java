package com.xminer.virtualplayer.client;

import com.xminer.virtualplayer.VirtualPlayerMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = VirtualPlayerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static KeyBinding MENU_KEY;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MENU_KEY = new KeyBinding("key.virtualplayer.menu", GLFW.GLFW_KEY_J, "key.categories.virtualplayer");
        ClientRegistry.registerKeyBinding(MENU_KEY);
    }
}
