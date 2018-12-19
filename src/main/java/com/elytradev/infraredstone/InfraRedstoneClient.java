package com.elytradev.infraredstone;

import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import com.elytradev.infraredstone.block.entity.EncoderBlockEntity;
import com.elytradev.infraredstone.client.DiodeRenderer;
import com.elytradev.infraredstone.client.EncoderRenderer;
import com.elytradev.infraredstone.client.SpriteProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.events.client.SpriteEvent;

public class InfraRedstoneClient implements ClientModInitializer {
	private SpriteProvider provider = new SpriteProvider();

	@Override
	public void onInitializeClient() {
		SpriteEvent.PROVIDE.register(provider);
		BlockEntityRendererRegistry.INSTANCE.register(DiodeBlockEntity.class, new DiodeRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(EncoderBlockEntity.class, new EncoderRenderer());
	}

}

