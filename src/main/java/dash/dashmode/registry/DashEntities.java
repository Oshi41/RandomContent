package dash.dashmode.registry;

import dash.dashmode.entity.JarOfKeepingThrowableEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashEntities {
    public static final EntityType<JarOfKeepingThrowableEntity> JarOfKeepingThrowableEntityType;

    static {
        JarOfKeepingThrowableEntityType = FabricEntityTypeBuilder.<JarOfKeepingThrowableEntity>create(SpawnGroup.MISC, (type, world) -> new JarOfKeepingThrowableEntity(world))
                .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                .trackRangeBlocks(4)
                .trackedUpdateRate(4)
                .build();
    }


    public static void init(String modid) {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(modid, "jar_of_keeping_throwable"), JarOfKeepingThrowableEntityType);
    }
}
