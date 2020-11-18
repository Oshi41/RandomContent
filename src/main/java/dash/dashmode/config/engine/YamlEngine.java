package dash.dashmode.config.engine;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import dash.dashmode.config.ValueInfo;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlEngine implements IoEngine {
    @Override
    public void read(String fileContent, Object instance, List<ValueInfo> fields) {
        YamlMapping yamlMapping;
        try {
            yamlMapping = Yaml.createYamlInput(fileContent).readYamlMapping();
        } catch (IOException e) {
            CrashReport.create(e, "Error during config reading");
            return;
        }

        Map<String, List<ValueInfo>> categories = fields.stream().collect(Collectors.groupingBy(x -> x.propertyInfo.category()));
        for (Map.Entry<String, List<ValueInfo>> entry : categories.entrySet()) {
            YamlMapping category = yamlMapping.value(entry.getKey()).asMapping();

            for (ValueInfo info : entry.getValue()) {
                YamlMapping yamlInfo = category.value(info.getKey()).asMapping();
                YamlNode yamlInfoValue = yamlInfo.value("value");

                // todo читаем настройки
            }
        }
    }

    @Override
    public String toWrite(Object instance, List<ValueInfo> fields) {
        YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();

        Map<String, List<ValueInfo>> categories = fields.stream().collect(Collectors.groupingBy(x -> x.propertyInfo.category()));
        for (Map.Entry<String, List<ValueInfo>> entry : categories.entrySet()) {
            YamlMappingBuilder category = Yaml.createYamlMappingBuilder();

            for (ValueInfo info : entry.getValue()) {
                Object value = info.getValue(instance);
                YamlNode yamlNode = Yaml.createYamlDump(value).dump();

                category = category.add(info.getKey(), Yaml.createYamlMappingBuilder()
                        .add("value", yamlNode)
                        .build(StringUtils.join(info.getComment(), "\n# ")));
            }

            builder = builder.add(entry.getKey(), category.build(
                    StringUtils.repeat('#', 25) + System.lineSeparator()
                            + entry.getKey().toUpperCase() + System.lineSeparator()
                            + StringUtils.repeat('#', 25) + System.lineSeparator() + System.lineSeparator()
            ));

        }

        return builder.build().toString();
    }

    @Override
    public String getExt() {
        return "yaml";
    }
}
