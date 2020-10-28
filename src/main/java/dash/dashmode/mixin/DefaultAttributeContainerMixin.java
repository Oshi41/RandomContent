package dash.dashmode.mixin;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(DefaultAttributeContainer.class)
public class DefaultAttributeContainerMixin implements Supplier<ListTag> {
    @Shadow
    @Final
    private Map<EntityAttribute, EntityAttributeInstance> instances;

    @Override
    public ListTag get() {
        ListTag tag = new ListTag();

        for (EntityAttributeInstance attr : instances.values()) {
            CompoundTag compoundTag = attr.toTag();
            compoundTag.putString("_comment", I18n.translate(attr.getAttribute().getTranslationKey()));
            tag.add(compoundTag);
        }

        return tag;
    }
}
