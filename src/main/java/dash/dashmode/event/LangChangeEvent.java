package dash.dashmode.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Language;

@FunctionalInterface
public interface LangChangeEvent {
    Event<LangChangeEvent> LangChanges = EventFactory.createArrayBacked(LangChangeEvent.class,
            listeners -> key -> {
                Language instance = Language.getInstance();

                for (LangChangeEvent event : listeners) {
                    event.onLanguageChanged(instance);
                }
            }
    );

    void onLanguageChanged(Language lang);
}
