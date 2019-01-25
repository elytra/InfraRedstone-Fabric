package com.elytradev.infraredstone.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.elytradev.infraredstone.api.InfraRedstoneCapable;
import com.elytradev.infraredstone.api.InfraRedstoneComponent;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import com.elytradev.infraredstone.api.InfraRedstoneWire;
import com.elytradev.infraredstone.api.SimpleInfraRedstoneSignal;
import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
public class InRedLogic {
	public static final int INTER_IR_TICKS = 1; //TODO: This might get a config, but it's hard to explain.
	private static int tickCount = 0;
	
	/** The maximum depth of wires to search through. More depth means more time spent every single time a logic gate
	 * changes. Where zero is the block immediately in front of a logic tile, 0 through MAX_DEPTH will be searched,
	 * inclusive, along every connected branch.
	 */
	public static int MAX_DEPTH = 63; //TODO: Move this into config once there's a config
	

	public static Consumer<MinecraftServer> onServerTick = server -> {
		tickCount++;
		if (tickCount > INTER_IR_TICKS)
			tickCount = 0;
	};

	public static boolean isIRTick() {
		return (tickCount == 0);
	}

	/**
	 * Searches for the highest IR signal which can be delivered to the indicated
	 * block face.
	 * 
	 * @param world
	 *            The world the device resides in
	 * @param device
	 *            The location of the device
	 * @param dir
	 *            The direction *from* the device *towards* where the prospective
	 *            signal is coming from.
	 * @return The IR value, or the redstone level if no IR is present, or 0 if
	 *         nothing is present.
	 */
	public static int findIRValue(World world, BlockPos device, Direction dir) {
		BlockPos initialPos = device.offset(dir);
		if (!checkCandidacy(world, initialPos, dir)) {
			BlockPos up = initialPos.up();
			if (checkCandidacy(world, up, dir)) {
				initialPos = up;
			} else {
				BlockPos down = initialPos.down();
				if (checkCandidacy(world, down, dir)) {
					initialPos = down;
				} else {
					return (world.getEmittedRedstonePower(initialPos, dir) != 0) ? 1 : 0;
				}
			}
		}

		if (world.isAir(initialPos)) return 0;
		BlockState initialState = world.getBlockState(initialPos);
		//System.out.println(initialState);
		if (initialState.getBlock() instanceof InfraRedstoneWire) {
			// Search!
			return wireSearch(world, device, dir);
		}

		if (initialState.getBlock() instanceof SimpleInfraRedstoneSignal) {
			// We have a simple IR block behind us. Excellent! Don't search, just get its
			// value.
			return ((SimpleInfraRedstoneSignal) initialState.getBlock()).getSignalValue(world, initialPos, initialState, dir.getOpposite());
		}

		BlockEntity be = world.getBlockEntity(initialPos);
		//System.out.println(be);
		if (be instanceof InfraRedstoneCapable && ((InfraRedstoneCapable)be).canConnectIR(initialPos, dir.getOpposite())) {
			// We have a full IR tile behind us. Fantastic! Don't search, just get its
			// value.
			InfraRedstoneSignal cap = ((InfraRedstoneCapable)be).getInfraRedstoneHandler(dir.getOpposite());
			return cap.getSignalValue();
		}

		// Oh. Okay. No wires or machines. Well, return the vanilla redstone value as
		// the bottom bit here and call it a day.
		return (world.getEmittedRedstonePower(initialPos, dir) != 0) ? 1 : 0;
	}

