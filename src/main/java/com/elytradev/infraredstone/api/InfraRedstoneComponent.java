package com.elytradev.infraredstone.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface indicates that a block has some sort of InfraRedstone functionality.
 */
public interface InfraRedstoneComponent {

	/**
	 * See if
	 * @param world the current world.
	 * @param currentPos the position of this block.
	 * @param inspectingFrom the position of the block to be checked for connection.
	 * @return whether the two can connect.
	 */
	boolean canConnect(World world, BlockPos currentPos, BlockPos inspectingFrom);
}
