package dash.dashmode.armor;

import net.minecraft.util.Identifier;

import java.util.Map;

public interface IArmorSupplier {
    /**
     * Returns set with current wearing armor sets
     *
     * @return
     */
    Map<Identifier, ArmorDescription> getWearingSets();

    /**
     * Check if current set is on
     *
     * @param id
     * @return
     */
    boolean isOn(Identifier id);

    /**
     * Forcing refresh check
     */
    void refresh();
}
