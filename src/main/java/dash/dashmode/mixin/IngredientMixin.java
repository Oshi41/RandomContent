package dash.dashmode.mixin;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
    private static final Gson rc_gson = new GsonBuilder().create();

    @Unique
    private final Map<Item, JsonObject> rc_tags = new HashMap<>();

    @Unique
    private final AbstractComparator rc_jsonComparator = new DefaultComparator(JSONCompareMode.LENIENT);

    @Inject(method = "fromJson", at = @At(value = "RETURN"))
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

    @Inject(method = "fromPacket", at = @At(value = "RETURN"))
    private static void fromPacketMixin(PacketByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
        IngredientMixin value = (IngredientMixin) ((Object) cir.getReturnValue());

        int i = buf.readVarInt();

        for (int j = 0; j < i; j++) {
            Item item = Registry.ITEM.get(new Identifier(buf.readString()));
            JsonObject tag = rc_gson.fromJson(buf.readString(), JsonObject.class);

            value.rc_tags.put(item, tag);
        }
    }

    @Inject(method = "toJson", at = @At(value = "RETURN"))
    private void toJsonInject(CallbackInfoReturnable<JsonElement> cir) {
        if (rc_tags.isEmpty())
            return;

        JsonElement value = cir.getReturnValue();
        List<JsonObject> entries = new ArrayList<>();

        if (value.isJsonObject()) {
            entries.add(value.getAsJsonObject());
        }

        if (value.isJsonArray()) {
            entries.addAll(StreamSupport.stream(value.getAsJsonArray().spliterator(), false)
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toList()));
        }

        for (JsonObject entry : entries) {
            Item item = Registry.ITEM.get(new Identifier(entry.get("item").getAsString()));
            JsonObject compound = rc_tags.get(item);

            if (compound != null) {
                entry.add(Rc_CompoundTag, compound);
            }
        }
    }

    @Inject(method = "write", at = @At(value = "RETURN"))
    private void writeInject(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(rc_tags.size());

        for (Map.Entry<Item, JsonObject> entry : rc_tags.entrySet()) {
            Identifier id = Registry.ITEM.getId(entry.getKey());
            buf.writeString(id.toString());
            buf.writeString(entry.toString());
        }
    }

    @Inject(method = "test",
            at = @At(value = "RETURN"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")),
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
