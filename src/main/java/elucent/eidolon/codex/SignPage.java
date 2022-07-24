package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import elucent.eidolon.ClientEvents;
import elucent.eidolon.Eidolon;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.util.ColorUtil;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SignPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_sign_page.png");
    Sign sign;

    public SignPage(Sign sign) {
        super(BACKGROUND);
        this.sign = sign;
    }

    @OnlyIn(Dist.CLIENT)
    static void colorBlit(PoseStack mStack, int x, int y, int uOffset, int vOffset, int width, int height, int textureWidth, int textureHeight, int color) {
        Matrix4f matrix = mStack.last().pose();
        int maxX = x + width, maxY = y + height;
        float minU = (float)uOffset / textureWidth, minV = (float)vOffset / textureHeight;
        float maxU = minU + (float)width / textureWidth, maxV = minV + (float)height / textureHeight;
        int r = ColorUtil.getRed(color),
            g = ColorUtil.getGreen(color),
            b = ColorUtil.getBlue(color);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float)x, (float)maxY, 0).uv(minU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)maxY, 0).uv(maxU, maxV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float)maxX, (float)y, 0).uv(maxU, minV).color(r, g, b, 255).endVertex();
        bufferbuilder.vertex(matrix, (float)x, (float)y, 0).uv(minU, minV).color(r, g, b, 255).endVertex();
        bufferbuilder.end();
        // todo RenderSystem.enableAlphaTest();
        BufferUploader.end(bufferbuilder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        Tesselator tess = Tesselator.getInstance();
        RenderSystem.enableBlend();
        // todo RenderSystem.alphaFunc(GL11.GL_GEQUAL, 1f / 256f);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        poseStack.pushPose();
        poseStack.translate(x + 64, y + 80, 0);
        // mStack.scale(0.9f, 0.9f, 0.9f);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(ClientEvents.getClientTicks() * 1.5f));
        colorBlit(poseStack, -40, -40, 128, 96, 80, 80, 256, 256, sign.getColor());
        poseStack.popPose();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        for (int i = 0; i < 2; i ++) {
            RenderUtil.litQuad(poseStack, MultiBufferSource.immediate(tess.getBuilder()), x + 44, y + 60, 40, 40,
                sign.getRed(), sign.getGreen(), sign.getBlue(), Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sign.getSprite()));
            tess.end();
        }
        // todo RenderSystem.defaultAlphaFunc();
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
}
