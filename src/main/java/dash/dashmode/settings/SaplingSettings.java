package dash.dashmode.settings;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;

public class SaplingSettings extends FabricBlockSettings {
    private ICanPlant canPlant;

    public SaplingSettings(Material material, MaterialColor color) {
        super(material, color);
    }

    public SaplingSettings withCanPlantCallback(ICanPlant plant) {
        this.canPlant = plant;
        return this;
    }

    public ICanPlant getCanPlant() {
        return canPlant;
    }
}
