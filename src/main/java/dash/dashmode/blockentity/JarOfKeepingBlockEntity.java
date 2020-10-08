package dash.dashmode.blockentity;

import dash.dashmode.registry.DashBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class JarOfKeepingBlockEntity extends BlockEntity implements Tickable {
    /**
     * Tag for item stack contatinig blockitem information
     */
    public static final String BlockItemTag = "BlockEntityTag";

    /**
     * Chance of random breaking
     */
    public static final String BreakChanceTag = "breakChance";

    /**
     * Chance of catching entity
     */
    public static final String CatchChanceTag = "catchChance";

    /**
     * Tag with caged entity info
     */
    public static final String EntityTag = "entity";

    private int breakChance;
    private int catchChance;
    private CompoundTag entity = new CompoundTag();

    public JarOfKeepingBlockEntity() {
        super(DashBlockEntities.JarOfKeepingBlockEntityType);
    }

    public JarOfKeepingBlockEntity(int breakChance, int catchChance) {
        this();
        this.breakChance = breakChance;
        this.catchChance = catchChance;
    }

    @Override
    public void tick() {
        // jar can be broken
        if (breakChance > 0) {
            if (!entity.isEmpty() && getWorld() != null && getWorld().random.nextInt(breakChance) == 0) {
                getWorld().setBlockState(getPos(), Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag result = super.toTag(tag);

        result.put(JarOfKeepingBlockEntity.EntityTag, entity);
        result.putInt(JarOfKeepingBlockEntity.BreakChanceTag, breakChance);
        result.putInt(JarOfKeepingBlockEntity.CatchChanceTag, catchChance);

        return result;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        entity = tag.getCompound(JarOfKeepingBlockEntity.EntityTag);
        breakChance = tag.getInt(JarOfKeepingBlockEntity.BreakChanceTag);
        catchChance = tag.getInt(JarOfKeepingBlockEntity.CatchChanceTag);
    }
}
