package dash.dashmode.entities.goals;

import com.mojang.datafixers.util.Function5;
import dash.dashmode.entities.cosmic.IAngry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Extended version of
 *
 * @see net.minecraft.entity.mob.GhastEntity
 * Search for ShootFireballGoal
 * <p>
 * To pass angry status implement
 * @see IAngry
 * <p>
 * Looks at follow range attribute to check max possible distance
 * to shoot
 */
public class RangedAttackGoal extends Goal {
    private final MobEntity ghast;
    private final Function5<World, MobEntity, Double, Double, Double, Boolean> shotFunc;
    private final Consumer<Boolean> setShootingFunc;
    private final SoundEvent warn;
    private final SoundEvent shoot;
    private final int defaultCooldown;
    public int cooldown;

    public RangedAttackGoal(MobEntity ghast) {
        this(ghast, RangedAttackGoal::spawnFireBall, SoundEvents.ENTITY_GHAST_WARN, SoundEvents.ENTITY_GHAST_SHOOT, 40);
    }

    public RangedAttackGoal(MobEntity ghast,
                            Function5<World, MobEntity, Double, Double, Double, Boolean> createFireBall,
                            SoundEvent warn,
                            SoundEvent shoot,
                            int defaultCooldown) {
        this.ghast = ghast;
        this.shotFunc = createFireBall;
        this.warn = warn;
        this.shoot = shoot;
        this.defaultCooldown = Math.abs(defaultCooldown);

        if (ghast instanceof GhastEntity) {
            setShootingFunc = ((GhastEntity) ghast)::setShooting;
        } else if (ghast instanceof IAngry) {
            setShootingFunc = ((IAngry) ghast)::setIsAngry;
        } else {
            setShootingFunc = x -> {
            };
        }
    }

    private static Boolean spawnFireBall(World world, MobEntity self, double velocityX, double velocityY, double velocityZ) {
        FireballEntity entity = new FireballEntity(world, self, velocityX, velocityY, velocityZ);
        Vec3d lookVec = self.getRotationVec(1.0F);
        entity.updatePosition(self.getX() + lookVec.x * 4.0D, self.getBodyY(0.5D) + 0.5D, entity.getZ() + lookVec.z * 4.0D);
        world.spawnEntity(entity);
        return true;
    }

    @Override
    public boolean canStart() {
        return this.ghast.getTarget() != null;
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        setShootingFunc.accept(false);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.ghast.getTarget();
        if (Math.sqrt(ghast.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)) < ghast.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) && this.ghast.canSee(livingEntity)) {
            World world = this.ghast.world;
            ++this.cooldown;
            if (this.cooldown == 10 && !this.ghast.isSilent() && warn != null) {
                world.playSound(null, ghast.getBlockPos(), warn, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
            }

            if (this.cooldown == 20) {
                double e = 4.0D;
                Vec3d vec3d = this.ghast.getRotationVec(1.0F);
                double f = livingEntity.getX() - (this.ghast.getX() + vec3d.x * e);
                double g = livingEntity.getBodyY(0.5D) - (0.5D + this.ghast.getBodyY(0.5D));
                double h = livingEntity.getZ() - (this.ghast.getZ() + vec3d.z * e);
                if (!this.ghast.isSilent() && shoot != null) {
                    world.playSound(null, ghast.getBlockPos(), shoot, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
                }

                if (shotFunc.apply(world, this.ghast, f, g, h)) {
                    this.cooldown = -defaultCooldown;
                }
            }
        } else if (this.cooldown > 0) {
            --this.cooldown;
        }

        setShootingFunc.accept(this.cooldown > 10);
    }
}
