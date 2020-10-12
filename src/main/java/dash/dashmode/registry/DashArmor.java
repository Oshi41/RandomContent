package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.armor.ArmorDescription;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashArmor {
    private static final ArmorDescription GoldArmor = new ArmorDescription()
            .withSlot(EquipmentSlot.HEAD, Items.GOLDEN_HELMET)
            .withSlot(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE)
            .withSlot(EquipmentSlot.LEGS, Items.GOLDEN_LEGGINGS)
            .withSlot(EquipmentSlot.FEET, Items.GOLDEN_BOOTS)
            .withPermanentPotion(new StatusEffectInstance(StatusEffects.GLOWING, 20));

    private static final ArmorDescription DiamondArmor = new ArmorDescription()
            .withSlot(EquipmentSlot.HEAD, Items.DIAMOND_HELMET)
            .withSlot(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE)
            .withSlot(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS)
            .withSlot(EquipmentSlot.FEET, Items.DIAMOND_BOOTS)
            .onAttack((source, amount) -> amount * 2);

    public static void init(String modid) {
        Registry.register(DashMod.ArmorSetRegistry, new Identifier(modid, "golden"), GoldArmor);
        Registry.register(DashMod.ArmorSetRegistry, new Identifier(modid, "diamond"), DiamondArmor);
    }
}
