package dash.dashmode.config.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dash.dashmode.config.ValueInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReport;

import java.util.List;

public class JsonEngine implements IoEngine {
    /**
     * GSON parser object
     */
    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .setPrettyPrinting()
            .create();

    @Override
    public void read(String fileContent, Object instance, List<ValueInfo> fields) {

        JsonObject jsonObject = load(fileContent);

        for (ValueInfo valueInfo : fields) {
            JsonObject category = jsonObject.getAsJsonObject(valueInfo.propertyInfo.category());
            if (category == null)
                continue;

            JsonObject valueObj = category.getAsJsonObject(valueInfo.getKey());
            if (valueObj == null)
                continue;

            JsonElement rawValue = valueObj.get("value");
            Object parsedValue = gson.fromJson(rawValue, valueInfo.propertyField.getType());
            valueInfo.setValue(instance, parsedValue);
        }
    }

    @Override
    public String toWrite(Object instance, List<ValueInfo> fields) {
        JsonObject configJson = toJson(instance, fields);
        String jsonContent = gson.toJson(configJson);
        return jsonContent;
    }

    public JsonObject toJson(Object instance, List<ValueInfo> fields) {
        JsonObject configJson = new JsonObject();

        for (ValueInfo valueInfo : fields) {
            if (!configJson.has(valueInfo.propertyInfo.category())) {
                configJson.add(valueInfo.propertyInfo.category(), new JsonObject());
            }

            JsonObject categoryJson = configJson.getAsJsonObject(valueInfo.propertyInfo.category());
            String valueKey = valueInfo.getKey();
            if (categoryJson.has(valueKey)) {
                CrashReport.create(new Exception("duplicate key:" + valueKey), String.format("Duplicating key (%s) in category (%s)", valueKey, categoryJson));
            }

            JsonObject fieldObject = new JsonObject();
            categoryJson.add(valueKey, fieldObject);

            JsonElement element = gson.toJsonTree(valueInfo.getValue(instance), valueInfo.propertyField.getType());

            fieldObject.add("value", element);
            List<String> comments = valueInfo.getComment();
            for (int i = 0; i < comments.size(); i++) {
                fieldObject.addProperty("_comment" + (i + 1), comments.get(i));
            }
        }

        return configJson;
    }

    public JsonObject load(String content) {
        return gson.fromJson(content, JsonObject.class);
    }

    @Override
    public String getExt() {
        return "json";
    }
}
