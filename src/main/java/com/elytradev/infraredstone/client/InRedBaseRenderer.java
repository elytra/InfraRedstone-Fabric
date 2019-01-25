package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

import static io.github.prospector.silk.util.MathUtil.frac;

public abstract class InRedBaseRenderer<T extends IRComponentBlockEntity> extends BlockEntityRenderer {
	protected Torch[] torches = new Torch[]{};

	@Override
	public void render(BlockEntity be, double x, double y, double z, float partialTicks, int destroyStage) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBufferBuilder();
		buffer.setOffset(x, y, z);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();

		renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.enableTexture();

		this.method_3570(true);
		Sprite sprite = getLightupTexture((T) be);
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_LMAP_COLOR); //position, texture, lightmap, color
		if (sprite!=null) renderTopFace(buffer, sprite, getFacing((T)be));
		if (torches != null && torches.length != 0) {
			for (Torch torch : torches) {
				Sprite light = (torch.isLit) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:block/lights_glow") : null;
				if (light != null) renderLight(buffer, light, torch.cornerX, torch.cornerZ, torch.isFullHeight, getFacing((T)be));
			}
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

	public void renderTopFace(BufferBuilder buffer, Sprite sprite, Direction facing) {
		double faceHeight = frac(3);
		faceHeight += 0.002; //Far enough to not z-fight. Hopefully.

		switch(facing) {
			case NORTH:
			default:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
			case EAST:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
			case SOUTH:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
			case WEST:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
		}
	}

	public void renderLight(BufferBuilder buffer, Sprite sprite, Double cornerX, Double cornerZ, boolean isFullHeight, Direction facing) {
		double adaptedX = cornerX;
		double adaptedZ = cornerZ;
		switch(facing) {
			case EAST:
				adaptedX = -1*cornerZ + frac(14);
				adaptedZ = cornerX;
				break;
			case SOUTH:
				adaptedZ = -1*adaptedZ + frac(14);
				adaptedX = -1*adaptedX + frac(14);
				break;
			case WEST:
				adaptedX = cornerZ;
				adaptedZ = -1*cornerX + frac(14);
				break;
			default:
				break;
		}
		double eastX = adaptedX-0.002;
		double westX = adaptedX+0.127;
		double northZ = adaptedZ-0.002;
		double southZ = adaptedZ+0.127;
		double topY = (isFullHeight) ? frac(5)+0.002 : frac(4)+0.002;
		double minY = (isFullHeight) ? frac(3) : frac(2);
		double maxY = (isFullHeight) ? frac(5) : frac(4);
		double minUV = frac(7);
		double maxUV = frac(9);
		double maxV = (isFullHeight) ? sprite.getMaxV() : sprite.getMaxV()-frac(0.25);
//		System.out.println("min: "+sprite.getMinU()+", "+sprite.getMinV());
//		System.out.println("max: "+sprite.getMaxU()+", "+sprite.getMaxV());

		//top
		buffer.vertex(adaptedX, topY, adaptedZ).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX, topY, adaptedZ+frac(2)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), topY, adaptedZ+frac(2)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), topY, adaptedZ).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();

		//north
		buffer.vertex(adaptedX, minY, northZ).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX, maxY, northZ).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), maxY, northZ).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), minY, northZ).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();

		//south
		buffer.vertex(adaptedX, minY, southZ).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), minY, southZ).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX+frac(2), maxY, southZ).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(adaptedX, maxY, southZ).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();

		//east
		buffer.vertex(eastX, minY, adaptedZ).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(eastX, minY, adaptedZ+frac(2)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(eastX, maxY, adaptedZ+frac(2)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(eastX, maxY, adaptedZ).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();

		//west
		buffer.vertex(westX, minY, adaptedZ).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(westX, maxY, adaptedZ).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(westX, maxY, adaptedZ+frac(2)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
		buffer.vertex(westX, minY, adaptedZ+frac(2)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1f, 1f, 1f, 1f).next();
	}

	public abstract Direction getFacing(T be);
	public abstract Sprite getLightupTexture(T block);
}
