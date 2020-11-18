package dash.dashmode.config;

import dash.dashmode.config.engine.IoEngine;
import dash.dashmode.event.LangChangeEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
     * Current
     */
    private final IoEngine ioEngine;
    private final List<ValueInfo> fields;

    /**
     * Linked file
     */
    private final File configFile;

    /**
     * Singletone config object
     */
    private T instance;

    /**
     * @param clazz    - class for config object. Should have parameterless ctor.
     * @param modId    - name of file inside configs folder.
     * @param ioEngine
     */
    public Config(Class<T> clazz, String modId, IoEngine ioEngine) {
        this.clazz = clazz;
        this.modId = modId;
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), modId + "." + ioEngine.getExt());
        this.ioEngine = ioEngine;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            CrashReport.create(e, "Config loading error");
        }

        fields = ValueInfo.load(instance);
        LangChangeEvent.LangChanges.register(lang -> writeToFile());
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
                String content = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                ioEngine.read(content, instance, fields);
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

        String content = ioEngine.toWrite(instance, fields);

        try {
            FileUtils.writeStringToFile(configFile, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.create(e, "Config write to file error");
        }
    }

}
