package dash.dashmode.mixin;

import com.google.gson.JsonArray;
import net.minecraft.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
    @Invoker("combinePattern")
    static String[] combinePattern(String... lines) {
        throw new AssertionError("mixin");
    }

    @Invoker("getPattern")
    static String[] getPattern(JsonArray json) {
        throw new AssertionError("mixin");
    }
}

