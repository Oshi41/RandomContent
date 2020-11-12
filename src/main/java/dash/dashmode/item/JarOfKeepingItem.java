package dash.dashmode.item;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.entities.throwable.JarOfKeepingThrowableEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class JarOfKeepingItem extends BlockItem {
    private final boolean isEnhanced;

    public JarOfKeepingItem(Block block, Settings settings, boolean isEnhanced) {
        super(block, settings.maxCount(1));
        this.isEnhanced = isEnhanced;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> use = super.use(world, user, hand);

        if (use.getResult() != ActionResult.PASS) {
            return use;
        }

        ItemStack itemStack = use.getValue();
        CompoundTag tag = itemStack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag);

        // Already have entity inside
        if (tag != null && !tag.isEmpty() && EntityType.fromTag(tag.getCompound(JarOfKeepingBlockEntity.EntityTag)).isPresent()) {

            if (user.isSneaking()) {
                HitResult hitResult = user.raycast(5, 1, false);
                Vec3d pos = hitResult.getPos();
                if (pos == null) {
                    pos = user.getPos();
                }

                spawnEntity(world, itemStack, new BlockPos(pos));
            }

            return use;
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
        if (!world.isClient) {
            ThrownItemEntity potionEntity = new JarOfKeepingThrowableEntity(world, user);
            potionEntity.setItem(itemStack);
            potionEntity.setProperties(user, user.pitch, user.yaw, -20.0F, 0.5F, 1.0F);
            world.spawnEntity(potionEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public ItemStack getDefaultStack() {
        return fillStack(super.getDefaultStack());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(fillStack(stack), world, player);
    }

    private ItemStack fillStack(ItemStack stack) {
        CompoundTag subTag = stack.getOrCreateSubTag(JarOfKeepingBlockEntity.BlockItemTag);

        if (!subTag.contains(JarOfKeepingBlockEntity.BreakChanceTag))
            subTag.putInt(JarOfKeepingBlockEntity.BreakChanceTag, isEnhanced ? -1 : 20 * 60 * 30);

        if (!subTag.contains(JarOfKeepingBlockEntity.CatchChanceTag))
            subTag.putInt(JarOfKeepingBlockEntity.CatchChanceTag, isEnhanced ? -1 : 10);

        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        CompoundTag tag = stack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag);
        if (tag != null && !tag.isEmpty()) {
            CompoundTag entityTag = tag.getCompound(JarOfKeepingBlockEntity.EntityTag);

            if (!entityTag.isEmpty()) {
                int breakChance = tag.getInt(JarOfKeepingBlockEntity.BreakChanceTag);
                Random random = world.random;

                if (breakChance > 0 && random.nextInt(breakChance) == 0) {

                    if (!world.isClient()) {
                        spawnEntity(world, stack, new BlockPos(entity.getPos().add(
                                (random.nextFloat() * 2) - 1,
                                (random.nextFloat() * 2) - 1,
                                (random.nextFloat() * 2) - 1
                        )));
                    }

                    tag.put(JarOfKeepingBlockEntity.EntityTag, new CompoundTag());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        CompoundTag tag = stack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag);
        if (tag != null && !tag.isEmpty()) {
            CompoundTag entity = tag.getCompound(JarOfKeepingBlockEntity.EntityTag);
            EntityType.fromTag(entity).ifPresent(entityType -> tooltip.add(entityType.getName()));

            int breakChance = tag.getInt(JarOfKeepingBlockEntity.BreakChanceTag);
            if (breakChance > 0) {
                TranslatableText text = new TranslatableText("random_content.jar_of_keeping.can_escape");
                text.setStyle(text.getStyle().withColor(Formatting.RED));
                tooltip.add(text);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public EntityType<?> getEntityType(ItemStack stack) {
        if (stack != null) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                CompoundTag blockTag = tag.getCompound(JarOfKeepingBlockEntity.BlockItemTag);
                if (blockTag != null) {
                    CompoundTag entityTag = blockTag.getCompound(JarOfKeepingBlockEntity.EntityTag);
                    if (entityTag != null) {
                        Optional<EntityType<?>> optional = EntityType.fromTag(entityTag);
                        if (optional.isPresent()) {
                            return optional.get();
                        }
                    }
                }
            }
        }

        return null;
    }

    private void spawnEntity(World world, ItemStack stack, BlockPos pos) {
        CompoundTag subTag = stack.getOrCreateSubTag(JarOfKeepingBlockEntity.BlockItemTag);
        CompoundTag entityTag = subTag.getCompound(JarOfKeepingBlockEntity.EntityTag);
        subTag.put(JarOfKeepingBlockEntity.EntityTag, new CompoundTag());

        if (!entityTag.isEmpty()) {
            Optional<EntityType<?>> type = EntityType.fromTag(entityTag);
            if (type.isPresent() && !world.isClient()) {
                Entity entity = type.get().create(world);
                entity.setPos(pos.getX(), pos.getY(), pos.getX());
                world.spawnEntity(entity);
            }
        }
    }
}
