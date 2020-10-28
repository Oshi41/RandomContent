package dash.dashmode.entities.goals;

import dash.dashmode.mixin.MobEntityAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;

public class BurnGoal extends Goal {
    private MobEntityAccessor accessor;
    private MobEntity owner;

    public BurnGoal(MobEntity owner) {
        this.owner = owner;
        if (owner instanceof MobEntityAccessor) {
            accessor = (MobEntityAccessor) owner;
        }
    }

    @Override
    public void tick() {
        ItemStack itemStack = owner.getEquippedStack(EquipmentSlot.HEAD);
        if (!itemStack.isEmpty()) {
            if (itemStack.isDamageable()) {
                itemStack.setDamage(itemStack.getDamage() + owner.getEntityWorld().random.nextInt(2));
                if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                    owner.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                    owner.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }

            return;
        }

        owner.setOnFireFor(8);
    }

    @Override
    public boolean canStart() {
        return owner.isAlive() && accessor.rc_isAffectedByDaylight();
    }
}
