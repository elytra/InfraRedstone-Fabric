package com.elytradev.infraredstone;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;

public class InfraRedstoneClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
//		BlockEntityRendererRegistry.INSTANCE.register(ChorusConduitBlockEntity.class, new ChorusConduitRenderer());
	}
}
