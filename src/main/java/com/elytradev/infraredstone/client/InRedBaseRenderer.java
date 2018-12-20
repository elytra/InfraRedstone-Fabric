package com.elytradev.infraredstone.client;

import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
import com.elytradev.infraredstone.util.Torch;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
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
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		this.method_3570(true);
		Sprite sprite = getLightupTexture((T) be);
		buffer.begin(GL11.GL_QUADS, VertexFormats.field_1586);
		if (sprite!=null) renderTopFace(buffer, sprite, getFacing((T)be));
		if (torches != null && torches.length != 0) {
			for (Torch torch : torches) {
				Sprite light = (torch.isLit) ? MinecraftClient.getInstance().getSpriteAtlas().getSprite("infraredstone:blocks/lights") : null;
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
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
			case SOUTH:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
			case WEST:
				buffer.vertex(frac(0), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(0), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMinV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(16)).texture(sprite.getMaxU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				buffer.vertex(frac(16), faceHeight, frac(0)).texture(sprite.getMinU(), sprite.getMaxV()).texture(240, 240).color(1F, 1F, 1F, 1F).next();
				break;
		}
	}

	public void renderLight(BufferBuilder buffer, Sprite tex, Double cornerX, Double cornerZ, boolean isFullHeight, Direction facing) {
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
		double maxY = (isFullHeight) ? frac(5) : frac(4);
		double minUV = 7;
		double maxUV = 9;
		double maxV = (isFullHeight) ? 9 : 8;

		//top
		buffer.vertex(adaptedX, topY, adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX, topY, adaptedZ+frac(2)).texture(tex.getU(minUV), tex.getV(maxUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), topY, adaptedZ+frac(2)).texture(tex.getU(maxUV), tex.getV(maxUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), topY, adaptedZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();

		//north
		buffer.vertex(adaptedX, frac(3), northZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), frac(3), northZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), maxY, northZ).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX, maxY, northZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();

		//south
		buffer.vertex(adaptedX, frac(3), southZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), frac(3), southZ).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX+frac(2), maxY, southZ).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(adaptedX, maxY, southZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();

		//east
		buffer.vertex(eastX, frac(3), adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(eastX, frac(3), adaptedZ+frac(2)).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(eastX, maxY, adaptedZ+frac(2)).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(eastX, maxY, adaptedZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();

		//west
		buffer.vertex(westX, frac(3), adaptedZ).texture(tex.getU(minUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(westX, frac(3), adaptedZ+frac(2)).texture(tex.getU(maxUV), tex.getV(minUV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(westX, maxY, adaptedZ+frac(2)).texture(tex.getU(maxUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
		buffer.vertex(westX, maxY, adaptedZ).texture(tex.getU(minUV), tex.getV(maxV)).color(1f, 1f, 1f, 1f).texture(240, 240).next();
	}

	public abstract Direction getFacing(T be);
	public abstract Sprite getLightupTexture(T block);
}
