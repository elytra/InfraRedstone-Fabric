package com.elytradev.infraredstone;

import com.elytradev.infraredstone.container.OscillatorContainerGui;
import com.elytradev.infraredstone.block.entity.*;
import com.elytradev.infraredstone.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.gui.GuiProviderRegistry;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.events.client.SpriteEvent;
import net.minecraft.util.math.BlockPos;

public class InfraRedstoneClient implements ClientModInitializer {
	private SpriteProvider provider = new SpriteProvider();

	@Override
	public void onInitializeClient() {
		SpriteEvent.PROVIDE.register(provider);
		BlockEntityRendererRegistry.INSTANCE.register(InfraRedstoneBlockEntity.class, new InfraRedstoneBlockRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(DiodeBlockEntity.class, new DiodeRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(NotGateBlockEntity.class, new NotGateRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(AndGateBlockEntity.class, new AndGateRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(XorGateBlockEntity.class, new XorGateRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(OscillatorBlockEntity.class, new OscillatorRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(TransistorBlockEntity.class, new TransistorRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(ShifterBlockEntity.class, new ShifterRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(EncoderBlockEntity.class, new EncoderRenderer());

		GuiProviderRegistry.INSTANCE.registerFactory(InfraRedstone.OSCILLATOR_CONTAINER, (identifier, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new OscillatorContainerGui(pos, player);
		});
	}

}

