package dash.dashmode.mixin;

import dash.dashmode.portal.DashPortalForcer;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.registry.DashPortals;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Unique
    private DashPortalForcer portalForcer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void getPortalForcer(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci) {
        IPortalDesciption portal = DashPortals.getPortal(registryKey);
        if (portal == null)
            return;

        portalForcer = new DashPortalForcer((ServerWorld) ((Object) this), portal);
    }

    @Inject(method = "getPortalForcer", at = @At("HEAD"))
    private void getPortalForcerInject(CallbackInfoReturnable<PortalForcer> cir) {
        if (portalForcer != null)
            cir.setReturnValue(portalForcer);
    }
}
