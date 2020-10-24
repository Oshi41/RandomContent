package dash.dashmode.mixin;

import com.google.common.collect.Sets;
import dash.dashmode.DashMod;
import dash.dashmode.portal.IPortalCooldown;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.portal.IPortalForcer;
import dash.dashmode.registry.DashDimensions;
import dash.dashmode.utils.PositionUtils;
import net.minecraft.class_5459;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
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
    @Unique
    private final Map<RegistryKey<World>, Integer> cooldownMap = new HashMap<>();

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
    public abstract int getDefaultNetherPortalCooldown();

    @Override
    public void setTickInPortal(RegistryKey<World> id, int ticks) {
        if (ticks < 0) {
            tickMap.remove(id);
        } else {
            tickMap.put(id, ticks);
        }
    }

    @Override
    public int getTickInPortal(RegistryKey<World> id) {
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
    public void setCooldown(RegistryKey<World> id, int ticks) {
        if (ticks <= 0) {
            cooldownMap.remove(id);
        } else {
            cooldownMap.put(id, ticks);
        }
    }

    @Override
    public int getCoolDown(RegistryKey<World> id) {
        return cooldownMap.getOrDefault(id, 0);
    }

    @Override
    public void copy(IPortalCooldown source) {
        if (source instanceof EntityPortalMixin) {
            EntityPortalMixin mixin = (EntityPortalMixin) source;

            mixin.posMap.forEach(this::setLastPortalPos);
            mixin.tickMap.forEach(this::setTickInPortal);
            mixin.cooldownMap.forEach(this::setCooldown);
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFromInject(Entity original, CallbackInfo ci) {
        if (original instanceof IPortalCooldown) {
            copy(((IPortalCooldown) original));
        }
    }

    @Inject(method = "tickNetherPortal", at = @At("RETURN"))
    private void tickNetherPortalInject(CallbackInfo ci) {

        int portalTick = Math.max(200, getMaxNetherPortalTime());
        int portalCooldown = Math.max(200, getDefaultNetherPortalCooldown());

        for (RegistryKey<World> key : Sets.union(tickMap.keySet(), cooldownMap.keySet())) {
            tickEntity(key);

            if (getTickInPortal(key) >= portalTick) {
                setLastPortalPos(key, getBlockPos());
                tickMap.clear();
                setCooldown(key, portalCooldown);

                // cahnge dimension onserver only
                if (!getEntityWorld().isClient()) {
                    changeDimension(key);
                }

                return;
            }
        }
    }

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTargetInject(ServerWorld destination, CallbackInfoReturnable<@Nullable TeleportTarget> cir) {
        // unknown portal description
        if (!cooldownMap.containsKey(destination.getRegistryKey()) && !cooldownMap.containsKey(getEntityWorld().getRegistryKey()))
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

        PortalForcer forcer = destination.getPortalForcer();
        if (!(forcer instanceof IPortalForcer))
            return;

        Optional<class_5459.class_5460> optional = ((IPortalForcer) forcer).tryFindOrCreate(entity, portalDesciption, portalPosition, false);

        if (!optional.isPresent()) {
            DashMod.MainLogger.warn("Can't create portal for dimension :(");
            return;
        }

        class_5459.class_5460 lv = optional.get();
        TeleportTarget teleportTarget = PositionUtils.getTeleportTarget(lv, entity);
        cir.setReturnValue(teleportTarget);
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
