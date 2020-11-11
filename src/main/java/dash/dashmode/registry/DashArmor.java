package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.armor.ArmorDescription;
import dash.dashmode.utils.CustomArmorMaterial;
import dash.dashmode.utils.RangeEnchantApply;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashArmor {
    public static final CustomArmorMaterial GlowstoneArmor = new CustomArmorMaterial("glowstone",
            15,
            new int[]{2, 5, 6, 2},
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            () -> Ingredient.ofItems(Items.IRON_INGOT));


    public static void init(String modid) {
        Registry.register(DashMod.ArmorSetRegistry,
                new Identifier(modid, "glowstone"),
                new ArmorDescription()
                        .withSet(DashItems.find(GlowstoneArmor))
                        .restrict(StatusEffects.GLOWING)
                        .withPermanentPotion(() -> new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20))
                        .forEntitiesNear(new RangeEnchantApply(
                                StatusEffects.GLOWING,
                                entity -> true,
                                10,
                                20 * 10)));
    }
}
