package dash.dashmode.debug;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AttributesHelper {
    private final String modid;
    private final File statsFolder;
    private boolean forced;

    public AttributesHelper(String modid, boolean forced) {
        this.modid = modid;
        statsFolder = Paths.get(FabricLoader.getInstance().getGameDir().toAbsolutePath().toString(),
                "../", "src", "main", "resources", "data", modid, "stats").toFile();
        this.forced = forced;
    }

    public void init() {
        Map<Identifier, DefaultAttributeContainer.Builder> map = getDefaultAttributes();

        for (Map.Entry<Identifier, DefaultAttributeContainer.Builder> entry : map.entrySet()) {
            Identifier key = entry.getKey();

            Object container = entry.getValue().build();
            if (!(container instanceof Supplier)) {
                continue;
            }

            ListTag listTag = ((Supplier<ListTag>) container).get();
            File file = new File(statsFolder, key.getPath() + ".json");

            if (file.exists() && !forced)
                continue;

            try {
                FileUtils.writeStringToFile(file, listTag.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Identifier, DefaultAttributeContainer.Builder> getDefaultAttributes() {
        Map<Identifier, DefaultAttributeContainer.Builder> attributes = new HashMap<Identifier, DefaultAttributeContainer.Builder>() {{
            put(new Identifier(modid, "paper_zombie"), ZombieEntity.createZombieAttributes());
            put(new Identifier(modid, "paper_cow"), CowEntity.createCowAttributes());
            put(new Identifier(modid, "cosmo_ghast"), GhastEntity.createGhastAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 50));
        }};

        return attributes;
    }
}
