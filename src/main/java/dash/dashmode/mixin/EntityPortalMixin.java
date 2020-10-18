package dash.dashmode.mixin;

import dash.dashmode.DashMod;
import dash.dashmode.portal.IPortalCooldown;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.registry.DashDimensions;
import dash.dashmode.utils.PositionUtils;
import net.minecraft.class_5459;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.AreaHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityPortalMixin implements IPortalCooldown {
    @Unique
    private final Map<RegistryKey<World>, Integer> tickMap = new HashMap<>();
    @Unique
    private final Map<RegistryKey<World>, BlockPos> posMap = new HashMap<>();

    @Shadow
    public abstract int getMaxNetherPortalTime();

    @Shadow
    public abstract World getEntityWorld();

    @Shadow
    public abstract boolean hasVehicle();

    @Shadow
    public @Nullable
    abstract Entity moveToWorld(ServerWorld destination);

    @Shadow
    public abstract Vec3d getPos();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    protected abstract Optional<class_5459.class_5460> method_30330(ServerWorld serverWorld, BlockPos blockPos, boolean bl);

    @Override
    public void setCooldown(RegistryKey<World> id, int ticks) {
        tickMap.put(id, ticks);
    }

    @Override
    public int getCooldown(RegistryKey<World> id) {
        return tickMap.getOrDefault(id, -1);
    }

    @Override
    public BlockPos getLatestPortalPos(RegistryKey<World> id) {
        return posMap.getOrDefault(id, new BlockPos(getPos()));
    }

    @Override
    public void setLastPortalPos(RegistryKey<World> id, BlockPos pos) {
        posMap.put(id, pos);
    }

    @Override
    public void copy(IPortalCooldown source) {
        EntityPortalMixin mixin = (EntityPortalMixin) source;

        mixin.tickMap.entrySet().stream().filter(x -> x.getValue() > 0)
                .forEach(x -> {
                    setCooldown(x.getKey(), x.getValue());
                });

        mixin.posMap.forEach(this::setLastPortalPos);
    }

    @Inject(method = "tickNetherPortal", at = @At("RETURN"))
    private void tickNetherPortalInject(CallbackInfo ci) {
        final RegistryKey<World> currentlyTravel = tickMap.entrySet().stream().filter(x -> x.getValue() >= 0).map(Map.Entry::getKey).findFirst().orElse(null);
        if (currentlyTravel != null) {
            int maxTime = getMaxNetherPortalTime();
            Integer current = tickMap.get(currentlyTravel);

            if (current >= maxTime) {
                if (!getEntityWorld().isClient()) {
                    changeDimension(currentlyTravel);
                    setLastPortalPos(currentlyTravel, getBlockPos());
                }

                current = 0;
            }

            tickMap.put(currentlyTravel, current - 1);
        }
    }

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTargetInject(ServerWorld destination, CallbackInfoReturnable<@Nullable TeleportTarget> cir) {
        // unknown portal description
        if (!tickMap.containsKey(destination.getRegistryKey()) && !tickMap.containsKey(getEntityWorld().getRegistryKey()))
            return;

        // modded world id
        RegistryKey<World> moddedKey = destination.getRegistryKey();
        if (moddedKey == World.OVERWORLD) {
            moddedKey = getEntityWorld().getRegistryKey();
        }

        // possible portal description
        IPortalDesciption portalDesciption = DashDimensions.getPortal(moddedKey);
        if (portalDesciption == null)
            return;

        Entity entity = (Entity) (Object) this;
        BlockPos portalPosition = PositionUtils.getTeleportPos(destination, entity);

        // searhing for portal
        Optional<class_5459.class_5460> optional = method_30330(destination, portalPosition, false);

        // creating new portal
        if (!optional.isPresent()) {
            optional = destination.getPortalForcer().method_30482(getLatestPortalPos(moddedKey), Direction.Axis.X);
        }

        if (optional.isPresent()) {
            class_5459.class_5460 lv = optional.get();
            TeleportTarget teleportTarget = AreaHelper.method_30484(destination, lv, Direction.Axis.X, new Vec3d(0.5D, 0.0D, 0.0D), entity.getDimensions(entity.getPose()), entity.getVelocity(), entity.yaw, entity.pitch);

            cir.setReturnValue(teleportTarget);
        } else {
            DashMod.MainLogger.warn("Can't create portal for dimension :(");
        }
    }

    private void changeDimension(RegistryKey<World> id) {
        ServerWorld world = ((ServerWorld) getEntityWorld());

        RegistryKey<World> registryKey = world.getRegistryKey();

        if (id == registryKey) {
            id = World.OVERWORLD;
        }

        ServerWorld toTravel = world.getServer().getWorld(id);
        if (toTravel != null && !hasVehicle()) {
            getEntityWorld().getProfiler().push("portal");
            moveToWorld(toTravel);
            getEntityWorld().getProfiler().pop();
        }
    }
}
