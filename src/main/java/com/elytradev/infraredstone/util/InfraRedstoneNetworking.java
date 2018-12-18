package com.elytradev.infraredstone.util;

import com.elytradev.infraredstone.block.entity.IRComponentBlockEntity;
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
import net.minecraft.util.math.BlockPos;

public class InfraRedstoneNetworking implements ModInitializer {

	public static final Identifier MODULE_SYNC = new Identifier("infraredstone:diode_sync");
	public static final Identifier MODULE_REQUEST = new Identifier("infraredstone:diode_request");

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
	}

	@Environment(EnvType.SERVER)
	public static void syncModule(IRComponentBlockEntity module, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(module.getPos());
		buf.writeCompoundTag(module.toTag(new CompoundTag()));
		player.networkHandler.sendPacket(new CustomPayloadClientPacket(MODULE_SYNC, buf));
	}
}
