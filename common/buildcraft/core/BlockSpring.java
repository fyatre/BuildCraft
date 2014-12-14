/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.core;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSpring extends Block {

	public static final Random rand = new Random();
	public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumSpring.class);

	public enum EnumSpring {

		WATER(5, -1, Blocks.water),
		OIL(6000, 8, null); // Set in BuildCraftEnergy
		public static final EnumSpring[] VALUES = values();
		public final int tickRate, chance;
		public Block liquidBlock;
		public boolean canGen = true;

		private EnumSpring(int tickRate, int chance, Block liquidBlock) {
			this.tickRate = tickRate;
			this.chance = chance;
			this.liquidBlock = liquidBlock;
		}

		public static EnumSpring fromState(IBlockState state) {
			return (EnumSpring) state.getValue(TYPE);
		}
	}

	public BlockSpring() {
		super(Material.rock);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(TYPE, EnumSpring.WATER));

		setStepSound(Block.soundTypeStone);

		disableStats();
		setTickRandomly(true);
		setCreativeTab(CreativeTabBuildCraft.BLOCKS.get());
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (EnumSpring type : EnumSpring.VALUES) {
			list.add(new ItemStack(this, 1, type.ordinal()));
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		assertSpring(world, pos, state);
	}

//	@Override
//	public void onNeighborBlockChange(World world, int x, int y, int z, int blockid) {
//		assertSpring(world, x, y, z);
//	}
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		world.scheduleUpdate(pos, this, EnumSpring.fromState(state).tickRate);
	}

	private void assertSpring(World world, BlockPos pos, IBlockState state) {
		EnumSpring spring = EnumSpring.fromState(state);
		world.scheduleUpdate(pos, this, spring.tickRate);
		if (!spring.canGen || spring.liquidBlock == null) {
			return;
		}
		if (!world.isAirBlock(pos.offsetUp())) {
			return;
		}
		if (spring.chance != -1 && rand.nextInt(spring.chance) != 0) {
			return;
		}
		world.setBlockState(pos.offsetUp(), spring.liquidBlock.getDefaultState());
	}

	// TODO: 1.7.10 - Prevents updates on chunk generation
	//@Override
	//public boolean func_149698_L() {
	//	return false;
	//}
}
