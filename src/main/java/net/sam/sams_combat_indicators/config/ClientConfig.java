package net.sam.sams_combat_indicators.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DAMAGE_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DAMAGE_TAKEN_INDICATOR;

    public static final ForgeConfigSpec.ConfigValue<Integer> NUMBER_DURATION;

    public static final ForgeConfigSpec.ConfigValue<Integer> INITIAL_HIT_TIME;

    public static final ForgeConfigSpec.ConfigValue<Integer> START_NUMBER_COLOR_R;
    public static final ForgeConfigSpec.ConfigValue<Integer> START_NUMBER_COLOR_G;
    public static final ForgeConfigSpec.ConfigValue<Integer> START_NUMBER_COLOR_B;

    public static final ForgeConfigSpec.ConfigValue<Integer> END_NUMBER_COLOR_R;
    public static final ForgeConfigSpec.ConfigValue<Integer> END_NUMBER_COLOR_G;
    public static final ForgeConfigSpec.ConfigValue<Integer> END_NUMBER_COLOR_B;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOULD_SEPARATE_KILL_COLOR;

    public static final ForgeConfigSpec.ConfigValue<Integer> KILL_COLOR_R;
    public static final ForgeConfigSpec.ConfigValue<Integer> KILL_COLOR_G;
    public static final ForgeConfigSpec.ConfigValue<Integer> KILL_COLOR_B;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DO_NUMBERS_FADE;

    public static final ForgeConfigSpec.ConfigValue<Double> NUMBER_BASE_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Double> NUMBER_INCREMENT_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Double> NUMBER_DECREMENT_SPEED;

    public static final ForgeConfigSpec.ConfigValue<Boolean> STACKING_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SPRAY_NUMBERS;
    public static final ForgeConfigSpec.ConfigValue<Integer> ROTATE_RANGE;

    static {
        BUILDER.push("Config for Sam's Combat Indicators");

        ENABLE_DAMAGE_NUMBERS = BUILDER.comment("Should damage numbers show DEFAULT: true").define("enable_damage_numbers", true);
        ENABLE_DAMAGE_TAKEN_INDICATOR = BUILDER.comment("Should damage taken indicators show DEFAULT: true").define("enable_damage_taken_indicator", true);

        NUMBER_DURATION = BUILDER.comment("Damage number duration (in ticks) DEFAULT: 30").define("number_duration", 30);

        INITIAL_HIT_TIME = BUILDER.comment("Duration damage numbers flash a different color and size initially (set to 0 to disable) (in ticks) DEFAULT: 10").define("separate_initial_hit_time", 10);

        START_NUMBER_COLOR_R = BUILDER.comment("Start damage number color red value (0-255) DEFAULT: 255").define("start_number_color_r", 255);
        START_NUMBER_COLOR_G = BUILDER.comment("Start damage number color green value (0-255) DEFAULT: 102").define("start_number_color_g", 102);
        START_NUMBER_COLOR_B = BUILDER.comment("Start damage number color blue value (0-255) DEFAULT: 0").define("start_number_color_b", 0);

        END_NUMBER_COLOR_R = BUILDER.comment("End damage number color red value (0-255) DEFAULT: 255").define("end_number_color_r", 255);
        END_NUMBER_COLOR_G = BUILDER.comment("End damage number color green (0-255) DEFAULT: 255").define("end_number_color_g", 255);
        END_NUMBER_COLOR_B = BUILDER.comment("End damage number color blue value (0-255) DEFAULT: 0").define("end_number_color_b", 0);

        SHOULD_SEPARATE_KILL_COLOR = BUILDER.comment("Should damage that kills be a different color DEFAULT: true").define("separate_kill_color", true);

        KILL_COLOR_R = BUILDER.comment("Kill damage number color red value (0-255) DEFAULT: 255").define("end_number_color_r", 255);
        KILL_COLOR_G = BUILDER.comment("Kill damage number color green (0-255) DEFAULT: 0").define("end_number_color_g", 0);
        KILL_COLOR_B = BUILDER.comment("Kill damage number color blue value (0-255) DEFAULT: 0").define("end_number_color_b", 0);

        NUMBER_BASE_SCALE = BUILDER.comment("Number base scale DEFAULT: 3.0").define("number_base_scale", 3.0);
        NUMBER_INCREMENT_SCALE = BUILDER.comment("Scale increase per successive hit (only applies when stacking numbers is on) DEFAULT: 1.0").define("number_increment_scale", 1.0);
        NUMBER_DECREMENT_SPEED = BUILDER.comment("How fast numbers decrease in scale (unit per second) (DEFAULT : 1.5)").define("number_decrement_speed", 1.5);

        DO_NUMBERS_FADE = BUILDER.comment("Should damage numbers fade away").define("do_numbers_fade", true);
        STACKING_NUMBERS = BUILDER.comment("Should damage numbers on a target sum up?").define("stack_damage_numbers", true);
        SPRAY_NUMBERS = BUILDER.comment("Should damage numbers spray in a random direction?").define("spray_damage_numbers", false);
        ROTATE_RANGE = BUILDER.comment("Range in degrees (-rotate_range to +rotate_range) between which damage numbers will randomly rotate (set to zero to disable)").define("rotate_range", 15);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
