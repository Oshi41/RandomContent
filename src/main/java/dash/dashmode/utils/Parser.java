package dash.dashmode.utils;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dash.dashmode.recipe.DashIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Parser {
    private static final String tagName = "compound";
    public static final Gson GSON = new GsonBuilder().create();

    /**
     * Parse tag from json
     *
     * @param object
     * @return
     */
    public static CompoundTag parseCompound(JsonObject object) {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {

            if (entry.getValue().isJsonObject()) {
                tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
            }
        }

        return tag;
    }

    private static Tag parseTag(JsonObject element) {
        byte type = element.get("nbt_type").getAsByte();
        JsonElement value = element.get("nbt_value");

        switch (type) {
            case 0:
                return EndTag.INSTANCE;
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
                List<Byte> bytes = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsByte)
                        .collect(Collectors.toList());
                return new ByteArrayTag(bytes);
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

                    if (entry.getValue().isJsonObject()) {
                        tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
                    }
                }

                return tag;

            case 11:
                List<Integer> ints = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsInt)
                        .collect(Collectors.toList());
                return new IntArrayTag(ints);
            case 12:
                List<Long> longs = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsLong)
                        .collect(Collectors.toList());
                return new LongArrayTag(longs);
        }


        throw new IllegalStateException("Unknown tag type: " + type);
    }

    /**
     * Parse single Dash ingredient
     *
     * @param object - json element
     * @return
     */
    public static DashIngredient parseIngredient(JsonObject object) {
        DashIngredient ingredient = new DashIngredient(Ingredient.fromJson(object));

        if (object.has(tagName)) {
            String rawJson = Parser.GSON.toJson(object.getAsJsonObject(tagName));
            try {
                CompoundTag tag = StringNbtReader.parse(rawJson);
                ingredient = ingredient.and(tag);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }

        return ingredient;
    }

    public static ItemStack parseItemStack(JsonObject object) {
        ItemStack stack = ShapedRecipe.getItemStack(object);

        if (object.has(tagName)) {
            CompoundTag compoundTag = parseCompound(object.getAsJsonObject(tagName));
            stack.setTag(compoundTag);
        }

        return stack;
    }
}
