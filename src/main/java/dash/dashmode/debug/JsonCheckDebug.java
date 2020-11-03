package dash.dashmode.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JsonCheckDebug {
    private final Gson gson;

    private final File blockLootFolder;
    private final File entityLootFolder;
    private final String defaultBlockLoot =
            "{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"rolls\": 1,\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"name\": \"%s\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
    private String id;

    public JsonCheckDebug(String id) {
        this.id = id;
        gson = new GsonBuilder().setPrettyPrinting().create();

        blockLootFolder = Paths.get(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString(),
                "../", "src", "main", "resources", "data", id, "loot_tables", "blocks").toFile();

        entityLootFolder = Paths.get(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString(),
                "../", "src", "main", "resources", "data", id, "loot_tables", "entities").toFile();
    }

    public void init() {
        List<Identifier> keys = Registry.BLOCK.getEntries().stream().filter(x -> x.getKey().getValue().getNamespace().equals(id)).map(x -> x.getKey().getValue()).collect(Collectors.toList());

        for (Identifier blockId : keys) {
            Block block = Registry.BLOCK.get(blockId);
            if (block.getLootTableId() == LootTables.EMPTY) {
                continue;
            }

            File file = new File(blockLootFolder, blockId.getPath() + ".json");
            if (!file.exists()) {
                try {
                    System.out.println("Creating default for: " + blockId.toString());
                    String content = String.format(defaultBlockLoot, blockId);
                    FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //keys = Registry.ENTITY_TYPE.getEntries().stream().filter(x -> x.getKey().getValue().getNamespace().equals(id)).map(x -> x.getKey().getValue()).collect(Collectors.toList());

//        for (Identifier entityId : keys) {
//            EntityType<?> type = Registry.ENTITY_TYPE.get(entityId);
//
//            File file = new File(entityLootFolder, entityId.getPath() + ".json");
//            if (!file.exists()) {
//                DashMod.MainLogger.error("Can't locate loot table for: " + entityId);
//            }
//        }
    }
}
