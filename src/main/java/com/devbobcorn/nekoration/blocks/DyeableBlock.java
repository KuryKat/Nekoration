package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class DyeableBlock extends Block {
	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

	public DyeableBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR);
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? ActionResultType.SUCCESS
					: ActionResultType.PASS;
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		if (stack.getItem() instanceof DyeableBlockItem)
			return this.defaultBlockState().setValue(COLOR, DyeableBlockItem.getColor(stack).getNBTId());
		else if (stack.getItem() instanceof DyeableWoodenBlockItem)
			return this.defaultBlockState().setValue(COLOR, DyeableWoodenBlockItem.getColor(stack).getNBTId());
		return this.defaultBlockState();
    }
}