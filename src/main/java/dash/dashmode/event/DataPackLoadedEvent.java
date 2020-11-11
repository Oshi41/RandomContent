package dash.dashmode.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.registry.DynamicRegistryManager;

@FunctionalInterface
public interface DataPackLoadedEvent {
    Event<DataPackLoadedEvent> EVENT = EventFactory.createArrayBacked(DataPackLoadedEvent.class,
            listeners -> manager -> {
                for (DataPackLoadedEvent event : listeners) {
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
