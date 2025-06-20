package net.sam.sams_combat_indicators.render;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sam.sams_combat_indicators.SamsCombatIndicators;
import net.sam.sams_combat_indicators.util.DamageIndicator;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class DamageIndicatorRenderer {

    public static List<DamageIndicator> damageIndicators = new ArrayList<>();
    private static final ResourceLocation DAMAGE_INDICATOR = new ResourceLocation(SamsCombatIndicators.MOD_ID, "textures/client/damage_indicator2.png");

    @SubscribeEvent
    public static void onRender(RenderGuiEvent.Post event) {
        renderDamageIndicators(event.getPartialTick());
    }

    public static void renderDamageIndicators(float partialTick){

        List<DamageIndicator> temp = new ArrayList<>();

        Window window = Minecraft.getInstance().getWindow();
        float screenWidth = window.getWidth();
        float screenHeight = window.getHeight();

        for (DamageIndicator dmg : damageIndicators){
            dmg.tick(partialTick);
            float x = (float) (screenWidth/2 + (Math.sin(dmg.rotation) * dmg.centerDistPx * dmg.distScale));
            float y = (float) (screenHeight/2 - (Math.cos(dmg.rotation) * dmg.centerDistPx * dmg.distScale));
            float width = 15 * dmg.scale;
            float height = 11 * dmg.scale;
            CustomHudRenderer.renderCustomHudObject(DAMAGE_INDICATOR, x,y,width,height, dmg.rotation, dmg.r, dmg.g, dmg.b, dmg.opacity);
            if (dmg.age < dmg.lifetime){
                temp.add(dmg);
            }
        }
        damageIndicators = temp;
    }
}
