package dash.dashmode.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dash.dashmode.registry.DashRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DashShapedRecipe implements CraftingRecipe {
    private final Identifier id;
    private final String group;
    private final ItemStack output;
    private final int width;
    private final int height;
    private final DefaultedList<ISerialize> ingredients;

    public DashShapedRecipe(Identifier id, String group, ItemStack output, int width, int height, DefaultedList<ISerialize> ingredients) {
        this.id = id;
        this.group = group;
        this.output = output;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        for (int i = 0; i <= craftingInventory.getWidth() - this.width; ++i) {
            for (int j = 0; j <= craftingInventory.getHeight() - this.height; ++j) {
                if (this.matchesSmall(craftingInventory, i, j, true)
                        || this.matchesSmall(craftingInventory, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesSmall(CraftingInventory inv, int offsetX, int offsetY, boolean bl) {
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                Predicate<ItemStack> ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (bl) {
                        ingredient = ingredients.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = ingredients.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getStack(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DashRecipes.DashRecipes;
    }

    public static class Serializer implements RecipeSerializer<DashShapedRecipe> {
        @Override
        public DashShapedRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            Map<String, ISerialize> map = getComponents(JsonHelper.getObject(json, "key"));
            String[] strings = combinePattern(getPattern(JsonHelper.getArray(json, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            DefaultedList<ISerialize> defaultedList = getIngredients(strings, map, i, j);
            ItemStack result = parse(json.getAsJsonObject("result"));

            return new DashShapedRecipe(id, group, result, i, j, defaultedList);
        }

        @Override
        public DashShapedRecipe read(Identifier id, PacketByteBuf buf) {
            int i = buf.readVarInt();
            int j = buf.readVarInt();
            String string = buf.readString(32767);
            DefaultedList<ISerialize> defaultedList = DefaultedList.ofSize(i * j, new StackIngredient());

            for (int k = 0; k < defaultedList.size(); ++k) {
                StackIngredient ingredient = new StackIngredient();
                ingredient.fromBuf(buf);
                defaultedList.set(k, ingredient);
            }

            ItemStack itemStack = buf.readItemStack();

            return new DashShapedRecipe(id, string, itemStack, i, j, defaultedList);
        }

        @Override
        public void write(PacketByteBuf buf, DashShapedRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            buf.writeString(recipe.group);
            recipe.ingredients.forEach(x -> x.toBuf(buf));
            buf.writeItemStack(recipe.output);
        }

        // region

        private Map<String, ISerialize> getComponents(JsonObject json) {
            Map<String, ISerialize> map = Maps.newHashMap();
            Iterator var2 = json.entrySet().iterator();

            while (var2.hasNext()) {
                Map.Entry<String, JsonElement> entry = (Map.Entry) var2.next();
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                }

                if (" ".equals(entry.getKey())) {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }

                map.put(entry.getKey(), parse(entry.getValue()));
            }

            map.put(" ", new StackIngredient());
            return map;
        }

        private ISerialize parse(JsonElement json) {
            if (json != null && !json.isJsonNull()) {
                if (json.isJsonObject()) {
                    return fromObject(json.getAsJsonObject());
                } else if (json.isJsonArray()) {
                    JsonArray jsonArray = json.getAsJsonArray();
                    if (jsonArray.size() == 0) {
                        throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                    } else {
                        List<ISerialize> ingredients = StreamSupport.stream(jsonArray.spliterator(), false)
                                .filter(JsonElement::isJsonObject)
                                .map(x -> fromObject(x.getAsJsonObject())).collect(Collectors.toList());

                        //todo implement
                        return ingredients.get(0);
                    }
                } else {
                    throw new JsonSyntaxException("Expected item to be object or array of objects");
                }
            } else {
                throw new JsonSyntaxException("Item cannot be null");
            }
        }

        private ISerialize fromObject(JsonObject object) {
            if (object.has("item")) {
                StackIngredient ingredient = new StackIngredient();
                ingredient.fromJson(object);

                return ingredient;
            }

            if (object.has("tag")) {
                TagIngredient ingredient = new TagIngredient();
                ingredient.fromJson(object);
                return ingredient;
            }

            return new StackIngredient();
        }

        private String[] getPattern(JsonArray json) {
            String[] strings = new String[json.size()];
            if (strings.length > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
            } else if (strings.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            } else {
                for (int i = 0; i < strings.length; ++i) {
                    String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                    if (string.length() > 3) {
                        throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                    }

                    if (i > 0 && strings[0].length() != string.length()) {
                        throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                    }

                    strings[i] = string;
                }

                return strings;
            }
        }

        @VisibleForTesting
        String[] combinePattern(String... lines) {
            int i = Integer.MAX_VALUE;
            int j = 0;
            int k = 0;
            int l = 0;

            for (int m = 0; m < lines.length; ++m) {
                String string = lines[m];
                i = Math.min(i, findNextIngredient(string));
                int n = findNextIngredientReverse(string);
                j = Math.max(j, n);
                if (n < 0) {
                    if (k == m) {
                        ++k;
                    }

                    ++l;
                } else {
                    l = 0;
                }
            }

            if (lines.length == l) {
                return new String[0];
            } else {
                String[] strings = new String[lines.length - l - k];

                for (int o = 0; o < strings.length; ++o) {
                    strings[o] = lines[o + k].substring(i, j + 1);
                }

                return strings;
            }
        }

        private int findNextIngredient(String pattern) {
            int i;
            for (i = 0; i < pattern.length() && pattern.charAt(i) == ' '; ++i) {
            }

            return i;
        }

        private int findNextIngredientReverse(String pattern) {
            int i;
            for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == ' '; --i) {
            }

            return i;
        }

        private DefaultedList<ISerialize> getIngredients(String[] pattern, Map<String, ISerialize> key, int width, int height) {
            DefaultedList<ISerialize> defaultedList = DefaultedList.ofSize(width * height, new StackIngredient());
            Set<String> set = Sets.newHashSet(key.keySet());
            set.remove(" ");

            for (int i = 0; i < pattern.length; ++i) {
                for (int j = 0; j < pattern[i].length(); ++j) {
                    String string = pattern[i].substring(j, j + 1);
                    ISerialize ingredient = key.get(string);
                    if (ingredient == null) {
                        throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                    }

                    set.remove(string);
                    defaultedList.set(j + width * i, ingredient);
                }
            }

            if (!set.isEmpty()) {
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
            } else {
                return defaultedList;
            }
        }

        private ItemStack parse(JsonObject object) {
            ItemStack result = JsonHelper.getItem(object, "item").getDefaultStack();

            if (object.has("count")) {
                result.setCount(object.get("count").getAsInt());
            }

            return result;
        }

        // endregion
    }
}