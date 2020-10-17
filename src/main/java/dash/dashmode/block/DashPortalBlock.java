package dash.dashmode.block;

import dash.dashmode.portal.IPortalCooldown;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class DashPortalBlock extends Block {
    private final Supplier<RegistryKey<World>> to;

    public DashPortalBlock(Settings settings, Supplier<RegistryKey<World>> to) {
        super(settings.noCollision());
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
                cooldown.setCooldown(worldRegistryKey, cooldown.getCooldown(worldRegistryKey) + 4);
            }
        }
    }
}
