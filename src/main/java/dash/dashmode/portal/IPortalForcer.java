package dash.dashmode.portal;

import net.minecraft.class_5459;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface IPortalForcer {
    /**
     * Trying to find or create portal
     *
     * @return
     */
    Optional<class_5459.class_5460> tryFindOrCreate(Entity entity, IPortalDesciption desciption, BlockPos portalPosition, boolean extendedRadius);
}
