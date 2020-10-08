package dash.dashmode.registry;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashBlockEntities {
    public static final BlockEntityType<JarOfKeepingBlockEntity> JarOfKeepingBlockEntityType;

    static {
        JarOfKeepingBlockEntityType = BlockEntityType.Builder.create(JarOfKeepingBlockEntity::new, DashBlocks.JarOfKeeping).build(null);
    }

    public static void init(String modid) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modid, "jar_of_keeping_block"), JarOfKeepingBlockEntityType);
    }
}
