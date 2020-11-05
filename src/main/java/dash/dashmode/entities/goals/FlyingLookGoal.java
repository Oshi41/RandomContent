package dash.dashmode.entities.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 *
 */
public class FlyingLookGoal extends Goal {
    private final MobEntity ghast;

    public FlyingLookGoal(MobEntity ghast) {
        this.ghast = ghast;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void tick() {
        if (this.ghast.getTarget() == null) {
            Vec3d vec3d = this.ghast.getVelocity();
            this.ghast.yaw = -((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F;
            this.ghast.bodyYaw = this.ghast.yaw;
        } else {
            LivingEntity livingEntity = this.ghast.getTarget();
            if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0D) {
                double e = livingEntity.getX() - this.ghast.getX();
                double f = livingEntity.getZ() - this.ghast.getZ();
                this.ghast.yaw = -((float) MathHelper.atan2(e, f)) * 57.295776F;
                this.ghast.bodyYaw = this.ghast.yaw;
            }
        }

    }
}
