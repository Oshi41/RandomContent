package dash.dashmode.mixin;

import com.google.gson.JsonArray;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
    @Invoker("combinePattern")
    static String[] rc_combinePattern(String... lines) {
        throw new AssertionError("mixin");
    }

    @Invoker("getPattern")
    static String[] rc_getPattern(JsonArray json) {
        throw new AssertionError("mixin");
    }

    @Accessor("group")
    String rc_getGroup();
}

