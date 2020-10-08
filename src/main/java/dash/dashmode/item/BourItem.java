package dash.dashmode.item;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class BourItem extends PickaxeItem {

    /**
     * 0 - sinle block
     * 1 - 3 * 3
     * 2 - 5 * 5
     * etc
     */
    private final int radius;

    public BourItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, int radius) {
        super(material, attackDamage, attackSpeed, settings);
        this.radius = radius;
    }
}
