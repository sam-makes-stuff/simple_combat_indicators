package net.sam.sams_combat_indicators;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sam.sams_combat_indicators.util.FloatingDamageRenderer;

// 5. Register render hook in Client setup
@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {


        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        FloatingDamageRenderer.render(
                event.getPoseStack(),
                event.getLevelRenderer(),
                event.getCamera(),
                event.getPartialTick()
        );
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        FloatingDamageRenderer.damageNumbers.removeIf(dmg -> dmg.update());
    }
}
