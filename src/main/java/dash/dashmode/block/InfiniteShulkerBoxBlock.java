package dash.dashmode.block;

import dash.dashmode.blockentity.InfiniteShulkerBoxBlockEntity;
import dash.dashmode.utils.DashInventoryUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerLidCollisions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfiniteShulkerBoxBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;

    public InfiniteShulkerBoxBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new InfiniteShulkerBoxBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NamedScreenHandlerFactory) {

                if (checkAnimation(world, blockEntity, state, pos)) {
                    player.openHandledScreen(((NamedScreenHandlerFactory) blockEntity));
                    player.incrementStat(Stats.OPEN_SHULKER_BOX);
                    PiglinBrain.onGuardedBlockInteracted(player, true);
                }

                return ActionResult.CONSUME;
            } else {
                return ActionResult.PASS;
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && !world.isClient && !player.isCreative()) {
            ItemStack stack = new ItemStack(this);

            CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());

            if (!compoundTag.isEmpty()) {
                stack.getOrCreateTag().put("BlockEntityTag", compoundTag);
            }

            dropStack(world, pos, stack);
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag == null) {
            return;
        }

        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
        DashInventoryUtils.fromTag(compoundTag, stacks, 64);

        int i = 0;
        int j = 0;

        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                ++j;
                if (i <= 4) {
                    ++i;
                    MutableText mutableText = itemStack.getName().shallowCopy();
                    mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
                    tooltip.add(mutableText);
                }
            }
        }

        if (j - i > 0) {
            tooltip.add((new TranslatableText("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    private boolean checkAnimation(World world, BlockEntity entity, BlockState state, BlockPos pos) {
        if (entity instanceof InfiniteShulkerBoxBlockEntity && ((InfiniteShulkerBoxBlockEntity) entity).getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
            Direction direction = state.get(FACING);
            return world.isSpaceEmpty(ShulkerLidCollisions.getLidCollisionBox(pos, direction));
        } else {
            return true;
        }
    }
}
