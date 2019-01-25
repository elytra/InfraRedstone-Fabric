package com.elytradev.infraredstone.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * This interface allows a device to expose its InfraRedstoneSignal interface/s to other blocks.
 * Used in place of a Capability.
 */
public interface InfraRedstoneCapable {

	/**
	 * Use this in place of Forge's getCapability.
	 * @param inspectingFrom the direction your object is being inspected from, to avoid scanning in the wrong direction.
	 * @return the InfraRedstoneSignal for the specific side.
	 */
	InfraRedstoneSignal getInfraRedstoneHandler(Direction inspectingFrom);

	/**
	 * Use this in place of Forge's hasCapability.
	 * @param inspectingFrom the dirtection being tested for connectability.
	 * @return whether the object can be connected to from this direction.
	 */
	@Deprecated
	default boolean canConnectToSide(Direction inspectingFrom) { return false; }

	/**
	 * Gets whether this block's connection rules allows it to make a connection directly to the specified position.
	 * @param dest The destination to connect to
	 * @param dir  The nearest horizontal direction towards the destination
	 * @return True if this block's connection rules permit it to connect directly to the specified position, false otherwise.
	 */
	boolean canConnectIR(BlockPos dest, Direction dir);

}
