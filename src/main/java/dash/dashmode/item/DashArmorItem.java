package dash.dashmode.item;

import com.google.common.collect.Multimap;
import dash.dashmode.mixin.ArmorItemAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

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
}
