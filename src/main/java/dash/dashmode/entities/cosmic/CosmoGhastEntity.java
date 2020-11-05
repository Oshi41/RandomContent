package dash.dashmode.entities.cosmic;

import dash.dashmode.DashMod;
import dash.dashmode.entities.controls.GhastLiveMoveControl;
import dash.dashmode.entities.goals.FlyingLookGoal;
import dash.dashmode.entities.goals.RandomFlyGoal;
import dash.dashmode.entities.goals.RangedAttackGoal;
import dash.dashmode.registry.DashEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

public class CosmoGhastEntity extends FlyingEntity implements Monster, IAngry {
    private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(CosmoGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public CosmoGhastEntity(World world) {
        super(DashEntities.CosmoGhast, world);
        experiencePoints = 40;
        this.moveControl = new GhastLiveMoveControl(this);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        getDataTracker().startTracking(ANGRY, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(5, new RandomFlyGoal(this));
        this.goalSelector.add(7, new FlyingLookGoal(this));
        this.goalSelector.add(7, new RangedAttackGoal(this, this::shootLightning, SoundEvents.ENTITY_GHAST_WARN, SoundEvents.ENTITY_GHAST_SHOOT, 60));


        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false,
                (livingEntity) -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0D));
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 4.55F;
    }

    private boolean shootLightning(World world, MobEntity self, double velocityX, double velocityY, double velocityZ) {
        LivingEntity victim = getTarget();
        if (victim == null)
            return false;

        Random random = world.random;

        for (int i = 0; i < 3; i++) {
            BlockPos blockPos = victim.getBlockPos().add((3 * (random.nextFloat() - random.nextFloat())), 0, (3 * (random.nextFloat() - random.nextFloat())));
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());

            if (y > blockPos.getY()) {
                blockPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
            }

            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
            if (lightningEntity == null) {
                DashMod.MainLogger.error("can't create lightning bolt");
                return false;
            }

            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            lightningEntity.setChanneler(victim instanceof ServerPlayerEntity ? (ServerPlayerEntity) victim : null);
            world.spawnEntity(lightningEntity);
        }

        return true;
    }

    @Override
    public void setIsAngry(Boolean shooting) {
        getDataTracker().set(ANGRY, shooting);
    }

    @Override
    public boolean isAngry() {
        return getDataTracker().get(ANGRY);
    }
}
