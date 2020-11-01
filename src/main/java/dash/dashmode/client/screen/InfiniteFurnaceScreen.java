package dash.dashmode.client.screen;

import dash.dashmode.DashMod;
import dash.dashmode.screen.InfiniteFurnaceScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class InfiniteFurnaceScreen extends AbstractFurnaceScreen<InfiniteFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(DashMod.ModId, "textures/gui/container/infinite_furnace.png");

    public InfiniteFurnaceScreen(InfiniteFurnaceScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, new FurnaceRecipeBookScreen(), inventory, title, TEXTURE);
    }
}