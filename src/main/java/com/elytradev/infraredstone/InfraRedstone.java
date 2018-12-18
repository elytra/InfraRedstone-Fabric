package com.elytradev.infraredstone;

import com.elytradev.infraredstone.block.ModBlocks;
import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
import com.elytradev.infraredstone.item.ModItems;
import com.elytradev.infraredstone.logic.InRedLogic;
import com.elytradev.infraredstone.util.CommonProxy;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.fabricmc.fabric.events.TickEvent;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InfraRedstone implements ModInitializer {

	public static final ItemGroup inRedGroup = ItemGroup.REDSTONE;
	public static CommonProxy proxy;

	//public static final Block CHORUS_CONDUIT = register("chorus_conduit", new ChorusConduitBlock(FabricBlockSettings.create(Material.GLASS).setStrength(3.0F, 3.0F).setLuminance(15).build()), ItemGroup.MISC);
	//public static final Item FLIPPERS = register("flippers", new ArmorItem(ArmorMaterials.TURTLE, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT)));
	//public static final Item PRISMARINE_ROD = register("prismarine_rod", new Item(new Item.Settings().itemGroup(ItemGroup.MISC)));
	//public static BlockEntityType<ChorusConduitBlockEntity> CHORUS_CONDUIT_BE = register("chorus_conduit", ChorusConduitBlockEntity::new);

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModItems.init();
		TickEvent.SERVER.register(InRedLogic.onServerTick);
	}
}
