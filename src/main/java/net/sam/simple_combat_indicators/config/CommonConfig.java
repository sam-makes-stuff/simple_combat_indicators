package net.sam.simple_combat_indicators.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> AIRDROP_CHEST_1_LOOTPOOL;


    static {
        BUILDER.push("Common config for Sam's Combat Indicators");

        AIRDROP_CHEST_1_LOOTPOOL = BUILDER.comment("Lootpool to use for first chest in the airdrop").define("Chest 1 Lootpool", "minecraft:chests/abandoned_mineshaft");


        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
