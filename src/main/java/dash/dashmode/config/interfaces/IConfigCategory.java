package dash.dashmode.config.interfaces;

import net.minecraft.text.Text;

import java.util.List;

public interface IConfigCategory {
    /**
     * Gets ID of category
     *
     * @return
     */
    String id();

    /**
     * Comment for section
     *
     * @return
     */
    Text comment();

    /**
     * Gets linked values to this category
     *
     * @return
     */
    List<IConfigEntry<?>> getValues();

    /**
     * Gets children categories
     *
     * @return
     */
    List<IConfigCategory> getChildren();

    /**
     * Gets parent of category
     * May be null
     *
     * @return
     */
    IConfigCategory getParent();

    /**
     * Sets current category as parent
     *
     * @param category
     * @return
     */
    IConfigCategory withParent(IConfigCategory category);

    /**
     * Sets current categories as children
     *
     * @param categories
     * @return
     */
    IConfigCategory withChildren(IConfigCategory... categories);

    /**
     * Sets current values attacjed to category
     *
     * @param categories
     * @return
     */
    IConfigCategory withChildren(IConfigEntry<?>... categories);
}
