package dash.dashmode.utils;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemStackUtils {

    /**
     * Redirecting method for shears item.
     * Check if current item a shear and return Items.Shears or stack.getItem()
     *
     * @param stack - current stack
     * @return
     */
    public static Item redirectIfShears(ItemStack stack) {
        return stack.getItem().isIn(FabricToolTags.SHEARS)
                ? Items.SHEARS
                : stack.getItem();
    }
}
