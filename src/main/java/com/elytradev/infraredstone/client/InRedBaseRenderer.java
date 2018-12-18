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

import static prospector.silk.util.RenderUtil.frac;

public abstract class InRedBaseRenderer<T extends IRComponentBlockEntity> extends BlockEntityRenderer {
	protected Torch[] torches = new Torch[]{};

	@Override
	public void render(BlockEntity be, double x, double y, double z, float partialTicks, int destroyStage) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBufferBuilder();
		buffer.setOffset(x, y, z);
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		Sprite sprite = getLightupTexture((T) be);
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
		if (sprite!=null) renderTopFace(buffer, sprite, getFacing((T)be));
		if (torches != null && torches.length != 0) {
			for (Torch torch : torches) {
				Sprite light = (torch.isLit) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:blocks/lights") : null;
				if (light != null) renderLight(buffer, light, torch.cornerX, torch.cornerZ, torch.isFullHeight, getFacing((T)be));
			}
		}
		buffer.setOffset(0.0, 0.0, 0.0);
		tessellator.draw();
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		super.render(be, x, y, z, partialTicks, destroyStage);
	}

	public void renderTopFace(BufferBuilder buffer, Sprite sprite, Direction facing) {
		double faceHeight = frac(3);
		faceHeight += 0.002; //Far enough to not z-fight. Hopefully.

		switch(facing) {
			case NORTH:
			default:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				break;
			case EAST:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				break;
			case SOUTH:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				break;
			case WEST:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).color(1F, 1F, 1F, 1F).next();
				break;
		}
	}

	public void renderLight(BufferBuilder buffer, Sprite tex, Double cornerX, Double cornerZ, boolean isFullHeight, Direction facing) {
		double adaptedX = cornerX;
		double adaptedZ = cornerZ;
		switch(facing) {
			case EAST:
				adaptedX = -1*cornerZ + 14/16d;
				adaptedZ = cornerX;
				break;
			case SOUTH:
				adaptedZ = -1*adaptedZ + 14/16d;
				adaptedX = -1*adaptedX + 14/16d;
				break;
			case WEST:
				adaptedX = cornerZ;
				adaptedZ = -1*cornerX + 14/16d;
				break;
			default:
				break;
		}
		double eastX = adaptedX-0.002;
		double westX = adaptedX+0.127;
		double northZ = adaptedZ-0.002;
		double southZ = adaptedZ+0.127;
		double topY = (isFullHeight) ? 0.3145 : 0.252;
		double maxY = (isFullHeight) ? 5/16d : 1/4d;
		double minUV = 7;
		double maxUV = 9;
		double maxV = (isFullHeight) ? 9 : 8;

		//top
		buffer.vertex(adaptedX, topY, adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, topY, adaptedZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, topY, adaptedZ+2/16d).texture(tex.getU(maxUV), tex.getV(maxUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX, topY, adaptedZ+2/16d).texture(tex.getU(minUV), tex.getV(maxUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();

		//north
		buffer.vertex(adaptedX, 3/16d, northZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, 3/16d, northZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, maxY, northZ).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX, maxY, northZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();

		//south
		buffer.vertex(adaptedX, 3/16d, southZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, 3/16d, southZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX+2/16d, maxY, southZ).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(adaptedX, maxY, southZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();

		//east
		buffer.vertex(eastX, 3/16d, adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(eastX, 3/16d, adaptedZ+2/16d).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(eastX, maxY, adaptedZ+2/16d).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(eastX, maxY, adaptedZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();

		//west
		buffer.vertex(westX, 3/16d, adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(westX, 3/16d, adaptedZ+2/16d).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(westX, maxY, adaptedZ+2/16d).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
		buffer.vertex(westX, maxY, adaptedZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f)/*.brightness(240, 240, 240, 240)*/.next();
	}

	public abstract Direction getFacing(T be);
	public abstract Sprite getLightupTexture(T block);
}
