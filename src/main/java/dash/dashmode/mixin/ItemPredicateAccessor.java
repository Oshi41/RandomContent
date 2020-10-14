package dash.dashmode.mixin;

import net.minecraft.item.Item;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemPredicate.class)
public interface ItemPredicateAccessor {
    @Accessor("item")
    Item rc_getItem();
}
