package net.sam.sams_combat_indicators.util;

import net.minecraftforge.common.ForgeConfigSpec;
import net.sam.sams_combat_indicators.config.ClientConfig;

import java.util.function.Supplier;

public class ConfigGetter {

    public static <T> T getOrDefault(ForgeConfigSpec.ConfigValue<T> configValue) {
        try {
            T value = configValue.get();
            if (value == null) {
                return configValue.getDefault();
            }
            return value;
        } catch (Exception e) {
            System.err.println("Invalid config value: " + e.getMessage());
            return configValue.getDefault();
        }
    }
}
