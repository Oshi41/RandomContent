package dash.dashmode.gravity;

import dash.dashmode.DashMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;

public class GravityHelper {

    /**
     * Returns gravity multiplier for current entity.
     * 1 for standart
     *
     * @param entity
     * @return
     */
    public static float getGravity(Entity entity) {
        if (entity == null || entity.getEntityWorld() == null) {
            return 1f;
        }

        Float result = DashMod.MainConfig.gravityMap.getValue().get(entity.getEntityWorld().getRegistryKey().getValue());

        if (result == null) {
            return 1;
        }

        return result;
    }

    /**
     * Check wherever entity is flying
     *
     * @param entity
     * @return
     */
    public static boolean isFlyingEntity(Entity entity) {
        if (entity instanceof MobEntity) {
            // flying entity
            if (((MobEntity) entity).getMoveControl() instanceof FlightMoveControl) {
                return true;
            }
        }

        if (entity instanceof FlyingEntity) {
            return true;
        }

        return entity instanceof BlazeEntity;
    }

    /**
     * Registering object with custom gravity
     * Can be such as Entity or IGravityProvider
     *
     * @param actor
     */
    public static void registerForInstance(Object actor) {

    }
}
