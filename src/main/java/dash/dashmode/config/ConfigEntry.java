package dash.dashmode.config;

import com.fasterxml.jackson.core.type.TypeReference;
import dash.dashmode.DashMod;
import dash.dashmode.config.interfaces.IConfigCategory;
import dash.dashmode.config.interfaces.IConfigEntry;
import net.minecraft.text.Text;

import java.util.function.Function;

public class ConfigEntry<T> implements IConfigEntry<T> {
    private final String key;
    private final Text comment;
    private final TypeReference<T> clazz;
    private final Function<T, T> validateFunc;

    private IConfigCategory category;
    private T value;

    public ConfigEntry(IConfigCategory category, String key, Text comment, TypeReference<T> clazz, Function<T, T> validateFunc, T defaultValue) {
        category.withChildren(this);
        this.key = key;
        this.comment = comment;
        this.clazz = clazz;

        if (validateFunc == null) {
            validateFunc = t -> t;
        }

        this.validateFunc = validateFunc;

        setValue(defaultValue);
    }

    @Override
    public IConfigCategory category() {
        return category;
    }

    @Override
    public IConfigEntry<T> withCategory(IConfigCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(Object val) {
        if (val == null) {
            this.value = null;
            return;
        }

        try {
            this.value = (T) val;
        } catch (Exception e) {
            DashMod.MainLogger.warn(String.format("Error during populate config\n origing value: %s\npopulating value^ %s", value, val));
        }
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Text comment() {
        return comment;
    }

    @Override
    public TypeReference<T> typeRef() {
        return clazz;
    }

    @Override
    public T validate(T source) {
        return validateFunc.apply(source);
    }
}
