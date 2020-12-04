package dash.dashmode.config.yamlImpl;

import com.fasterxml.jackson.core.type.TypeReference;

public class TypeRef<T> extends TypeReference<T> {
    public final Class clazz;

    public TypeRef(Class clazz) {
        super();
        this.clazz = clazz;
    }
}
