package dash.dashmode.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UpgradeItem extends Item implements IUpgrade {
    private final int level;

    public UpgradeItem(Settings settings, int level) {
        super(settings);
        this.level = level;
    }

    @Override
    public int getLevel(ItemStack stack) {
        return level;
    }
}
