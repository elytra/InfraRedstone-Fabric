package com.elytradev.infraredstone.block;

import com.elytradev.infraredstone.InfraRedstone;
import com.elytradev.infraredstone.block.entity.*;
import com.elytradev.infraredstone.item.ModItems;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModBlocks {
	//TODO: properly implement
	public static final Block INFRA_REDSTONE = register(new InfraRedstoneCable(), InfraRedstone.inRedGroup);
	public static final Block IN_RED_SCAFFOLD = register(new InfraRedstoneScaffold(), InfraRedstone.inRedGroup);
	public static final Block IN_RED_BLOCK = register(new InfraRedstoneBlock(), InfraRedstone.inRedGroup);
	public static final Block DIODE = register(new DiodeBlock(), InfraRedstone.inRedGroup);
	public static final Block NOT_GATE = register(new NotGateBlock(), InfraRedstone.inRedGroup);
	public static final Block AND_GATE = register(new AndGateBlock(), InfraRedstone.inRedGroup);
//	public static final Block XOR_GATE = register(new InfraRedstoneCable(), InfraRedstone.inRedGroup);
//	public static final Block OSCILLATOR = register(new InfraRedstoneCable(), InfraRedstone.inRedGroup);
	public static final Block TRANSISTOR = register(new TransistorBlock(), InfraRedstone.inRedGroup);
//	public static final Block SHIFTER = register(new InfraRedstoneCable(), InfraRedstone.inRedGroup);
	public static final Block ENCODER = register(new EncoderBlock(), InfraRedstone.inRedGroup);

	public static final BlockEntityType<InfraRedstoneBlockEntity> IN_RED_BLOCK_BE = register("infra_redstone_block", InfraRedstoneBlockEntity::new);
	public static final BlockEntityType<DiodeBlockEntity> DIODE_BE = register("diode", DiodeBlockEntity::new);
	public static final BlockEntityType<NotGateBlockEntity> NOT_GATE_BE = register("not_gate", NotGateBlockEntity::new);
	public static final BlockEntityType<AndGateBlockEntity> AND_GATE_BE = register("and_gate", AndGateBlockEntity::new);
	public static final BlockEntityType<EncoderBlockEntity> ENCODER_BE = register("encoder", EncoderBlockEntity::new);
	public static final BlockEntityType<EncoderBlockEntity> TRANSISTOR_BE = register("transistor", TransistorBlockEntity::new);


	public static void init() {

	}

	public static Block register(NamedBlock block, ItemGroup tab) {
		Registry.register(Registry.BLOCK, "infraredstone:" + block.getName(), block.getBlock());
		BlockItem item = new BlockItem(block.getBlock(), new Item.Settings().itemGroup(tab));
		ModItems.register(block.getName(), item);
		return block.getBlock();
	}

	public static BlockEntityType register(String name, Supplier<BlockEntity> be) {
		return Registry.register(Registry.BLOCK_ENTITY, "infraredstone:" + name, BlockEntityType.Builder.create(be).method_11034(null));
	}
}
