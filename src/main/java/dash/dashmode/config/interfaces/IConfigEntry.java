package dash.dashmode.config.interfaces;

import dash.dashmode.config.yamlImpl.TypeRef;
import net.minecraft.text.Text;

public interface IConfigEntry<T> {

    /**
     * Current category
     *
     * @return
     */
    IConfigCategory category();

    /**
     * Set category as parent
     *
     * @param category
     * @return
     */
    IConfigEntry<T> withCategory(IConfigCategory category);

    /**
     * Get value from config
     *
     * @return
     */
    T getValue();

    /**
     * Set value to config
     *
     * @param val
     */
    void setValue(Object val);

    /**
     * Key of entry
     *
     * @return
     */
    String key();

    /**
     * Comment for entry
     *
     * @return
     */
    Text comment();

    /**
     * Actual field class
     *
     * @return
     */
    TypeRef<T> typeRef();

    /**
     * Validates value from config
     *
     * @param source - raw data from config
     * @return
     */
    T validate(T source);
}
