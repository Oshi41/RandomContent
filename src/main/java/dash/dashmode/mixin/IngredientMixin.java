package dash.dashmode.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dash.dashmode.utils.NbtUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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
    @Unique
    private final Map<Item, CompoundTag> rc_tags = new HashMap<>();

    @Unique
    private final AbstractComparator rc_jsonComparator = new DefaultComparator(JSONCompareMode.LENIENT);

    @Inject(method = "fromJson", at = @At(value = "RETURN"))
    private static void fromJsonInject(@Nullable JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
        IngredientMixin value = (IngredientMixin) ((Object) cir.getReturnValue());

        if (json == null || !json.isJsonObject()) {
            return;
        }

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

        items.removeIf(x -> !x.has(NbtUtil.CompoundTagName));

        if (items.isEmpty()) {
            return;
        }

        for (JsonObject itemObj : items) {
            Item itemEntry = JsonHelper.getItem(itemObj, "item");
            try {
                String jsonRaw = NbtUtil.GSON.toJson(itemObj.getAsJsonObject(NbtUtil.CompoundTagName));
                CompoundTag compoundTag = StringNbtReader.parse(jsonRaw);

                value.rc_tags.put(itemEntry, compoundTag);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(method = "fromPacket", at = @At(value = "RETURN"))
    private static void fromPacketMixin(PacketByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
        IngredientMixin value = (IngredientMixin) ((Object) cir.getReturnValue());

        int i = buf.readVarInt();

        for (int j = 0; j < i; j++) {
            String id = buf.readString();
            CompoundTag compound = buf.readCompoundTag();

            Item item = Registry.ITEM.get(new Identifier(id));
            value.rc_tags.put(item, compound);
        }
    }

    @Inject(method = "toJson", at = @At(value = "RETURN"))
    private void toJsonInject(CallbackInfoReturnable<JsonElement> cir) {
        if (rc_tags.isEmpty()) {
            return;
        }

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
            CompoundTag tag = rc_tags.get(JsonHelper.getItem(entry, "item"));

            if (tag != null) {
                JsonObject jsonObject = NbtUtil.GSON.fromJson(tag.toString(), JsonObject.class);
                entry.add(NbtUtil.CompoundTagName, jsonObject);
            }
        }
    }

    @Inject(method = "write", at = @At(value = "RETURN"))
    private void writeInject(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(rc_tags.size());

        for (Map.Entry<Item, CompoundTag> entry : rc_tags.entrySet()) {
            String itemId = Registry.ITEM.getId(entry.getKey()).toString();

            buf.writeString(itemId);
            buf.writeCompoundTag(entry.getValue());
        }
    }

    @Inject(method = "test",
            at = @At(value = "RETURN"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")),
            cancellable = true)
    public void testInject(@Nullable ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (rc_tags.isEmpty() || itemStack == null) {
            return;
        }

        CompoundTag compoundTag = rc_tags.get(itemStack.getItem());
        if (compoundTag == null) {
            return;
        }

        CompoundTag left = itemStack.getTag();
        if (left == null) {
            return;
        }

        try {
            JSONCompareResult result = JSONCompare.compareJSON(left.toString(), compoundTag.toString(), rc_jsonComparator);
            cir.setReturnValue(result.passed());
        } catch (JSONException e) {
            e.printStackTrace();
            cir.setReturnValue(false);
        }
    }
}
