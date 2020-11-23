package dash.dashmode.config.configs;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dash.dashmode.DashMod;
import dash.dashmode.config.ConfigBase;
import dash.dashmode.config.interfaces.IConfigCategory;
import dash.dashmode.config.interfaces.IConfigEntry;
import dash.dashmode.config.yamlImpl.IdentifierSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class YamlConfig extends ConfigBase {
    private static final int symbolLength = 25;
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Identifier.class, new IdentifierSerializer());
        mapper.registerModule(module);
    }

    public YamlConfig(List<IConfigCategory> categories, Identifier id) {
        super(id, categories, "yaml");
    }

    @Override
    public void load() {
        String yaml = loadFileContent();
        JsonNode jsonNode;

        try {
            jsonNode = mapper.readTree(yaml);
        } catch (JsonProcessingException e) {
            CrashReport.create(e, "Error during read config");
            return;
        }

        for (IConfigCategory category : categories()) {
            loadCategory(jsonNode, category, mapper);
        }
    }

    @Override
    public void save() {
        YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();

        for (IConfigCategory category : categories()) {
            builder = populateCategory(builder, category);
        }

        saveFileContent(builder.build().toString());
    }

    private YamlMappingBuilder populateCategory(YamlMappingBuilder builder, IConfigCategory category) {
        if (category == null || builder == null) {
            return builder;
        }

        YamlMappingBuilder categoryYaml = Yaml.createYamlMappingBuilder();

        for (IConfigEntry<?> configEntry : category.getValues()) {
            categoryYaml = categoryYaml
                    .add(configEntry.key(), Yaml.createYamlMappingBuilder()
                            .add("value", Yaml.createYamlDump(configEntry.getValue()).dump())
                            .build(StringUtils.join(configEntry.comment().asTruncatedString(Integer.MAX_VALUE).split("\n"), "  " + "\n")
                            ));
        }

        if (!category.getChildren().isEmpty()) {
            YamlMappingBuilder childrenCategoriesYaml = Yaml.createYamlMappingBuilder();

            for (IConfigCategory childCategory : category.getChildren()) {
                childrenCategoriesYaml = populateCategory(childrenCategoriesYaml, childCategory);
            }

            categoryYaml = categoryYaml.add("children", childrenCategoriesYaml.build());
        }

        String catComment = "";
        if (category.comment() != null) {
            catComment = StringUtils.join(category.comment().asTruncatedString(Integer.MAX_VALUE).split("\n"), "  " + "\n");
        }

        return builder.add(category.id(),
                categoryYaml.build(
                        StringUtils.repeat('#', symbolLength) + System.lineSeparator()
                                + category.id().toUpperCase() + System.lineSeparator()
                                + StringUtils.repeat('#', symbolLength) + System.lineSeparator()
                                + System.lineSeparator() + catComment)
        );
    }

    private void loadCategory(JsonNode node, IConfigCategory category, ObjectMapper mapper) {
        if (node == null || category == null)
            return;

        JsonNode categoryNode = node.get(category.id());
        if (categoryNode == null)
            return;

        for (IConfigEntry<?> configEntry : category.getValues()) {
            JsonNode valueNode = categoryNode.get(configEntry.key());
            if (valueNode == null) {
                DashMod.MainLogger.warn(String.format("Can't find node %s from %s", configEntry.key(), categoryNode));
                continue;
            }

            JsonNode rawValueNode = valueNode.get("value");

            Object parsedValue = mapper.convertValue(rawValueNode, configEntry.typeRef());
            configEntry.setValue(parsedValue);
        }

        if (!category.getChildren().isEmpty()) {
            JsonNode childrenNode = categoryNode.get("children");
            for (IConfigCategory child : category.getChildren()) {
                loadCategory(childrenNode, child, mapper);
            }
        }
    }
}
