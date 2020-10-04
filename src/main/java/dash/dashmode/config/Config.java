package dash.dashmode.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Language;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Config<T> {
    /**
     * Type of config. Should be with open ctor
     */
    private final Class<T> clazz;

    /**
     * name of config
     */
    private final String modId;

    /**
     * GSON parser object
     */
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    /**
     * List of annotated fields
     */
    private final Map<Field, Property> fields = new HashMap<>();

    /**
     * Linked file
     */
    private final File configFile;

    /**
     * Singletone config object
     */
    private T instance;

    /**
     * @param clazz - class for config object. Should have parameterless ctor.
     * @param modId - name of file inside configs folder.
     */
    public Config(Class<T> clazz, String modId) {
        this.clazz = clazz;
        this.modId = modId;

        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            CrashReport.create(e, "Config loading error");
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Property.class)) {
                continue;
            }

            Property annotation = field.getAnnotation(Property.class);
            fields.put(field, annotation);
        }

        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), modId + ".json");
    }

    /**
     * Returns instance of stored config
     *
     * @return
     */
    public T getConfig() {
        return instance;
    }

    /**
     * Populate config object from file
     * Without comments!!! The correct lang will apper later
     */
    public void read() {
        configFile.getParentFile().mkdirs();

        if (configFile.exists()) {
            try {
                readFromJson(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();

                CrashReport.create(e, "Config reading error");
            }
        } else {
            writeToFile();
        }
    }

    /**
     * Write current config to
     */
    private void writeToFile() {
        configFile.getParentFile().mkdirs();

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

                CrashReport.create(e, "Config write to file error");
            }
        }

        JsonObject configJsonObj = new JsonObject();

        for (Map.Entry<Field, Property> entry : fields.entrySet()) {
            Field field = entry.getKey();
            Property property = entry.getValue();

            JsonObject category = configJsonObj.getAsJsonObject(property.category());
            if (category == null) {
                category = new JsonObject();
                configJsonObj.add(property.category(), category);
            }

            String key = getKey(property, field);

            if (category.has(key)) {
                CrashReport.create(new Exception("duplicate key:" + key), String.format("Duplicating key (%s) in category (%s)", key, category));
            }

            JsonObject fieldObject = new JsonObject();
            category.add(key, fieldObject);

            try {
                Object fieldValue = field.get(instance);
                fieldObject.addProperty("value", gson.toJson(fieldValue));
                fieldObject.addProperty("_comment", getComment(property));
            } catch (IllegalAccessException e) {
                e.printStackTrace();

                CrashReport.create(e, "Error during reading config");
            }
        }

        String jsonContent = gson.toJson(configJsonObj);
        try {
            FileUtils.writeStringToFile(configFile, jsonContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();

            CrashReport.create(e, "Config write to file error");
        }
    }

    private void readFromJson(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        for (Map.Entry<Field, Property> entry : fields.entrySet()) {
            Property property = entry.getValue();
            Field field = entry.getKey();

            // missing category
            JsonObject category = jsonObject.getAsJsonObject(property.category());
            if (category == null) {
                continue;
            }

            // key or field name
            String key = getKey(property, field);

            // missing key
            if (!category.has(key)) {
                continue;
            }

            // get field as json
            JsonObject fieldValue = category.get(key).getAsJsonObject();
            JsonElement value = fieldValue.get("value");

            if (value == null) {
                continue;
            }

            // parse actual value
            Object parsedValue = gson.fromJson(value, field.getType());

            // trying to inject to object
            try {
                field.set(instance, parsedValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();

                CrashReport.create(e, "Config reading error");
            }
        }
    }

    private String getKey(Property property, Field field) {
        String key = property.key();

        if (key.isEmpty()) {
            key = field.getName();
        }

        return key;
    }

    private String getComment(Property property) {
        if (property.commentLangKey().isEmpty())
            return property.comment();


        return Language.getInstance().get(property.commentLangKey());
    }
}
