package dash.dashmode.armor;

import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface IWear {

    /**
     * Called when current wear status was changed
     *
     * @param entity
     * @param isOn
     */
    void onWearStatusChanged(LivingEntity entity, boolean isOn);
}
