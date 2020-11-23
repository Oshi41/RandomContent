package dash.dashmode.utils;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlDump;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class YamlReflectUtils {
    /**
     * Changing scalar types for yaml serializer
     *
     * @param classes
     * @return
     */
    public static boolean patchStringClasses(Class... classes) {
        // loading classes
        YamlDump yamlDump = Yaml.createYamlDump(null);
        try {
            Class<?> clazz = Class.forName("com.amihaiemil.eoyaml.ReflectedYamlDump");
            Field scalarTypes = clazz.getDeclaredField("SCALAR_TYPES");

            scalarTypes.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(scalarTypes, scalarTypes.getModifiers() & ~Modifier.FINAL);

            List<Class> list = new ArrayList<>((List<Class>) scalarTypes.get(null));
            list.addAll(Arrays.stream(classes).collect(Collectors.toList()));
            list = list.stream().distinct().collect(Collectors.toList());

            scalarTypes.set(null, list);
            return true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
