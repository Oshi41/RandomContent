package dash.dashmode.config.interfaces;

import net.minecraft.util.Identifier;

import java.util.List;

public interface IConfig {

    /**
     * List of top most categories
     *
     * @return
     */
    List<IConfigCategory> categories();

    /**
     * Loads from file
     */
    void load();

    /**
     * Save to file
     */
    void save();

    /**
     * Returns ID of file
     *
     * @return
     */
    Identifier getId();
}
