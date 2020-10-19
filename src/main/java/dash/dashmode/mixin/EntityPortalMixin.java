package dash.dashmode.mixin;

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
import java.util.Set;

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
    protected abstract Optional<class_5459.class_5460> method_30330(ServerWorld serverWorld, BlockPos blockPos, boolean bl);

    @Shadow
    public abstract int getDefaultNetherPortalCooldown();

    @Override
    public void setTickInPortal(RegistryKey<World> id, int ticks) {
        tickMap.put(id, ticks);
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
        cooldownMap.put(id, ticks);
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
        // current ticking portal
        Set<RegistryKey<World>> ticks = tickMap.keySet();
        // max cooldown
        int maxNetherPortalTime = Math.max(200, getMaxNetherPortalTime());

        for (RegistryKey<World> key : ticks) {
            // have cooldown for current portal
            if (cooldownMap.containsKey(key))
                continue;

            // getting current tick amount
            int tick = tickMap.get(key);

            // can travel
            if (tick >= maxNetherPortalTime) {
                // remember current portal pos
                setLastPortalPos(key, getBlockPos());
                // set cooldown to prevent infinite travel routine
                setCooldown(key, getDefaultNetherPortalCooldown());

                // cahnge dimension onserver only
                if (!getEntityWorld().isClient()) {
                    changeDimension(key);
                }

                // removing current id from ticks
                tickMap.remove(key);
                // return because we did all work
                return;
            }


            if (tick <= 0) {
                // tick is ended, remove from map
                tickMap.remove(key);
            } else {
                // remove tick if stand away from portal
                tickMap.put(key, tick - 1);
            }
        }

        // iterate through cooldowns strictly after regular tick
        Set<RegistryKey<World>> coolDown = cooldownMap.keySet();

        for (RegistryKey<World> key : coolDown) {
            int value = cooldownMap.get(key) - 1;

            if (value > 0) {
                // decay cooldown
                cooldownMap.put(key, value);
            } else {
                // finished cooldown
                coolDown.remove(key);
            }
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
