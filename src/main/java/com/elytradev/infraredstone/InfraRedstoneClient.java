package com.elytradev.infraredstone;

import com.elytradev.infraredstone.block.entity.*;
import com.elytradev.infraredstone.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.events.client.SpriteEvent;

public class InfraRedstoneClient implements ClientModInitializer {
	private SpriteProvider provider = new SpriteProvider();

	@Override
	public void onInitializeClient() {
		SpriteEvent.PROVIDE.register(provider);
		BlockEntityRendererRegistry.INSTANCE.register(InfraRedstoneBlockEntity.class, new InfraRedstoneBlockRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(DiodeBlockEntity.class, new DiodeRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(NotGateBlockEntity.class, new NotGateRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(AndGateBlockEntity.class, new AndGateRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(TransistorBlockEntity.class, new TransistorRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(EncoderBlockEntity.class, new EncoderRenderer());
	}

}

