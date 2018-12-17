package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public abstract class InRedRenderBase<T extends IRComponentBlockEntity> extends FastBlockEntityRenderer {
	protected Torch[] torches = new Torch[]{};

	@Override
	public void renderFast(BlockEntity te, double x, double y, double z, float partialTicks, int partial, VertexBuffer buffer) {
		buffer.setOffset(x, y, z);
		Sprite sprite = getLightupTexture((T) te);
		if (sprite!=null) renderTopFace(buffer, sprite, getFacing((T)te));
		if (torches != null && torches.length != 0) {
			for (Torch torch : torches) {
				Sprite light = (torch.isLit) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:blocks/lights") : null;
				if (light != null) renderLight(buffer, light, torch.cornerX, torch.cornerZ, torch.isFullHeight, getFacing((T)te));
			}
		}
	}

	public void renderTopFace(VertexBuffer buffer, Sprite tex, Direction facing) {
		double faceHeight = 3/16.0;
		faceHeight += 0.002; //Far enough to not z-fight. Hopefully.

		switch(facing) {
			case NORTH:
			default:
				buffer.vertex(0, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(0, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				break;
			case EAST:
				buffer.vertex(0, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(0, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				break;
			case SOUTH:
				buffer.vertex(0, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(0, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMinV()).brightness(240, 240, 240, 240);
				break;
			case WEST:
				buffer.vertex(0, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMinV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 0).color(1f, 1f, 1f, 1f).texture(tex.getMaxU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(1, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMaxV()).brightness(240, 240, 240, 240);
				buffer.vertex(0, faceHeight, 1).color(1f, 1f, 1f, 1f).texture(tex.getMinU(), tex.getMinV()).brightness(240, 240, 240, 240);
				break;
		}
	}

	public void renderLight(VertexBuffer buffer, Sprite tex, Double cornerX, Double cornerZ, boolean isFullHeight, Direction facing) {
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
		buffer.vertex(adaptedX, topY, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, topY, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, topY, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(maxUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX, topY, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(maxUV)).brightness(240, 240, 240, 240);

		//north
		buffer.vertex(adaptedX, 3/16d, northZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, 3/16d, northZ).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, maxY, northZ).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(maxV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX, maxY, northZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(maxV)).brightness(240, 240, 240, 240);

		//south
		buffer.vertex(adaptedX, 3/16d, southZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, 3/16d, southZ).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX+2/16d, maxY, southZ).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(maxV)).brightness(240, 240, 240, 240);
		buffer.vertex(adaptedX, maxY, southZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(maxV)).brightness(240, 240, 240, 240);

		//east
		buffer.vertex(eastX, 3/16d, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(eastX, 3/16d, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(eastX, maxY, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(maxV)).brightness(240, 240, 240, 240);
		buffer.vertex(eastX, maxY, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(maxV)).brightness(240, 240, 240, 240);

		//west
		buffer.vertex(westX, 3/16d, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(westX, 3/16d, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(minUV)).brightness(240, 240, 240, 240);
		buffer.vertex(westX, maxY, adaptedZ+2/16d).color(1f, 1f, 1f, 1f).texture(tex.getU(maxUV), tex.getV(maxV)).brightness(240, 240, 240, 240);
		buffer.vertex(westX, maxY, adaptedZ).color(1f, 1f, 1f, 1f).texture(tex.getU(minUV), tex.getV(maxV)).brightness(240, 240, 240, 240);
	}

	public abstract Direction getFacing(T be);
	public abstract Sprite getLightupTexture(T block);
}
