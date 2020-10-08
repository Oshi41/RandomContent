package dash.dashmode.mixin;

import dash.dashmode.DashMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onEntitySpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER),
            cancellable = true)
    public void onEntitySpawnInject(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        EntityType<?> entityType = packet.getEntityTypeId();

        // Entity is not belong to our mod
        if (!Registry.ENTITY_TYPE.getId(entityType).getNamespace().equals(DashMod.ModId))
            return;

        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        Entity entity;

        try {
            entity = entityType.create(world);
        } catch (Exception ex) {
            DashMod.MainLogger.error(ex);
            return;
        }

        if (entity == null)
            return;

        int i = packet.getId();
        entity.updateTrackedPosition(d, e, f);
        entity.refreshPositionAfterTeleport(d, e, f);
        entity.pitch = (float) (packet.getPitch() * 360) / 256.0F;
        entity.yaw = (float) (packet.getYaw() * 360) / 256.0F;
        entity.setEntityId(i);
        entity.setUuid(packet.getUuid());
        this.world.addEntity(i, entity);
        if (entity instanceof AbstractMinecartEntity) {
            client.getSoundManager().play(new MovingMinecartSoundInstance((AbstractMinecartEntity) entity));
        }

        ci.cancel();
    }
}
