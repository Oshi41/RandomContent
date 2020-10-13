package dash.dashmode.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
    @Accessor("matchingStacks")
    ItemStack[] rc_getMatchingStacks();

    @Invoker("cacheMatchingStacks")
    void rc_cacheMatchingStacks();
}
