package dash.dashmode.config.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dash.dashmode.DashMod;
import dash.dashmode.config.ConfigBase;
import dash.dashmode.config.interfaces.IConfigCategory;
import dash.dashmode.config.interfaces.IConfigEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class JsonConfig extends ConfigBase {
    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .setPrettyPrinting()
            .create();

    public JsonConfig(List<IConfigCategory> categories, Identifier id) {
        super(id, categories, "json");
    }

    @Override
    public void load() {
        JsonObject jsonObject = gson.fromJson(loadFileContent(), JsonObject.class);

        for (IConfigCategory category : categories()) {
            readCategory(jsonObject, category);
        }
    }

    private void readCategory(JsonObject object, IConfigCategory category) {
        JsonObject categoryJson = object.getAsJsonObject(category.id());
        if (categoryJson == null) {
            DashMod.MainLogger.warn("Category is missing: " + category.id() + "\nFrom json " + object.toString());
            return;
        }

        for (IConfigEntry<?> configEntry : category.getValues()) {
            JsonElement elementJson = categoryJson.get(configEntry.key());
            if (elementJson == null || !elementJson.isJsonObject()) {
                DashMod.MainLogger.warn("Entry key is missing: " + configEntry.key() + "\nFrom json " + categoryJson.toString());
                continue;
            }

            Object fromJson = gson.fromJson(elementJson.getAsJsonObject().get("value"), configEntry.typeRef().getType());
            configEntry.setValue(fromJson);
        }

        for (IConfigCategory child : category.getChildren()) {
            readCategory(categoryJson, child);
        }
    }

    @Override
    public void save() {
        JsonObject result = new JsonObject();

        for (IConfigCategory category : categories()) {
            writeCategory(result, category);
        }

        saveFileContent(gson.toJson(result));
    }


    private void writeCategory(JsonObject object, IConfigCategory category) {
        JsonObject categoryJson = new JsonObject();
        object.add(category.id(), categoryJson);

        addComment(categoryJson, category.comment());

        for (IConfigEntry<?> configEntry : category.getValues()) {
            JsonObject valueConfigJson = new JsonObject();
            categoryJson.add(configEntry.key(), valueConfigJson);

            addComment(valueConfigJson, configEntry.comment());
            valueConfigJson.add("value", gson.toJsonTree(configEntry.getValue(), configEntry.typeRef().getType()));
        }

        for (IConfigCategory child : category.getChildren()) {
            writeCategory(categoryJson, child);
        }
    }

    private void addComment(JsonObject object, Text comment) {
        if (object == null || comment == null) {
            return;
        }

        String[] commentLines = comment.asTruncatedString(Integer.MAX_VALUE).split(System.lineSeparator());
        if (commentLines.length == 0) {
            return;
        }

        for (int i = 0; i < commentLines.length; i++) {
            if (commentLines[i] == null || commentLines[i].length() < 1) {
                continue;
            }

            String id = "_comment";
            if (i > 0) {
                id += i;
            }

            object.addProperty(id, commentLines[i]);
        }
    }
}
