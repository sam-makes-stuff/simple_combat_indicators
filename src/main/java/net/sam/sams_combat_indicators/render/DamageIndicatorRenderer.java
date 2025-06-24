package net.sam.sams_combat_indicators.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sam.sams_combat_indicators.SamsCombatIndicators;
import net.sam.sams_combat_indicators.config.ClientConfig;
import net.sam.sams_combat_indicators.util.DamageDealtIndicator;
import net.sam.sams_combat_indicators.util.DamageTakenIndicator;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SamsCombatIndicators.MOD_ID, value = Dist.CLIENT)
public class DamageIndicatorRenderer {

    public static List<DamageTakenIndicator> damageTakenIndicators = new ArrayList<>();
    public static List<DamageDealtIndicator> damageDealtIndicators = new ArrayList<>();
    private static final ResourceLocation DAMAGE_TAKEN_INDICATOR = new ResourceLocation(SamsCombatIndicators.MOD_ID, "textures/client/damage_indicator2.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        renderDamageTakenIndicators(event.getPartialTick());
        renderDamageDealtIndicators(event.getPartialTick(), event.getGuiGraphics());
    }


    public static void renderDamageTakenIndicators(float partialTick){


        Window window = Minecraft.getInstance().getWindow();
        float screenWidth = window.getWidth();
        float screenHeight = window.getHeight();
        List<DamageTakenIndicator> temp1 = new ArrayList<>();
        for (DamageTakenIndicator dmg : damageTakenIndicators){
            dmg.tick(partialTick);
            float x = (float) (screenWidth/2 + (Math.sin(dmg.rotation) * dmg.centerDistPx * dmg.distScale));
            float y = (float) (screenHeight/2 - (Math.cos(dmg.rotation) * dmg.centerDistPx * dmg.distScale));
            float width = 15 * dmg.scale;
            float height = 11 * dmg.scale;
            CustomHudRenderer.renderCustomHudObject(DAMAGE_TAKEN_INDICATOR, x,y,width,height, dmg.rotation, dmg.r, dmg.g, dmg.b, dmg.opacity);
            if (dmg.age < dmg.lifetime){
                temp1.add(dmg);
            }
        }
        damageTakenIndicators = temp1;

    }


    public static void renderDamageDealtIndicators(float partialTick, GuiGraphics guiGraphics){
        List<DamageDealtIndicator> temp2 = new ArrayList<>();
        for (DamageDealtIndicator dmg : damageDealtIndicators){

            dmg.tick(partialTick);
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 up = new Vec3 (camera.getUpVector().x(),camera.getUpVector().y(),camera.getUpVector().z());
            Vec3 left = new Vec3 (camera.getLeftVector().x(),camera.getLeftVector().y(),camera.getLeftVector().z());

            double offsetY = 0.75;
            double offsetX = 0.35;

            double distanceToEntity = Minecraft.getInstance().player.getPosition(partialTick).subtract(dmg.target.getPosition(partialTick)).length();
            Vec3 pos = dmg.target.getPosition(partialTick).add(new Vec3(0,dmg.target.getBbHeight() * 0.5,0));
            pos = pos.add(up.scale(offsetY * (CustomHudRenderer.currentFov / 110) * Math.sqrt(distanceToEntity)));
            pos = pos.add(left.scale(offsetX * (CustomHudRenderer.currentFov / 110) * Math.sqrt(distanceToEntity)));

            Vec2 screenRenderPos = CustomHudRenderer.worldToScreen(pos);
            if(screenRenderPos != null){

                String text = String.format("%.0f", dmg.damage);
                CustomHudRenderer.renderText(guiGraphics, text, screenRenderPos.x, screenRenderPos.y, dmg.color, 1, dmg.rotation);
            }

            if (dmg.age < dmg.lifetime){
                temp2.add(dmg);
            }
        }
        damageDealtIndicators = temp2;
    }
}
