package dash.dashmode.mixin;

import dash.dashmode.utils.IOriented;
import net.minecraft.class_5459;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_5459.class_5460.class)
public class class_5460Mixin implements IOriented {

    @Unique
    private Direction.Axis rc_axis = Direction.Axis.X;

    @Override
    public Direction.Axis getAxis() {
        return rc_axis;
    }

    @Override
    public void setAxis(Direction.Axis axis) {
        rc_axis = axis;
    }
}
