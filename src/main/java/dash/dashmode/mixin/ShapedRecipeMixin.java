package dash.dashmode.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(method = "getItemStack", at = @At("RETURN"))
    private static void getItemStackInject(JsonObject json, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack value = cir.getReturnValue();

        if (!json.has("compound"))
            return;

        JsonObject compound = JsonHelper.getObject(json, "compound");
        CompoundTag tag = value.getOrCreateTag();

        for (Map.Entry<String, JsonElement> entry : compound.entrySet()) {
            if (entry.getValue().isJsonObject())
                tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
        }
    }

    private static Tag parseTag(JsonObject element) {
        byte type = element.get("nbt_type").getAsByte();
        JsonElement value = element.get("nbt_value");

        switch (type) {
            case 0:
                return new CompoundTag();
            case 1:
                return ByteTag.of(value.getAsByte());
            case 2:
                return ShortTag.of(value.getAsShort());
            case 3:
                return IntTag.of(value.getAsInt());
            case 4:
                return LongTag.of(value.getAsLong());
            case 5:
                return FloatTag.of(value.getAsFloat());
            case 6:
                return DoubleTag.of(value.getAsDouble());
            case 7:
                // todo
                // ByteArrayTag
                return new CompoundTag();
            case 8:
                return StringTag.of(value.toString());

            case 9:
                ListTag listTag = new ListTag();

                JsonArray array = element.getAsJsonArray();
                array.forEach(x -> {
                    if (x.isJsonObject()) {
                        listTag.add(parseTag(x.getAsJsonObject()));
                    }
                });

                return listTag;

            case 10:
                CompoundTag tag = new CompoundTag();

                for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {

                    if (entry.getValue().isJsonObject())
                        tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
                }

                return tag;

            case 11:
                // IntArrayTag
                return new CompoundTag();
            case 12:
                // LongArrayTag
                return new CompoundTag();
        }


        throw new IllegalStateException("Unknown tag type: " + type);
    }

    // Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, tag);

//                switch (nbtTag.get("type").getAsInt()) {

//                }
}

