package com.elytradev.infraredstone.api;

import net.minecraft.util.math.Direction;

/**
 * This interface allows a device to expose its IInfraRedstone interface/s to other blocks.
 */
public interface InfraRedstoneCapable {

	/**
	 * @param inspectingFrom the direction your object is being inspected from, to avoid scanning in the wrong direction.
	 * @return the IInfraRedstone for the specific side.
	 */
	IInfraRedstone getInfraRedstoneHandler(Direction inspectingFrom);

	/**
	 *
	 * @param inspectingFrom the dirtection being tested for connectability.
	 * @return whether the object can be connected to from this direction.
	 */
	boolean canConnectToSide(Direction inspectingFrom);

}
