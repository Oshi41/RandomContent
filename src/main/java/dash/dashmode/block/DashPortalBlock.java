package dash.dashmode.block;

import dash.dashmode.portal.IPortalCooldown;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.registry.DashBlocks;
import dash.dashmode.registry.DashDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;
import java.util.function.Supplier;

public class DashPortalBlock extends Block {
    private final Supplier<RegistryKey<World>> to;

    public DashPortalBlock(Settings settings, Supplier<RegistryKey<World>> to) {
        super(settings.noCollision().ticksRandomly());
        this.to = to;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.hasVehicle()
                && !entity.hasPassengers()
                && entity.canUsePortals()
                && to != null
                && entity instanceof IPortalCooldown) {
            RegistryKey<World> worldRegistryKey = to.get();

            if (worldRegistryKey != null) {
                IPortalCooldown cooldown = (IPortalCooldown) entity;
                cooldown.setTickInPortal(worldRegistryKey, cooldown.getTickInPortal(worldRegistryKey) + 4);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }

        if (to != null) {
            RegistryKey<World> key = to.get();
            if (key != null) {
                IPortalDesciption portal = DashDimensions.getPortal(key);
                if (portal != null) {
                    BlockPattern.Result result = portal.testWorkingPortal(world, pos);
                    if (result != null) {
                        world.breakBlock(pos, true);
                    }
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        world.addParticle(ParticleTypes.PORTAL,
                pos.getX() + random.nextDouble() - random.nextDouble(),
                pos.getY() + random.nextDouble() - random.nextDouble(),
                pos.getZ() + random.nextDouble() - random.nextDouble(),
                random.nextFloat() - random.nextFloat(),
                random.nextFloat() - random.nextFloat(),
                random.nextFloat() - random.nextFloat());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.down());
        return down.isOf(this) || down.isOf(DashBlocks.PaperBookshelf);
    }
}
