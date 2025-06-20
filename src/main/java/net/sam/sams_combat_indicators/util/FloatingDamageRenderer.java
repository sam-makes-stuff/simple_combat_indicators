package net.sam.sams_combat_indicators.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FloatingDamageRenderer {
    public static final List<FloatingDamage> damageNumbers = new ArrayList<>();

    public static void spawnDamageNumber(float amount, Entity target) {
        boolean found = false;
        for (FloatingDamage dmg : damageNumbers) {
            if(dmg.attachedTo == target){
                damageNumbers.add(new FloatingDamage(amount + dmg.amount, target));
                damageNumbers.remove(dmg);
                found = true;
                break;
            }
        }
        if(!found){
            damageNumbers.add(new FloatingDamage(amount, target));
        }
    }

    public static void render(PoseStack poseStack, LevelRenderer levelRenderer, Camera camera, float partialTicks) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            Vec3 camPos = camera.getPosition();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                    GL11.GL_ZERO);
            RenderSystem.disableDepthTest();
            for (FloatingDamage dmg : damageNumbers) {
                dmg.follow(partialTicks);
                Vec3 pos = dmg.position;
                Vec3 up = new Vec3 (camera.getUpVector().x(),camera.getUpVector().y(),camera.getUpVector().z());
                Vec3 left = new Vec3 (camera.getLeftVector().x(),camera.getLeftVector().y(),camera.getLeftVector().z());

                double offsetY = 0.9;
                double offsetX = 0.5;

                Minecraft mc = Minecraft.getInstance();
                double fov = mc.options.fov().get();
                double baseFov = 110.0;
                double fovScale = fov / baseFov;
                double distanceToNum = camera.getPosition().distanceTo(pos);
                pos = pos.add(up.scale(offsetY * fovScale * Math.sqrt(distanceToNum)));
                pos = pos.add(left.scale(offsetX * fovScale * Math.sqrt(distanceToNum)));
                float scale = (float)(0.01 * distanceToNum * fovScale);
                if (dmg.big){
                    scale *= 1.3;
                }

                poseStack.pushPose();
                poseStack.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
                poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
                poseStack.scale(-scale, -scale, scale);

                Font font = Minecraft.getInstance().font;
                Component text = Component.literal(String.format("%.0f", dmg.amount));

                float xOffset = -font.width(text) / 2f;  // Centered
                float yOffset = -font.lineHeight / 2f;

                if (dmg.big){
                    font.drawInBatch(text, xOffset, yOffset, 0xFFF700, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0x000000, 15728880);
                    poseStack.scale(1.1f, 1.1f, 1f);
                    font.drawInBatch(text, xOffset, yOffset, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0x000000, 15728880);
                }else{
                    font.drawInBatch(text, xOffset, yOffset, 0xFF7300, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0x000000, 15728880);
                    poseStack.scale(1.1f, 1.1f, 1f);
                    font.drawInBatch(text, xOffset, yOffset, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0x000000, 15728880);
                }
                poseStack.popPose();
            }
            bufferSource.endBatch();
            RenderSystem.enableDepthTest();        });
            RenderSystem.disableBlend();
    }

    public static class FloatingDamage {
        public Entity attachedTo;
        public float amount;
        public Vec3 position;
        public float age = 0;
        public final int lifetime = 30;
        Font font = Minecraft.getInstance().font;
        public boolean big = true;
        public int bigTime = 10;
        public FloatingDamage(float amount, Entity attachedTo) {
            this.amount = amount;
            this.position = attachedTo.position().add(0, attachedTo.getBbHeight() * 0.5, 0);
            this.attachedTo = attachedTo;
        }

        public boolean update() {
            age++;
            if (age > bigTime){
                big = false;
            }
            return age > lifetime;
        }

        public void follow(float partialTick){
            this.position = attachedTo.getPosition(partialTick).add(0, attachedTo.getBbHeight() * 0.5, 0);
        }
    }

}