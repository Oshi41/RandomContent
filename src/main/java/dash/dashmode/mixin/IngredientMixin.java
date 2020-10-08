package dash.dashmode.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.AbstractComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(Ingredient.class)
public abstract class IngredientMixin {
    private static final String Rc_CompoundTag = "compound";
    private final Map<Item, JsonObject> rc_tags = new HashMap<>();
    private final AbstractComparator rc_jsonComparator = new DefaultComparator(JSONCompareMode.LENIENT);

    @Inject(method = "fromJson", at = @At("RETURN"))
    private static void fromJsonInject(@Nullable JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
        IngredientMixin value = (IngredientMixin) ((Object) cir.getReturnValue());

        if (json == null || !json.isJsonObject())
            return;

        List<JsonObject> items = new ArrayList<>();

        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            items.addAll(StreamSupport.stream(array.spliterator(), false)
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toList()));
        }

        if (json.isJsonObject()) {
            items.add(json.getAsJsonObject());
        }

        for (JsonObject item : items) {
            if (!item.has(Rc_CompoundTag))
                continue;

            Identifier itemId = new Identifier(item.get("item").getAsString());
            Item itemEntry = Registry.ITEM.get(itemId);

            value.rc_tags.put(itemEntry, item.getAsJsonObject(Rc_CompoundTag));
        }
    }

    @Inject(method = "test",
            at = @At("RETURN"),
            cancellable = true)
    public void testInject(@Nullable ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            return;

        if (itemStack == null)
            return;

        JsonObject json = rc_tags.get(itemStack.getItem());
        if (json == null)
            return;

        CompoundTag tag = itemStack.getTag();
        if (tag == null) {
            cir.setReturnValue(false);
            return;
        }

        try {
            String left = json.toString();
            String right = tag.toString();

            JSONCompareResult result = JSONCompare.compareJSON(left, right, rc_jsonComparator);
            cir.setReturnValue(result.passed());
        } catch (AssertionError | JSONException e) {
            cir.setReturnValue(false);
        }
    }
}
