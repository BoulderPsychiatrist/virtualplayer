package com.xminer.virtualplayer;

import com.xminer.virtualplayer.net.Network;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VirtualPlayerMod.MODID)
public class VirtualPlayerMod {
    public static final String MODID = "virtualplayer";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public VirtualPlayerMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VPConfig.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Network.register();
    }
}
