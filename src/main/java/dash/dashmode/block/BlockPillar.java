package dash.dashmode.block;

import dash.dashmode.blockentity.StackHolderBlockEntity;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockPillar extends BlockWithEntity {
    private static final VoxelShape shape = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 4.0D, 16),
            Block.createCuboidShape(4, 4, 4, 12, 12, 12),
            Block.createCuboidShape(2, 12, 2, 16, 16, 14)
    );

    public BlockPillar(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof UseBlockCallback) {
            return ((UseBlockCallback) blockEntity).interact(player, world, hand, hit);
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AttackEntityCallback) {
            ((AttackEntityCallback) blockEntity).interact(player, world, player.getActiveHand(), player, null);
        }

        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new StackHolderBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shape;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
