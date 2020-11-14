package dash.dashmode.entities.projectiles;

import dash.dashmode.registry.DashEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class CustomFireBall extends AbstractFireballEntity {
    private static final TrackedData<ParticleEffect> Effect = DataTracker.registerData(CustomFireBall.class, TrackedDataHandlerRegistry.PARTICLE);
    private float damage;
    private boolean reflect;

    public CustomFireBall(World world) {
        super(DashEntities.CustomFireBall, world);
    }

    public CustomFireBall(LivingEntity livingEntity, double d, double e, double f, World world, ParticleEffect effect, float damage, boolean reflect) {
        super(DashEntities.CustomFireBall, livingEntity, d, e, f, world);
        this.damage = damage;
        this.reflect = reflect;

        getDataTracker().set(Effect, effect);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putFloat("damage", damage);
        tag.putBoolean("reflect", reflect);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        damage = tag.getFloat("damage");
        reflect = tag.getBoolean("reflect");
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        if (entity != null) {
            entity.damage(DamageSource.fireball(this, getOwner()), damage);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        getDataTracker().startTracking(Effect, ParticleTypes.EFFECT);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source))
            return false;

        scheduleVelocityUpdate();

        return reflect && super.damage(source, amount);
    }

    @Override
    protected ParticleEffect getParticleType() {
        return getDataTracker().get(Effect);
    }
}
