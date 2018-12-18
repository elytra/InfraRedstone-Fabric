package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.InRedLog;
import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.lwjgl.opengl.GL11;

import static prospector.silk.util.RenderUtil.frac;

public class DiodeRender extends BlockEntityRenderer<DiodeBlockEntity> {

	@Override
	public void render(DiodeBlockEntity diode, double x, double y, double z, float partialTicks, int destroyStage) {
//		if (diode.isActive()) {
//			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBufferBuilder();
			buffer.setOffset(x, y, z);
			GlStateManager.disableLighting();
			Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:block/diode_glow");
//			Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.DIAMOND_BLOCK.getDefaultState()).getSprite();
			InRedLog.info(sprite);
			double faceHeight = frac(4);
			//faceHeight += 0.002; //Far enough to not z-fight. Hopefully.
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
			buffer.vertex(frac(1), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
			buffer.vertex(frac(1), faceHeight, frac(1)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
			buffer.vertex(frac(0), faceHeight, frac(1)).texture(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
//		}
		super.render(diode, x, y, z, partialTicks, destroyStage);
	}
}
