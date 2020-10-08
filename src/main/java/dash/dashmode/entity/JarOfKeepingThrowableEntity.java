package dash.dashmode.entity;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.registry.DashEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

        if (entity != null && isCatchableEntity(entity)) {
            CompoundTag tag = stack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag);
            if (tag != null && !tag.isEmpty()) {
                int catchChance = tag.getInt(JarOfKeepingBlockEntity.CatchChanceTag);
                boolean canCatch = catchChance <= 0 || world.random.nextInt(catchChance) == 0;

                if (canCatch) {
                    CompoundTag entityTag = new CompoundTag();
                    entity.saveToTag(entityTag);
                    tag.put(JarOfKeepingBlockEntity.EntityTag, entity.toTag(entityTag));
                }

                if (world.isClient()) {
                    spawnParticles(canCatch);
                } else {
                    entity.remove();
                }
            }
        }

        dropStack(stack);
        remove();
    }

    protected boolean isCatchableEntity(Entity entity) {
        if (!entity.isAlive())
            return false;

        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        if (getOwner() instanceof PlayerEntity && ((PlayerEntity) getOwner()).isCreative()) {
            return true;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        StatusEffectInstance weakness = livingEntity.getStatusEffect(StatusEffects.WEAKNESS);

        if (weakness == null)
            return false;

        StatusEffectInstance slowness = livingEntity.getStatusEffect(StatusEffects.SLOWNESS);

        return slowness != null && slowness.getAmplifier() >= 3;
    }

    @Environment(EnvType.CLIENT)
    protected void spawnParticles(boolean wasCaught) {
        ParticleEffect effect = wasCaught ? ParticleTypes.EXPLOSION : ParticleTypes.SMOKE;

        Random random = getEntityWorld().random;

        for (int i = 0; i < 6; i++) {
            world.addParticle(
                    effect,
                    getX() + random.nextFloat() - random.nextFloat(),
                    getY() + random.nextFloat() - random.nextFloat(),
                    getZ() + random.nextFloat() - random.nextFloat(),
                    random.nextFloat() - 1,
                    random.nextFloat(),
                    random.nextFloat() - 1
            );
        }
    }
}
