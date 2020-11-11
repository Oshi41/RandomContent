package dash.dashmode.config;

import java.util.List;

public interface IoEngine {
    /**
     * Reads from file contens with supplied fields
     *
     * @param fileContent - file content
     * @param instance    - config instance
     * @param fields      - list of using fields
     */
    void read(String fileContent, Object instance, List<ValueInfo> fields);

    /**
     * Writes current config to file
     *
     * @param instance - config instance
     * @param fields   - list of fields
     */
    String toWrite(Object instance, List<ValueInfo> fields);

    /**
     * Extension for file
     *
     * @return
     */
    String getExt();
}
