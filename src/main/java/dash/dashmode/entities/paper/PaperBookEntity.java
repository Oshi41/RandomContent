package dash.dashmode.entities.paper;

import dash.dashmode.entities.goals.RangedAttackGoal;
import dash.dashmode.entities.projectiles.CustomFireBall;
import dash.dashmode.registry.DashEntities;
import dash.dashmode.utils.EntityUtils;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PaperBookEntity extends HostileEntity {
    public PaperBookEntity(World world) {
        super(DashEntities.PaperBook, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));

        this.goalSelector.add(4, new RangedAttackGoal(this, this::shoot, null, null, 20));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));

        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, (new RevengeGoal(this)));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        Vec3d pos = getPos();

        for (int i = 0; i < 3; i++) {
            world.addParticle(ParticleTypes.ENCHANT,
                    pos.getX(),
                    pos.getY() + 2.0D,
                    pos.getZ(),
                    i * random.nextFloat() - 0.5D,
                    random.nextFloat() - 1.0F,
                    i * random.nextFloat() - 0.5D);
        }
    }

    private boolean shoot(World world, MobEntity self, double velocityX, double velocityY, double velocityZ) {
        Vec3d speed = this.getRotationVec(1.0F).multiply(2);

        Vec3d pos = new Vec3d(velocityX, velocityY, velocityZ);

        CustomFireBall fireballEntity = new CustomFireBall(this,
                pos.x,
                pos.y,
                pos.z,
                world,
                ParticleTypes.ENCHANT,
                (float) (getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 2),
                false);

        fireballEntity.setItem(new ItemStack(Items.BOOK));
        fireballEntity.updatePosition(this.getX() + speed.x, this.getBodyY(0.5D) + 0.5D, fireballEntity.getZ() + speed.z);
        world.spawnEntity(fireballEntity);
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, EntityUtils.increaseBurnDamage(source, amount));
    }
}
