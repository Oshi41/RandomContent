package dash.dashmode.mixin.client;

import dash.dashmode.DashMod;
import dash.dashmode.armor.IArmorSupplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
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
        if (!Registry.ENTITY_TYPE.getId(entityType).getNamespace().equals(DashMod.ModId)) {
            return;
        }

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

        if (entity == null) {
            return;
        }

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

        if (entity instanceof ThrownEntity) {
            Entity owner = world.getEntityById(packet.getEntityData());
            ((ThrownEntity) entity).setOwner(owner);
        }

        ci.cancel();
    }

    @Inject(method = "onEquipmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntityEquipmentUpdateS2CPacket;getEquipmentList()Ljava/util/List;"))
    private void onEquipmentUpdateInject(EntityEquipmentUpdateS2CPacket packet, CallbackInfo ci) {
        Entity entity = world.getEntityById(packet.getId());
        if (!(entity instanceof IArmorSupplier)) {
            return;
        }

        ((IArmorSupplier) entity).refresh();
    }

    @Inject(method = "onBlockEntityUpdate", at = @At("RETURN"))
    private void onBlockEntityUpdateInject(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        int blockEntityType = packet.getBlockEntityType();
        BlockEntityType<?> type = Registry.BLOCK_ENTITY_TYPE.get(blockEntityType);
        if (type == null)
            return;

        BlockPos blockPos = packet.getPos();
        BlockEntity entity = world.getBlockEntity(blockPos);
        if (entity == null || entity.getType() != type)
            return;

        entity.fromTag(world.getBlockState(blockPos), packet.getCompoundTag());
    }
}
