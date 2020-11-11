package dash.dashmode.block;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JarOfKeepingBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0.0F, 3, 13, 16, 13);
    private final int breakChance;
    private final int catchChance;

    public JarOfKeepingBlock(Settings settings, boolean isEnhanced) {
        super(settings.dropsNothing());

        // can stain for near 30 minutes
        breakChance = isEnhanced ? -1 : 20 * 60 * 30;
        catchChance = isEnhanced ? -1 : 10;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new JarOfKeepingBlockEntity(breakChance, catchChance);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof JarOfKeepingBlockEntity)) {
            return;
        }

        if (!world.isClient() && !player.isCreative()) {
            JarOfKeepingBlockEntity entity = (JarOfKeepingBlockEntity) blockEntity;
            ItemStack stack = asItem().getDefaultStack();
            stack.putSubTag(JarOfKeepingBlockEntity.BlockItemTag, entity.toTag(new CompoundTag()));

            dropStack(world, pos, stack);
        }
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return this.asItem().getDefaultStack();
    }
}
