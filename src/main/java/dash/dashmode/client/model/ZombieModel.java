package dash.dashmode.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.HostileEntity;

@Environment(EnvType.CLIENT)
public class ZombieModel<T extends HostileEntity> extends AbstractZombieModel<T> {
    public ZombieModel(float scale, boolean bl) {
        super(scale, 0.0F, 64, bl ? 32 : 64);
    }

    @Override
    public boolean isAttacking(T hostileEntity) {
        return false;
    }
}
