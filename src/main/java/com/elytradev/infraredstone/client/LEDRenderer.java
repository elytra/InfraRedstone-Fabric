package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.entity.LEDBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.lwjgl.opengl.GL11;

public class LEDRenderer extends BlockEntityRenderer<LEDBlockEntity> {
	private double minDist = -0.002;
	private double maxDist = 1.002;

	@Override
	public void render(LEDBlockEntity be, double x, double y, double z, float partialTicks, int destroyStage) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBufferBuilder();
		buffer.setOffset(x, y, z);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();

		renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.enableTexture();

		this.method_3570(true);
		Sprite side = MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:block/led_side_glow");
		Sprite end = MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:block/led_end_glow");
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_LMAP_COLOR);
		float brightness = (be.getLightLevel() / 15f);
		if (be.getLightLevel() > 0) {
			renderUp(buffer, end, brightness);
			renderDown(buffer, end, brightness);
			renderNorth(buffer, side, brightness);
			renderSouth(buffer, side, brightness);
			renderEast(buffer, side, brightness);
			renderWest(buffer, side, brightness);
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		buffer.setOffset(0.0, 0.0, 0.0);
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableLighting();
		this.method_3570(false);
		super.render(be, x, y, z, partialTicks, destroyStage);
	}

	public void renderUp(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(0, maxDist, 0).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(0, maxDist, 1).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, maxDist, 1).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, maxDist, 0).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}

	public void renderDown(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(0, minDist, 0).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, minDist, 0).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, minDist, 1).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(0, minDist, 1).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}

	public void renderSouth(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(0, 1, maxDist).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, 1, maxDist).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, 0, maxDist).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(0, 0, maxDist).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}

	public void renderNorth(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(0, 1, minDist).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(0, 0, minDist).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, 0, minDist).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(1, 1, minDist).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}

	public void renderWest(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(minDist, 1, 0).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(minDist, 1, 1).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(minDist, 0, 1).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(minDist, 0, 0).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}

	public void renderEast(BufferBuilder buffer, Sprite sprite, float brightness) {
		buffer.vertex(maxDist, 1, 0).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(maxDist, 0, 0).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(maxDist, 0, 1).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
		buffer.vertex(maxDist, 1, 1).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, brightness).next();
	}
}
