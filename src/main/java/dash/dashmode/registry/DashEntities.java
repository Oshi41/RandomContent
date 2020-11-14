package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.entities.cosmic.CosmoGhastEntity;
import dash.dashmode.entities.paper.PaperBookEntity;
import dash.dashmode.entities.paper.PaperCowEntity;
import dash.dashmode.entities.paper.PaperZombieEntity;
import dash.dashmode.entities.projectiles.CustomFireBall;
import dash.dashmode.entities.projectiles.JarOfKeepingThrowableEntity;
import dash.dashmode.utils.DynamicResourceUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashEntities {
    public static final EntityType<JarOfKeepingThrowableEntity> JarOfKeepingThrowableEntityType;
    public static final EntityType<PaperZombieEntity> PaperZombie;
    public static final EntityType<PaperCowEntity> PaperCow;
    public static final EntityType<CosmoGhastEntity> CosmoGhast;
    public static final EntityType<PaperBookEntity> PaperBook;
    public static final EntityType<dash.dashmode.entities.projectiles.CustomFireBall> CustomFireBall;

    static {
        JarOfKeepingThrowableEntityType = FabricEntityTypeBuilder.<JarOfKeepingThrowableEntity>create(SpawnGroup.MISC, (type, world) -> new JarOfKeepingThrowableEntity(world))
                .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                .trackRangeBlocks(4)
                .trackedUpdateRate(4)
                .build();

        PaperZombie = FabricEntityTypeBuilder.<PaperZombieEntity>create(SpawnGroup.MONSTER, (type, world) -> new PaperZombieEntity(world))
                .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
                .trackRangeBlocks(8)
                .build();

        PaperCow = FabricEntityTypeBuilder.<PaperCowEntity>create(SpawnGroup.CREATURE, (type, world) -> new PaperCowEntity(world))
                .dimensions(EntityDimensions.fixed(0.9F, 1.4F))
                .trackRangeBlocks(14)
                .build();

        CosmoGhast = FabricEntityTypeBuilder.<CosmoGhastEntity>create(SpawnGroup.MONSTER, (type, world) -> new CosmoGhastEntity(world))
                .dimensions(EntityDimensions.fixed(7, 7))
                .trackRangeBlocks(10)
                .fireImmune()
                .build();

        PaperBook = FabricEntityTypeBuilder.<PaperBookEntity>create(SpawnGroup.MONSTER, (type, world) -> new PaperBookEntity(world))
                .dimensions(EntityDimensions.fixed(1, 1.5F))
                .trackRangeBlocks(10)
                .build();

        CustomFireBall = FabricEntityTypeBuilder.<dash.dashmode.entities.projectiles.CustomFireBall>create(SpawnGroup.MONSTER, (type, world) -> new CustomFireBall(world))
                .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                .trackRangeBlocks(4)
                .trackRangeBlocks(10)
                .build();
    }

    public static void init(String modid) {
        initEntities(modid);
        initAttributes(modid);
    }

    private static void initEntities(String modid) {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "jar_of_keeping_throwable"), JarOfKeepingThrowableEntityType);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "paper_zombie"), PaperZombie);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "paper_cow"), PaperCow);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "cosmo_ghast"), CosmoGhast);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "book"), PaperBook);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "custom_fireball"), CustomFireBall);
    }

    private static void initAttributes(String modid) {
        List<Identifier> dashTypes = Registry.ENTITY_TYPE.getIds().stream().filter(x -> x.getNamespace().equals(modid)).collect(Collectors.toList());

        Identifier folder = new Identifier("stats");

        for (Identifier id : dashTypes) {
            Tag elements = DynamicResourceUtils.loadFromNbt(id, folder);
            if (!(elements instanceof ListTag))
                continue;

            EntityType<?> entityType = Registry.ENTITY_TYPE.get(id);
            if (!(entityType instanceof FabricEntityType))
                continue;

            List<EntityAttributeInstance> attributes = toAttr((ListTag) elements);
            if (attributes.isEmpty())
                continue;

            DefaultAttributeContainer.Builder builder = MobEntity.createMobAttributes();

            for (EntityAttributeInstance attribute : attributes) {
                builder.add(attribute.getAttribute(), attribute.getBaseValue());
            }

            FabricDefaultAttributeRegistry.register(((FabricEntityType) entityType), builder);
        }
    }

    private static List<EntityAttributeInstance> toAttr(ListTag listTag) {
        ArrayList<EntityAttributeInstance> list = new ArrayList<>();

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            String string = compoundTag.getString("Name");
            Optional<EntityAttribute> optional = Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(string));
            if (optional.isPresent()) {
                EntityAttributeInstance instance = new EntityAttributeInstance(optional.get(), x -> {
                });

                try {
                    instance.fromTag(compoundTag);
                    list.add(instance);
                    continue;

                } catch (Exception e) {
                    DashMod.MainLogger.warn(e);
                }
            }

            DashMod.MainLogger.warn("Ignoring unknown attribute '{}'", string);
        }

        return list;
    }
}
