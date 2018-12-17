package com.elytradev.infraredstone;

import com.elytradev.infraredstone.block.entity.DiodeBlockEntity;
import com.elytradev.infraredstone.client.DiodeRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.client.texture.SpriteRegistry;
import net.fabricmc.fabric.events.client.SpriteEvent;
import net.minecraft.util.Identifier;

public class InfraRedstoneClient implements ClientModInitializer {
	private SpriteProvider provider = new SpriteProvider();

	@Override
	public void onInitializeClient() {
		SpriteEvent.PROVIDE.register(provider);
		BlockEntityRendererRegistry.INSTANCE.register(DiodeBlockEntity.class, new DiodeRender());
	}

}

class SpriteProvider implements SpriteEvent.Provider {
	@Override
	public void registerSprites(SpriteRegistry registry) {
		registry.register(new Identifier("infraredstone:block/diode_glow"));
	}
}
