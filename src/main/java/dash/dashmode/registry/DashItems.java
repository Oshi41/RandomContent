package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.item.*;
import dash.dashmode.utils.CustomArmorMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashItems {
    public static final Item PaperApple;
    public static final Item PaperCrystal;

    public static final Item GoldenMultitool;
    public static final Item IronMultitool;
    public static final Item DiamondMultitool;
    public static final Item NetheriteMultitool;


    public static final Item IronScissors;
    public static final Item GoldenScissors;
    public static final Item DiamondScissors;
    public static final Item NetheriteScissors;

    public static final Item IronBoer;
    public static final Item IronFurnaceSpring;
    public static final Item GoldFurnaceSpring;
    public static final Item DiamondFurnaceSpring;

    public static final Item GlowstoneChunk;
    public static final Item BedrockChunk;
    public static final Item PaperSword;

    static {
        PaperApple = new Item((new Item.Settings()).group(DashMod.DashItemsTab).food(FoodComponents.APPLE));
        PaperCrystal = new Item(new Item.Settings().group(DashMod.DashItemsTab));

        GoldenMultitool = new MultiToolItem(DashToolMaterials.GoldenMultiTool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        IronMultitool = new MultiToolItem(DashToolMaterials.IronMultitool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        DiamondMultitool = new MultiToolItem(DashToolMaterials.DiamondMultiTool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));
        NetheriteMultitool = new MultiToolItem(DashToolMaterials.NetheriteMultitool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab));

        IronBoer = new BoerItem(DashToolMaterials.IronMultitool, 3, -3F, new Item.Settings().group(DashMod.DashItemsTab), 1, 0);

        IronScissors = new DashShears(DashToolMaterials.IronShears, new Item.Settings().group(DashMod.DashItemsTab));
        GoldenScissors = new DashShears(DashToolMaterials.GoldenShears, new Item.Settings().group(DashMod.DashItemsTab));
        DiamondScissors = new DashShears(DashToolMaterials.DiamondShears, new Item.Settings().group(DashMod.DashItemsTab));
        NetheriteScissors = new DashShears(DashToolMaterials.NetheriteShears, new Item.Settings().group(DashMod.DashItemsTab));

        IronFurnaceSpring = new UpgradeItem(new Item.Settings().maxCount(1).group(DashMod.DashItemsTab), 1);
        GoldFurnaceSpring = new UpgradeItem(new Item.Settings().maxCount(1).group(DashMod.DashItemsTab), 2);
        DiamondFurnaceSpring = new UpgradeItem(new Item.Settings().maxCount(1).group(DashMod.DashItemsTab), 3);

        GlowstoneChunk = new Item(new Item.Settings().group(DashMod.DashItemsTab));
        BedrockChunk = new Item(new Item.Settings().group(DashMod.DashItemsTab));
        PaperSword = new DirectDamageSword(DashToolMaterials.PaperSword, 2, -2.4f, new Item.Settings().group(DashMod.DashItemsTab));
    }

    public static void init(String modId) {
        DashMod.MainLogger.debug("Entering to item registry");

        Registry.register(Registry.ITEM, new Identifier(modId, "paper_apple"), PaperApple);
        Registry.register(Registry.ITEM, new Identifier(modId, "paper_crystal"), PaperCrystal);
        Registry.register(Registry.ITEM, new Identifier(modId, "golden_multitool"), GoldenMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "iron_multitool"), IronMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "diamond_multitool"), DiamondMultitool);
        Registry.register(Registry.ITEM, new Identifier(modId, "netherite_multitool"), NetheriteMultitool);

        Registry.register(Registry.ITEM, new Identifier(modId, "boer_iron"), IronBoer);
        Registry.register(Registry.ITEM, new Identifier(modId, "iron_scissors"), IronScissors);
        Registry.register(Registry.ITEM, new Identifier(modId, "golden_scissors"), GoldenScissors);

        Registry.register(Registry.ITEM, new Identifier(modId, "furnace_coil_iron"), IronFurnaceSpring);
        Registry.register(Registry.ITEM, new Identifier(modId, "furnace_coil_gold"), GoldFurnaceSpring);
        Registry.register(Registry.ITEM, new Identifier(modId, "furnace_coil_diamond"), DiamondFurnaceSpring);

        createAndRegisterArmorSet(DashArmor.GlowstoneArmor, new Item.Settings().group(DashMod.DashItemsTab), modId);

        Registry.register(Registry.ITEM, new Identifier(modId, "glowstone_chunk"), GlowstoneChunk);
        Registry.register(Registry.ITEM, new Identifier(modId, "bedrock_chunk"), BedrockChunk);
        Registry.register(Registry.ITEM, new Identifier(modId, "paper_sword"), PaperSword);
    }

    /**
     * Easy create & register armor set.
     * 1) Create ArmorMaterial with custom name, for example "glowstone"
     * 2) place in "texture\models\armor" two files: "glowstone_layer_1" and "glowstone_layer_2"
     * 3) create 4 item textures: "glowstone_head", "glowstone_chest", "glowstone_legs", "glowstone_feet"
     *
     * @param material - modded material for registry
     * @param settings - item settings for all set
     * @param modid    - id of mod
     * @return
     */
    private static Map<EquipmentSlot, ArmorItem> createAndRegisterArmorSet(CustomArmorMaterial material, Item.Settings settings, String modid) {
        HashMap<EquipmentSlot, ArmorItem> map = new HashMap<>();

        for (EquipmentSlot slot : Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            map.put(slot, new DashArmorItem(material, slot, settings));
        }

        for (ArmorItem item : map.values()) {
            Registry.register(Registry.ITEM, new Identifier(modid, String.format("%s_%s", material.getRegistryName(), item.getSlotType().getName())), item);
        }

        return map;
    }


    /**
     * Performs search for modded armor item
     *
     * @param material - custom material
     * @param slot     - current slot
     * @return
     */
    @Nullable
    public static ArmorItem find(CustomArmorMaterial material, EquipmentSlot slot) {
        Identifier identifier = new Identifier(DashMod.ModId, String.format("%s_%s", material.getRegistryName(), slot.getName()));
        Item item = Registry.ITEM.get(identifier);
        if (item instanceof ArmorItem)
            return ((ArmorItem) item);

        return null;
    }

    public static Map<EquipmentSlot, ArmorItem> find(CustomArmorMaterial material) {
        Map<EquipmentSlot, ArmorItem> map = Arrays.stream(EquipmentSlot.values())
                .filter(x -> x.getType() == EquipmentSlot.Type.ARMOR)
                .map(x -> find(material, x))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ArmorItem::getSlotType, x -> x));

        return map;
    }
}
