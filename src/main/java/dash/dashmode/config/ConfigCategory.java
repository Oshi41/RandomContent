package dash.dashmode.config;

import dash.dashmode.config.interfaces.IConfigCategory;
import dash.dashmode.config.interfaces.IConfigEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigCategory implements IConfigCategory {
    private final String id;
    private final Text comment;
    private final List<IConfigEntry<?>> values = new ArrayList<>();
    private final List<IConfigCategory> children = new ArrayList<>();
    private IConfigCategory parent;

    public ConfigCategory(String id, Text comment) {
        this.id = id;
        this.comment = comment;
    }

    @Override
    public IConfigCategory getParent() {
        return parent;
    }

    @Override
    public IConfigCategory withParent(IConfigCategory category) {
        parent = category;
        return this;
    }

    @Override
    public IConfigCategory withChildren(IConfigCategory... categories) {
        if (categories != null && categories.length > 0) {
            for (IConfigCategory child : categories) {
                children.add(child.withParent(this));
            }
        }

        return this;
    }

    @Override
    public IConfigCategory withChildren(IConfigEntry<?>... entries) {
        if (entries != null && entries.length > 0) {
            for (IConfigEntry<?> configEntry : entries) {
                values.add(configEntry.withCategory(this));
            }
        }

        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Text comment() {
        return comment;
    }

    @Override
    public List<IConfigEntry<?>> getValues() {
        return values;
    }

    @Override
    public List<IConfigCategory> getChildren() {
        return children;
    }
}
