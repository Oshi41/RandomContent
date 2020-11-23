package dash.dashmode.config;

import dash.dashmode.config.interfaces.IConfig;
import dash.dashmode.config.interfaces.IConfigCategory;
import dash.dashmode.event.LangChangeEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class ConfigBase implements IConfig {
    private final Identifier id;
    private final List<IConfigCategory> categories;
    private final String ext;
    private final Charset charset = StandardCharsets.UTF_8;

    protected ConfigBase(Identifier id, List<IConfigCategory> categories, String ext) {
        this.id = id;
        this.categories = categories;
        this.ext = ext;

        LangChangeEvent.LangChanges.register(lang -> save());
    }

    protected String loadFileContent() {
        File file = getFile();
        if (!file.exists()) {
            save();
        }

        try {
            return FileUtils.readFileToString(file, charset);
        } catch (IOException e) {
            CrashReport.create(e, "Error during read config");
            return "";
        }
    }

    protected void saveFileContent(String content) {
        File file = getFile();

        try {
            FileUtils.writeStringToFile(file, content, charset);
        } catch (IOException e) {
            CrashReport.create(e, "Error duting save config");
        }
    }

    @Override
    public List<IConfigCategory> categories() {
        return categories;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    protected File getFile() {
        Identifier file = getId();

        String concat = file.getNamespace();
        if (file.getPath() != null && file.getPath().length() > 0) {
            concat += "/" + file.getPath();
        }

        concat += "." + ext;

        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), concat);

        config.getParentFile().mkdirs();

        return config;
    }
}
