package dash.dashmode.item;

import net.minecraft.item.ItemStack;

public interface IUpgrade {
    /**
     * Level of upgrade item
     *
     * @param stack - current item stack
     * @return
     */
    int getLevel(ItemStack stack);
}
