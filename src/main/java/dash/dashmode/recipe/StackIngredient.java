package dash.dashmode.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.AbstractComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;


public class StackIngredient implements ISerialize {
    private static final AbstractComparator comparer = new DefaultComparator(JSONCompareMode.LENIENT);

    private ItemStack stack;

    @Override
    public boolean test(ItemStack stack) {
        if (stack != null) {
            if (stack.getItem() == stack.getItem()) {
                if (stack.hasTag()) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null) {
                        String left = stack.getTag().getString("compound");
                        String right = tag.toString();

                        try {
                            JSONCompareResult result = JSONCompare.compareJSON(left, right, comparer);
                            return result.passed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void fromJson(JsonElement object) {
        stack = JsonHelper.getItem(object.getAsJsonObject(), "item").getDefaultStack();

        if (object instanceof JsonObject && ((JsonObject) object).has("compound")) {
            stack.getOrCreateTag().putString("compound", ((JsonObject) object).get("compound").toString());
        }
    }

    @Override
    public JsonElement toJson() {
        JsonObject result = new JsonObject();

        result.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());

        if (stack.hasTag()) {
            result.addProperty("compound", stack.getTag().getString("compound"));
        }

        return result;
    }

    @Override
    public void fromBuf(PacketByteBuf buf) {
        stack = buf.readItemStack();
    }

    @Override
    public void toBuf(PacketByteBuf buf) {
        buf.writeItemStack(stack);
    }
}
