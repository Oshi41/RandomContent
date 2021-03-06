package dash.dashmode.registry;

import dash.dashmode.blockentity.InfiniteFurnaceBlockEntity;
import dash.dashmode.blockentity.InfiniteShulkerBoxBlockEntity;
import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.blockentity.StackHolderBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DashBlockEntities {
    public static final BlockEntityType<JarOfKeepingBlockEntity> JarOfKeepingBlockEntityType;
    public static final BlockEntityType<InfiniteFurnaceBlockEntity> InfiniteFurnace;
    public static final BlockEntityType<StackHolderBlockEntity> StackHolder;
    public static final BlockEntityType<InfiniteShulkerBoxBlockEntity> InfiniteShulker;

    static {
        JarOfKeepingBlockEntityType = BlockEntityType.Builder.create(JarOfKeepingBlockEntity::new, DashBlocks.JarOfKeeping, DashBlocks.PerfectJarOfKeeping).build(null);
        InfiniteFurnace = BlockEntityType.Builder.create(InfiniteFurnaceBlockEntity::new, DashBlocks.InfiniteFurnace).build(null);
        StackHolder = BlockEntityType.Builder.create(StackHolderBlockEntity::new, DashBlocks.Pillar).build(null);
        InfiniteShulker = BlockEntityType.Builder.create(InfiniteShulkerBoxBlockEntity::new, DashBlocks.InfiniteShulker).build(null);
    }

    public static void init(String modid) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modid, "jar_of_keeping_block"), JarOfKeepingBlockEntityType);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modid, "infinite_furnace"), InfiniteFurnace);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modid, "stack_holder"), StackHolder);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modid, "infinite_shulker_box"), InfiniteShulker);
    }
}
