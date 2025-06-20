package net.sam.sams_combat_indicators.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.sam.sams_combat_indicators.ClientEvents;
import net.sam.sams_combat_indicators.util.ModParticles;

public class Init {

    public static void registerAll(IEventBus bus){

        ModParticles.PARTICLES.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        });

    }

}
