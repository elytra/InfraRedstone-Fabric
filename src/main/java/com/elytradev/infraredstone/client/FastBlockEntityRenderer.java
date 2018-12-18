package com.elytradev.infraredstone.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.lwjgl.opengl.GL11;

//NOTE: This is adapted from Forge's FastTESR, remapped to Yarn names on 12/16/18. Any non-mapping changes are noted below.

public abstract class FastBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

	@Override
	public final void render(T be, double x, double y, double z, float partialTicks, int destroyStage)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBufferBuilder();
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (MinecraftClient.isAmbientOcclusionEnabled())
		{
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		}
		else
		{
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);

		renderFast(be, x, y, z, partialTicks, destroyStage, buffer);
		buffer.setOffset(0, 0, 0);

		tessellator.draw();

		GlStateManager.enableLighting(); //commented out until I can find it in 1.14
		super.render(be, x, y, z, partialTicks, destroyStage);
	}

	public abstract void renderFast(T te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer); //destoryStage no longer exists, and partial is now an int
}
