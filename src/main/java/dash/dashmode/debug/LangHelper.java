package dash.dashmode.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class LangHelper {

    private final String id;
    private final Gson gson;

    private final Map<File, JsonObject> langFiles = new HashMap<>();

    public LangHelper(String id) {
        this.id = id;
        gson = new GsonBuilder().setPrettyPrinting().create();

        File langFolder = Paths.get(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString(),
                "../", "src", "main", "resources", "assets", id, "lang").toFile();

        for (File file : langFolder.listFiles()) {
            try {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                JsonObject jsonObject = gson.fromJson(content, JsonObject.class);

                langFiles.put(file, jsonObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fill() {

        List<Boolean> registries = new ArrayList<>();

        registries.add(initBlocksAndItems());
        registries.add(addKeys(Registry.ENTITY_TYPE, "entity_type"));
        registries.add(addKeys(BuiltinRegistries.BIOME, "biome"));
        registries.add(itemGroupCheck());

        if (registries.stream().anyMatch(x -> x)) {
            for (Map.Entry<File, JsonObject> entry : langFiles.entrySet()) {
                String json = gson.toJson(entry.getValue());

                try {
                    FileUtils.writeStringToFile(entry.getKey(), json, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Lang files was patched");
        }
    }

    private boolean itemGroupCheck() {
        List<Identifier> translationKeys = Arrays.stream(ItemGroup.GROUPS)
                .map(x -> new Identifier(x.getName().replace('.', ':')))
                .filter(x -> x.getNamespace() == id)
                .collect(Collectors.toList());

        return addKeys(translationKeys, "itemGroup");
    }

    private boolean initBlocksAndItems() {
        Set<Identifier> blocks = Registry.BLOCK.getEntries().stream()
                .filter(x -> x.getKey().getValue().getNamespace() == id)
                .map(x -> x.getKey().getValue())
                .collect(Collectors.toSet());

        boolean block = addKeys(blocks, "block");

        Set<Identifier> items = Registry.ITEM.getEntries().stream()
                .filter(x -> x.getKey().getValue().getNamespace() == id)
                .map(x -> x.getKey().getValue())
                .collect(Collectors.toSet());

        boolean item = addKeys(items, "item");

        Set<Identifier> toRemove = new HashSet<>(items);

        toRemove.retainAll(blocks);
        items.removeAll(toRemove);

        String message = "";

        for (Map.Entry<File, JsonObject> entry : langFiles.entrySet()) {
            JsonObject jsonObject = entry.getValue();

            for (Identifier identifier : toRemove) {
                String key = String.format("item.%s.%s", identifier.getNamespace(), identifier.getPath());

                if (jsonObject.has(key)) {
                    message += String.format("Item key duplication (ItemBlock): %s", key);
                    jsonObject.remove(key);
                }
            }
        }

        return item || block || !message.isEmpty();
    }

    private <T> boolean addKeys(Registry<T> registry, String prefix) {
        List<Identifier> allItems = registry.getEntries().stream()
                .filter(x -> x.getKey().getValue().getNamespace() == id)
                .map(x -> x.getKey().getValue()).collect(Collectors.toList());

        return addKeys(allItems, prefix);
    }

    private <T> boolean addKeys(Collection<Identifier> allItems, String prefix) {
        String message = "";

        for (Identifier item : allItems) {
            String key = String.format("%s.%s.%s", prefix, item.getNamespace(), item.getPath());

            for (Map.Entry<File, JsonObject> entry : langFiles.entrySet()) {
                JsonObject object = entry.getValue();

                if (!object.has(key)) {
                    message += String.format("Missing key %s in file %s%s", key, entry.getKey().getName(), System.lineSeparator());
                    object.addProperty(key, "");
                }
            }
        }

        if (!message.isEmpty()) {
            System.out.println(message);
            return true;
        }

        return false;
    }
}
