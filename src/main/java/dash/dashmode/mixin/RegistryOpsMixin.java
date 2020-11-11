package dash.dashmode.mixin;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dash.dashmode.DashMod;
import dash.dashmode.event.DataPackLoadedEvent;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryOps.class)
public class RegistryOpsMixin {
    @Inject(method = "of(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/util/dynamic/RegistryOps$EntryLoader;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)Lnet/minecraft/util/dynamic/RegistryOps;",
            at = @At("RETURN"))
    private static <T> void loadInject(DynamicOps<T> dynamicOps, RegistryOps.EntryLoader entryLoader, DynamicRegistryManager.Impl impl, CallbackInfoReturnable<RegistryOps<T>> cir) {
        if (dynamicOps != JsonOps.INSTANCE)
            return;

        if (impl.getDimensionTypes().getIds().stream().noneMatch(x -> x.getNamespace().equals(DashMod.ModId)))
            return;

        DataPackLoadedEvent.EVENT.invoker().afterLoading(impl);
    }
}