	public static boolean checkCandidacy(World world, BlockPos pos, Direction side) {
		if (world.isAir(pos)) return false;

		BlockState state = world.getBlockState(pos);
		BlockState secondState = world.getBlockState(pos.offset(side.getOpposite()));

		if (state.getBlock() instanceof InfraRedstoneComponent && secondState.getBlock() instanceof InfraRedstoneComponent) {
			InfraRedstoneComponent comp = (InfraRedstoneComponent)state.getBlock();
			InfraRedstoneComponent secondComp = (InfraRedstoneComponent)secondState.getBlock();

			if (comp.canConnect(world, pos, pos.offset(side.getOpposite())) && secondComp.canConnect(world, pos.offset(side), pos)) {
				BlockEntity be = world.getBlockEntity(pos);
				if (be == null) return true;

				if (be instanceof InfraRedstoneCapable) {
					System.out.println(((InfraRedstoneCapable) be).canConnectToSide(side));
					return (((InfraRedstoneCapable) be).canConnectToSide(side));
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	@Deprecated
	public static boolean isSideSolid(World world, BlockPos pos, Direction dir) {
		//return Block.isFaceFullCube(world.getBlockState(pos).getBoundingShape(world, pos), dir);
		return world.getBlockState(pos).isSimpleFullBlock(world, pos);
	}

	private static int wireSearch(World world, BlockPos device, Direction dir) {
		int depth = 0;
		Set<Endpoint> rejected = new HashSet<Endpoint>();
		Set<BlockPos> traversed = new HashSet<BlockPos>();
		List<Endpoint> members = new ArrayList<>();
		List<Endpoint> queue = new ArrayList<>();
		List<Endpoint> next = new ArrayList<>();

		queue.add(new Endpoint(device.offset(dir), dir.getOpposite()));

		if (device.getY() < 255 && !isSideSolid(world, device.offset(Direction.UP), Direction.DOWN)) queue.add(new Endpoint(device.offset(dir).up(), dir.getOpposite()));
		if (device.getY() > 0 && !isSideSolid(world, device.offset(dir), dir.getOpposite())) queue.add(new Endpoint(device.offset(dir).down(), dir.getOpposite()));

		while (!queue.isEmpty() || !next.isEmpty()) {
			if (queue.isEmpty()) {
				depth++;
				if (depth > 63) return 0; // We've searched too far, there's no signal in range.
				queue.addAll(next);
				next.clear();
			}

			Endpoint cur = queue.remove(0);

			if (world.isAir(cur.pos)) continue;
			BlockState state = world.getBlockState(cur.pos);

			Block block = state.getBlock();
			if (block instanceof InfraRedstoneWire) {
				traversed.add(cur.pos);
				for (Direction facing : Direction.values()) {
					if (facing == cur.facing) continue; // Don't try to bounce back to the block we came from
					BlockPos offset = cur.pos.offset(facing);
					if (facing != Direction.UP && facing != Direction.DOWN) {
						if (offset.getY() < 255 && !isSideSolid(world, cur.pos.up(), Direction.DOWN))
							if (checkCandidacy(world, cur.pos, Direction.UP)) checkAdd(new Endpoint(offset.up(), facing.getOpposite()), next, traversed, rejected);
						if (offset.getY() > 0 && !isSideSolid(world, offset, facing.getOpposite()) && !(world.getBlockState(offset.down()).getBlock() instanceof InfraRedstoneComponent))
							if (checkCandidacy(world, cur.pos, Direction.DOWN)) checkAdd(new Endpoint(offset.down(), facing.getOpposite()), next, traversed, rejected);
					}
					if (checkCandidacy(world, cur.pos, facing.getOpposite())) checkAdd(new Endpoint(offset, facing.getOpposite()), next, traversed, rejected);
				}

				continue;
			}

			Integer rightHere = valueDirectlyAt(world, cur.pos, cur.facing);
			if (rightHere != null) {
				members.add(cur);
				rejected.add(cur);
				continue;
			}
		}

		// Grab the bitwise OR of all signals
		int result = 0;
		for (Endpoint cur : members) {
			int val = valueDirectlyAt(world, cur.pos, cur.facing);
			result |= val;
		}
		return result;
	}
	
	/** Searches forward to find endpoints connected to the supplied Endpoint.
	 * 
	 * <p>The safest way to use this is to go through the following steps:
	 * 
	 *   <li>A tile's output has changed. It does a forwardSearch and uses it to notify its neighbors that its
	 *       signal contribution has changed (and the value of the new signal).
	 *       
	 *   <li>The receiving tile(s) checks to see if the signal could possibly alter the value of its cached input: If the
	 *       new signal is equal to the cached signal, no action is taken.
	 *       
	 *   <li>Having decided it may have to change, it does a forwardSearch *backwards* to determine *its* list of
	 *       reachable contributors, which may be different from the original forward search due to length limitations.
	 *       
	 *   <li>The discovered endpoints are queried for their output signals and OR'd to determine the new value of the
	 *       input signal.
	 *       
	 *   <li>IF this input signal value is changed, then trigger a recalculation of the output signal, and potentially a
	 *       network sync.
	 * 
	 * <p>Reminder: An endpoint's direction is from the perspective of its own block. Let's say we're describing the
	 *    connection between a NOT gate, to the west; and a wire segment, to the east. The endpoint describing the NOT
	 *    gate's output to the wire would be EAST. The endpoint describing the wire's connection to the NOT gate would
	 *    be WEST.
	 */
	public static List<Endpoint> wireSearch(World world, Endpoint point) {
		List<Endpoint> result = new ArrayList<>();
		
		/** How far we've traveled from the initial endpoint. If this reaches MAX_DEPTH, we'll stop searching since signals eventually die.*/
		int depth = 0;
		/** These prospective endpoints have been rejected either because the blocks mentioned aren't inred endpoints,
		 * or because connectivity rules forbid connecting in that direction. */
		Set<Endpoint> rejected = new HashSet<Endpoint>();
		/** These blocks have already been considered as a "connect-from", meaning all directions have been searched
		 * and we can skip considering these blocks entirely.
		 */
		Set<BlockPos> traversed = new HashSet<BlockPos>();
		/** The set of possible next connections that we're considering at *this* depth */
		List<Endpoint> queue = new ArrayList<>();
		/** The set of connections that we'll consider at the *next* depth */
		List<Endpoint> next = new ArrayList<>();
		
		queue.add(new Endpoint(point.pos.offset(point.facing), point.facing.getOpposite()));
		if (point.pos.getY() < 255) queue.add(new Endpoint(point.pos.offset(point.facing).up(), point.facing.getOpposite()));
		if (point.pos.getY() > 0) queue.add(new Endpoint(point.pos.offset(point.facing).down(), point.facing.getOpposite()));
		
		
		while (!queue.isEmpty() || !next.isEmpty()) {
			if (queue.isEmpty()) {
				depth++;
				if (depth > 63) return result; // We've searched too far, this is all the Endpoints we're going to get
				queue.addAll(next);
				next.clear();
			}

			Endpoint cur = queue.remove(0);
			
			traversed.add(cur.pos);
			if (!isParticipant(world, cur.pos)) {
				rejected.add(cur);
				continue;
			}
			
			//Search diagonally in each direction
			for(Direction facing : Direction.values()) {
				BlockPos dest = cur.pos.offset(facing).offset(Direction.UP);
				if (canConnectBothWays(world, cur.pos, dest, facing)) {
					checkAdd(new Endpoint(dest, facing.getOpposite()), next, traversed, rejected);
				}
				
				dest = cur.pos.offset(facing);
				if (canConnectBothWays(world, cur.pos, dest, facing)) {
					checkAdd(new Endpoint(dest, facing.getOpposite()), next, traversed, rejected);
				}
				
				dest = cur.pos.offset(facing).offset(Direction.DOWN);
				if (canConnectBothWays(world, cur.pos, dest, facing)) {
					checkAdd(new Endpoint(dest, facing.getOpposite()), next, traversed, rejected);
				}
			}
			//Search straight up and down
			BlockPos dest = cur.pos.offset(Direction.UP);
			if (canConnectBothWays(world, cur.pos, dest, Direction.UP)) {
				checkAdd(new Endpoint(dest, Direction.UP), next, traversed, rejected);
			}
			
			dest = cur.pos.offset(Direction.DOWN);
			if (canConnectBothWays(world, cur.pos, dest, Direction.DOWN)) {
				checkAdd(new Endpoint(dest, Direction.DOWN), next, traversed, rejected);
			}
		}
		
		return result;
	}
	
	
	/** Returns true if this block participates in inred logic */
	private static boolean isParticipant(BlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof SimpleInfraRedstoneSignal) return true;
		BlockEntity be = world.getBlockEntity(pos);
		return be instanceof InfraRedstoneCapable;
	}
	
	private static boolean canConnectBothWays(BlockView world, BlockPos src, BlockPos dest, Direction horizontalDir) {
		return canConnectOneWay(world, src, dest, horizontalDir) && canConnectOneWay(world, dest, src, horizontalDir.getOpposite());
	}
	
	private static boolean canConnectOneWay(BlockView world, BlockPos src, BlockPos dest, Direction dir) {
		BlockState state = world.getBlockState(src);
		Block block = state.getBlock();
		if (block instanceof SimpleInfraRedstoneSignal) {
			return ((SimpleInfraRedstoneSignal)block).canConnectIR(world, src, state, dest, dir);
		} else {
			BlockEntity be = world.getBlockEntity(src);
			if (be instanceof InfraRedstoneCapable) {
				return ((InfraRedstoneCapable)be).canConnectIR(dest, dir);
			} else return false;
		}
	}
	

	private static void checkAdd(Endpoint endpoint, List<Endpoint> next, Set<BlockPos> traversed, Set<Endpoint> rejected) {
		if (traversed.contains(endpoint.pos)) return;
		if (rejected.contains(endpoint)) return;
		next.add(endpoint);
	}

	public static Integer valueDirectlyAt(World world, BlockPos pos, Direction dir) {
		if (world.isAir(pos)) return null;
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof InfraRedstoneWire) return null; // wires don't carry power directly
		if (block instanceof SimpleInfraRedstoneSignal) {
			return ((SimpleInfraRedstoneSignal)block).getSignalValue(world, pos, state, dir);
		}
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InfraRedstoneCapable && ((InfraRedstoneCapable)be).canConnectToSide(dir)) {
			return ((InfraRedstoneCapable)be).getInfraRedstoneHandler(dir).getSignalValue();
		}
		return null;
	}

	private static class Endpoint {
		BlockPos pos;
		Direction facing;

		public Endpoint(BlockPos pos, Direction facing) {
			this.pos = pos;
			this.facing = facing;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(pos, facing);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
			if (!(other instanceof Endpoint)) return false;
			Endpoint otherEnd = (Endpoint) other;
			return Objects.equal(pos, otherEnd.pos) && Objects.equal(facing, otherEnd.facing);
		}

		@Override
		public String toString() {
			return "{x:" + pos.getX() + ", y:" + pos.getY() + ", z:" + pos.getZ() + ", dir:" + facing + "}";
		}
		
		public void notifyOfNewValue(World world, int signal) {
			//TODO: Work notifications into interface
		}
		
		public int getSignalValue(BlockView world) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof SimpleInfraRedstoneSignal) {
				return ((SimpleInfraRedstoneSignal)state.getBlock()).getSignalValue(world, pos, state, facing);
			} else {
				BlockEntity entity = world.getBlockEntity(pos);
				if (entity instanceof InfraRedstoneCapable) {
					return ((InfraRedstoneCapable)entity).getInfraRedstoneHandler(facing).getSignalValue();
				}
			}
			
			return 0; //We made a mistake somehow. This isn't a block that does signal at all. Fallback gracefully to zero.
		}
	}
	
	public static class Connection {
		BlockPos from;
		BlockPos to;
		Direction planarFacing;
	}
}
