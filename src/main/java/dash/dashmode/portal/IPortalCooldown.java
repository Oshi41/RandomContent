package dash.dashmode.portal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface IPortalCooldown {
    /**
     * Settings current dimension portal ticks
     *
     * @param id
     * @param ticks
     */
    void setTickInPortal(RegistryKey<World> id, int ticks);

    /**
     * Gets ticks in portal from/to current dimension
     *
     * @param id
     * @return
     */
    int getTickInPortal(RegistryKey<World> id);

    /**
     * Gets latest portal pose from current dimension
     *
     * @param id
     * @return
     */
    BlockPos getLatestPortalPos(RegistryKey<World> id);

    /**
     * Applies current portal pos for dimension
     *
     * @param id
     * @param pos
     */
    void setLastPortalPos(RegistryKey<World> id, BlockPos pos);

    /**
     * Applies cooldown for current portal
     *
     * @param id
     * @param ticks
     */
    void setCooldown(RegistryKey<World> id, int ticks);

    /**
     * Gets cooldown from portal id
     *
     * @param id
     * @return
     */
    int getCoolDown(RegistryKey<World> id);

    /**
     * Copy from prev
     *
     * @param source
     */
    void copy(IPortalCooldown source);
}
