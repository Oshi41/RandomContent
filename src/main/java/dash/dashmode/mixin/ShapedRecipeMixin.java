package dash.dashmode.mixin;

import com.google.gson.JsonObject;
import dash.dashmode.utils.NbtUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(method = "getItemStack", at = @At("RETURN"))
    private static void getItemStackInject(JsonObject json, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack value = cir.getReturnValue();

        if (!json.has(NbtUtil.CompoundTagName)) {
            return;
        }

        JsonObject compound = JsonHelper.getObject(json, NbtUtil.CompoundTagName);
        CompoundTag tag = NbtUtil.parseCompound(compound);
        value.setTag(tag);
    }
}

