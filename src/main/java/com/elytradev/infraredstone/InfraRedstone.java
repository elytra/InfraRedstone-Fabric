package com.elytradev.infraredstone;

import com.elytradev.infraredstone.container.OscillatorContainer;
import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.item.ModItems;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.util.CommonProxy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.events.TickEvent;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class InfraRedstone implements ModInitializer {

	public static final ItemGroup inRedGroup = FabricItemGroupBuilder.build(new Identifier("infraredstone:infra_redstone_tab"), () -> new ItemStack(ModBlocks.INFRA_REDSTONE));
	public static CommonProxy proxy;

	public static final Identifier OSCILLATOR_CONTAINER = new Identifier("infraredstone:oscillator_container");

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModItems.init();
		TickEvent.SERVER.register(InRedLogic.onServerTick);
		//Registers a container factory that opens our example Container, this reads the block pos from the buffer
		ContainerProviderRegistry.INSTANCE.registerFactory(OSCILLATOR_CONTAINER, (syncId, suidentifier, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new OscillatorContainer(syncId, pos, player);
		});
	}
}
