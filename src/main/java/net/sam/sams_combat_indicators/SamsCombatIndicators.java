package net.sam.sams_combat_indicators;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sam.sams_combat_indicators.config.ClientConfig;
import net.sam.sams_combat_indicators.config.CommonConfig;
import net.sam.sams_combat_indicators.init.Init;
import net.sam.sams_combat_indicators.networking.ModPackets;
import net.sam.sams_combat_indicators.util.DamageHandler;
import org.slf4j.Logger;

@Mod(SamsCombatIndicators.MOD_ID)
public class SamsCombatIndicators
{
    public static final String MOD_ID = "sams_combat_indicators";
    private static final Logger LOGGER = LogUtils.getLogger();
    public SamsCombatIndicators()
    {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        Init.registerAll(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "sams_combat_indicators-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "sams_combat_indicators-client.toml");

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        ModPackets.registerClient();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
