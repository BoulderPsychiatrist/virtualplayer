package com.xminer.virtualplayer;

import net.minecraftforge.common.ForgeConfigSpec;

public class VPConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_BOTS;
    public static final ForgeConfigSpec.BooleanValue INVULNERABLE;
    public static final ForgeConfigSpec.BooleanValue FIX_SLEEP;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_OP;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("general");
        MAX_BOTS = b.comment("Maximum number of virtual players at the same time")
                .defineInRange("maxBots", 4, 1, 32);
        INVULNERABLE = b.comment("Virtual players cannot be damaged or killed")
                .define("invulnerable", true);
        FIX_SLEEP = b.comment("Virtual players are ignored when real players sleep (night can be skipped)")
                .define("fixSleep", true);
        REQUIRE_OP = b.comment("Only operators (permission level 2+) can manage virtual players. Always allowed in singleplayer")
                .define("requireOp", true);
        b.pop();
        SPEC = b.build();
    }

    private VPConfig() {}
}
