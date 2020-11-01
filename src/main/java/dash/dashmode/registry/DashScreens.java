package dash.dashmode.registry;

import dash.dashmode.screen.InfiniteFurnaceScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class DashScreens {
    public static ScreenHandlerType<InfiniteFurnaceScreenHandler> InfiniteFurnace;

    public static void init(String modid) {
        InfiniteFurnace = ScreenHandlerRegistry.registerSimple(new Identifier(modid, "infinite_furnace"), InfiniteFurnaceScreenHandler::new);
    }
}
