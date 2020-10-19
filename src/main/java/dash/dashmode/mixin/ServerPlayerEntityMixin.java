package dash.dashmode.mixin;

import dash.dashmode.portal.IPortalCooldown;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFromInject(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof IPortalCooldown && this instanceof IPortalCooldown) {
            ((IPortalCooldown) this).copy((IPortalCooldown) oldPlayer);
        }
    }
}
