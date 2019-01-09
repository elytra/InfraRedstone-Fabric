package com.elytradev.infraredstone.util;

import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.ThreadTaskQueue;
import net.minecraft.util.math.BlockPos;

public class InfraRedstoneNetworking implements ModInitializer {

	public static final Identifier MODULE_SYNC = new Identifier("infraredstone:diode_sync");
	public static final Identifier MODULE_REQUEST = new Identifier("infraredstone:diode_request");
	public static final Identifier OSCILLATOR_SYNC = new Identifier("infraredstone:oscillator_sync");
	public static final Identifier OSCILLATOR_CHANGE = new Identifier("infraredstone:oscillator_change");

	@Override
	public void onInitialize() {
		CustomPayloadPacketRegistry.CLIENT.register(MODULE_SYNC, ((packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			CompoundTag tag = packetByteBuf.readCompoundTag();
			if (packetContext.getPlayer() != null && packetContext.getPlayer().getEntityWorld() != null) {
				BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
				if (be instanceof IRComponentBlockEntity && tag != null) {
					be.fromTag(tag);
				}
			}
		}));
		CustomPayloadPacketRegistry.SERVER.register(MODULE_REQUEST, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (be instanceof IRComponentBlockEntity) {
				syncModule((IRComponentBlockEntity) be, (ServerPlayerEntity) packetContext.getPlayer());
			}
		});
		CustomPayloadPacketRegistry.CLIENT.register(OSCILLATOR_SYNC, ((packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
			if (be instanceof OscillatorBlockEntity) {
				((OscillatorBlockEntity)be).maxRefreshTicks = packetByteBuf.readInt();
			}
		}));
		CustomPayloadPacketRegistry.SERVER.register(OSCILLATOR_CHANGE, ((packetContext, packetByteBuf) -> {
			ThreadTaskQueue queue = packetContext.getTaskQueue();
			BlockPos pos = packetByteBuf.readBlockPos();
			int change = packetByteBuf.readInt();
			queue.execute(() -> {
				BlockEntity be = packetContext.getPlayer().getEntityWorld().getBlockEntity(pos);
				if (be instanceof OscillatorBlockEntity) {
					((OscillatorBlockEntity)be).maxRefreshTicks += change;
					((OscillatorBlockEntity)be).setDelay();
					InfraRedstoneNetworking.syncOscillator((OscillatorBlockEntity)be, (ServerPlayerEntity)packetContext.getPlayer());
				}
			});
		}));
	}

	@Environment(EnvType.CLIENT)
	public static void syncModule(IRComponentBlockEntity module, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(module.getPos());
		buf.writeCompoundTag(module.toTag(new CompoundTag()));
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(MODULE_SYNC, buf));
	}
	@Environment(EnvType.CLIENT)
	public static void requestModule(IRComponentBlockEntity module) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(module.getPos());
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(MODULE_REQUEST, buf));
	}
	@Environment(EnvType.CLIENT)
	public static void syncOscillator(OscillatorBlockEntity oscillator, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(oscillator.getPos());
		buf.writeInt(oscillator.maxRefreshTicks);
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(OSCILLATOR_SYNC, buf));
	}
	@Environment(EnvType.CLIENT)
	public static void changeOscillator(OscillatorBlockEntity oscillator, int change) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(oscillator.getPos());
		buf.writeInt(change);
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(OSCILLATOR_CHANGE, buf));
	}


}
