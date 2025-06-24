package net.sam.sams_combat_indicators.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> STACKING_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SPRAY_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ROTATE_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Integer> ROTATE_RANGE;

    static {
        BUILDER.push("Config for Sam's Combat Indicators");

        STACKING_NUMBERS = BUILDER.comment("Should damage numbers on a target sum up?").define("stack_damage_numbers", true);
        SPRAY_NUMBERS = BUILDER.comment("Should damage numbers spray in a random direction?").define("spray_damage_numbers", false);
        ROTATE_NUMBERS = BUILDER.comment("Should damage numbers spawn with random rotation?").define("rotate_damage_numbers", false);
        ROTATE_RANGE = BUILDER.comment("Range in degrees (-rotate_range to +rotate_range) between which damage numbers will randomly rotate (does nothing if rotate_damage_numbers is disabled)").define("rotate_range", 30);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
