package dash.dashmode.utils;

import net.minecraft.util.math.Direction;

public interface IOriented {
    /**
     * Returns axis
     *
     * @return
     */
    Direction.Axis getAxis();

    /**
     * Set current axis
     *
     * @param axis
     */
    void setAxis(Direction.Axis axis);
}
