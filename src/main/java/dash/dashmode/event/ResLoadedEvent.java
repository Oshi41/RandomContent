package dash.dashmode.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.registry.DynamicRegistryManager;

@FunctionalInterface
public interface ResLoadedEvent {
    Event<ResLoadedEvent> EVENT = EventFactory.createArrayBacked(ResLoadedEvent.class,
            listeners -> manager -> {
                for (ResLoadedEvent event : listeners) {
                    event.afterLoading(manager);
                }
            }
    );

    /**
     * Called after resource loaded
     *
     * @param manager
     */
    void afterLoading(DynamicRegistryManager.Impl manager);
}
