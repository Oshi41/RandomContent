package dash.dashmode.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
    @Accessor("quickCraftSlots")
    Set<Slot> rc_getQuickCraftSlots();
}
