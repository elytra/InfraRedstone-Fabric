package com.elytradev.infraredstone;

import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import com.elytradev.infraredstone.client.DiodeRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class InfraRedstoneClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MinecraftClient.getInstance().getSpriteAtlas().addSpriteToLoad(MinecraftClient.getInstance().getResourceManager(), new Identifier("infraredstone:block/diode_glow"));
		BlockEntityRendererRegistry.INSTANCE.register(DiodeBlockEntity.class, new DiodeRender());
	}
}
