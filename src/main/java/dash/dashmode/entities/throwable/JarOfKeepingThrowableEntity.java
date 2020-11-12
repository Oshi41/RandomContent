package dash.dashmode.entities.throwable;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.registry.DashEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Random;

public class JarOfKeepingThrowableEntity extends ThrownItemEntity {

    public JarOfKeepingThrowableEntity(World world) {
        super(DashEntities.JarOfKeepingThrowableEntityType, world);
    }

    public JarOfKeepingThrowableEntity(World world, LivingEntity owner) {
        super(DashEntities.JarOfKeepingThrowableEntityType, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        dropStack(getItem());
        remove();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        ItemStack stack = getItem();

        // creative player can catch any mobs
        boolean isCreative = (getOwner() instanceof PlayerEntity && ((PlayerEntity) getOwner()).isCreative());
        // or check can catch
        boolean canCatch = isCreative || isCatchableEntity(entity);

        // perform catch logic
        if (canCatch) {
            // contains NBT
            CompoundTag tag = stack.getOrCreateSubTag(JarOfKeepingBlockEntity.BlockItemTag);
            canCatch = tag != null && !tag.isEmpty();

            // should check for catch chance
            if (canCatch && !isCreative) {
                int catchChance = tag.getInt(JarOfKeepingBlockEntity.CatchChanceTag);
                canCatch = catchChance <= 0 || world.random.nextInt(catchChance) == 0;
            }

            // save entity to NBT
            if (canCatch) {
                CompoundTag entityTag = new CompoundTag();
                canCatch = entity.saveToTag(entityTag);

                if (canCatch) {
                    tag.put(JarOfKeepingBlockEntity.EntityTag, entity.toTag(entityTag));
                }
            }
        }

        // perform particle effect
        if (world.isClient()) {
            spawnParticles(canCatch);
        } else if (canCatch) {
            // removing entity if can catch
            entity.remove();
        }

        dropStack(stack);
        remove();
    }

    protected boolean isCatchableEntity(Entity entity) {
        if (!entity.isAlive()) {
            return false;
        }

        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        StatusEffectInstance weakness = livingEntity.getStatusEffect(StatusEffects.WEAKNESS);

        if (weakness == null) {
            return false;
        }

        StatusEffectInstance slowness = livingEntity.getStatusEffect(StatusEffects.SLOWNESS);

        return slowness != null && slowness.getAmplifier() >= 3;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        int id = 0;
        if (getOwner() != null) {
            id = getOwner().getEntityId();
        }
        return new EntitySpawnS2CPacket(this, id);
    }

    protected void spawnParticles(boolean wasCaught) {
        ParticleEffect effect = wasCaught ? ParticleTypes.EXPLOSION : ParticleTypes.FLAME;

        Random random = getEntityWorld().random;
        int count = wasCaught ? 3 : 18;

        for (int i = 0; i < count; i++) {
            world.addParticle(
                    effect,
                    getX() + random.nextFloat() - random.nextFloat(),
                    getY() + random.nextFloat() - random.nextFloat(),
                    getZ() + random.nextFloat() - random.nextFloat(),
                    0,
                    random.nextFloat(),
                    0
            );
        }
    }
}
