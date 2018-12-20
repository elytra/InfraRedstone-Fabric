package com.elytradev.infraredstone.client;

import net.fabricmc.fabric.client.texture.SpriteRegistry;
import net.fabricmc.fabric.events.client.SpriteEvent;
import net.minecraft.util.Identifier;

public class SpriteProvider implements SpriteEvent.Provider {
	@Override
	public void registerSprites(SpriteRegistry registry) {
		registry.register(new Identifier("infraredstone:block/diode_glow"));
		registry.register(new Identifier("infraredstone:block/not_gate_glow"));
		registry.register(new Identifier("infraredstone:block/not_gate_glow_in"));
		registry.register(new Identifier("infraredstone:block/not_gate_glow_out"));
		registry.register(new Identifier("infraredstone:block/encoder_glow"));
	}
}
