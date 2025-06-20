package net.sam.sams_combat_indicators.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CustomHudRenderer {

    public static void renderCustomHudObject(ResourceLocation texture, float x, float y, float width, float height, float rotationRads, float r, float g, float b, float a) {

        float scale_gui = 1.0f/(float) Minecraft.getInstance().getWindow().getGuiScale(); // e.g. 2.0

        PoseStack poseStack = new PoseStack();
        float pixelScale = 3.0f;
        poseStack.pushPose();
        poseStack.scale(scale_gui, scale_gui, 1);
        poseStack.translate(x, y, 0);
        poseStack.scale(pixelScale, pixelScale, 1);
        poseStack.mulPose(Axis.ZP.rotation(rotationRads));
        poseStack.translate(-(width) / 2, -(height) / 2, 0);

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(r, g, b, a);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, 0,0, 0).uv(0, 0).endVertex();
        buffer.vertex(matrix, width,0,        0).uv(1, 0).endVertex();
        buffer.vertex(matrix, width,   height,   0).uv(1, 1).endVertex();
        buffer.vertex(matrix, 0,       height,   0).uv(0, 1).endVertex();
        tesselator.end();
        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
    }

}
