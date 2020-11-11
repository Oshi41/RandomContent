package dash.dashmode.item;

import com.google.common.collect.Multimap;
import dash.dashmode.mixin.ArmorItemAccessor;
import dash.dashmode.utils.CustomArmorMaterial;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DashArmorItem extends ArmorItem {
    public DashArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);

        if (material.getKnockbackResistance() > 0) {
            Multimap<EntityAttribute, EntityAttributeModifier> multimap = getAttributeModifiers(slot);

            if (!multimap.containsKey(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                multimap.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(
                        ArmorItemAccessor.rc_getModifiers()[slot.getEntitySlotId()],
                        "Armor knockback resistance",
                        this.knockbackResistance,
                        EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (getMaterial() instanceof CustomArmorMaterial) {
            List<Text> texts = ((CustomArmorMaterial) getMaterial()).fullSetAbilities();
            if (!texts.isEmpty()) {
                tooltip.add(new TranslatableText("random_content.full_set_perks").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));
                tooltip.addAll(texts);
            }
        }
    }
}
