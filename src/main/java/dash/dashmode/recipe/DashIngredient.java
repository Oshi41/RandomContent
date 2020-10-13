package dash.dashmode.recipe;

import dash.dashmode.mixin.IngredientAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.AbstractComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

import java.util.Objects;
import java.util.function.Predicate;

public class DashIngredient implements Predicate<ItemStack> {
    public static final DashIngredient EMPTY = new DashIngredient(Ingredient.EMPTY);
    private static final AbstractComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);

    public final Ingredient source;

    private CompoundTag tag;

    public DashIngredient(Ingredient source) {
        this.source = source;
    }

    private static boolean test(ItemStack x, CompoundTag tag) {
        if (tag == null || tag.isEmpty())
            return true;

        CompoundTag itemTag = x.getTag();
        if (itemTag == null || itemTag.isEmpty())
            return false;

        String left = tag.toString();
        String right = itemTag.toString();

        try {
            JSONCompareResult result = JSONCompare.compareJSON(left, right, comparator);
            return result.passed();
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!source.test(stack))
            return false;

        return tag == null || test(stack, tag);
    }

    /**
     * Populates tag into cached stacks
     *
     * @param tag
     * @return
     */
    public DashIngredient and(CompoundTag tag) {
        this.tag = tag;

        if (tag != null) {
            CompoundTag copy = getTag();

            IngredientAccessor accessor = (IngredientAccessor) ((Object) this.source);
            accessor.rc_cacheMatchingStacks();
            for (ItemStack stack : accessor.rc_getMatchingStacks()) {
                stack.setTag(copy);
            }
        }

        return this;
    }

    @Nullable
    public CompoundTag getTag() {
        if (tag == null)
            return null;

        return tag.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DashIngredient)) return false;
        DashIngredient that = (DashIngredient) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, tag);
    }
}
