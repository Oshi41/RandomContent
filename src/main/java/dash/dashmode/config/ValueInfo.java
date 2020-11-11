package dash.dashmode.config;

import dash.dashmode.DashMod;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValueInfo {
    public final Field propertyField;
    public final Property propertyInfo;
    @Nullable
    public final FloatValidate propertyValidation;

    public ValueInfo(Field propertyField, Property propertyInfo, @Nullable FloatValidate propertyValidation) {
        this.propertyField = propertyField;
        this.propertyInfo = propertyInfo;
        this.propertyValidation = propertyValidation;
    }

    public static List<ValueInfo> load(Object instance) {
        List<ValueInfo> result = new ArrayList<>();

        for (Field field : instance.getClass().getFields()) {
            if (!field.isAnnotationPresent(Property.class))
                continue;

            Property annotation = field.getAnnotation(Property.class);
            FloatValidate validate = null;
            if (field.isAnnotationPresent(FloatValidate.class)) {
                validate = field.getAnnotation(FloatValidate.class);
            }

            result.add(new ValueInfo(field, annotation, validate));
        }

        return result;
    }

    /**
     * Gets corrent value for field
     *
     * @param instance - config instance
     * @param <T>      - type of field
     * @return
     */
    public <T> T getValue(Object instance) {
        try {
            Object value = propertyField.get(instance);

            if (propertyValidation != null && value instanceof Float) {
                value = MathHelper.clamp(((Float) value), propertyValidation.minValue(), propertyValidation.maxValue());
            }

            return ((T) value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set value to field
     *
     * @param instance   - config instance
     * @param fieldValue - field value
     * @return
     */
    public boolean setValue(Object instance, Object fieldValue) {
        try {
            if (propertyValidation != null && fieldValue instanceof Float) {
                fieldValue = MathHelper.clamp(((Float) fieldValue), propertyValidation.minValue(), propertyValidation.maxValue());
            }

            propertyField.set(instance, fieldValue);
            return true;
        } catch (Exception e) {
            DashMod.MainLogger.warn(e);
            return false;
        }
    }

    /**
     * Returbs comment for property
     *
     * @return
     */
    public List<String> getComment() {
        ArrayList<String> arrayList = new ArrayList<>();

        if (propertyInfo.commentLangKey().isEmpty()) {
            arrayList.add(propertyInfo.comment());
        } else {
            arrayList.add(Language.getInstance().get(propertyInfo.commentLangKey()));
        }

        if (propertyValidation != null) {
            arrayList.add(String.format(Language.getInstance().get("random_content.range_restrict.comment"), propertyValidation.minValue(), propertyValidation.maxValue()));
        }

        return arrayList;
    }

    /**
     * Returns key for field
     *
     * @return
     */
    public String getKey() {
        return propertyInfo.key().isEmpty()
                ? propertyField.getName()
                : propertyInfo.key();
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyField);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueInfo)) return false;
        ValueInfo valueInfo = (ValueInfo) o;
        return Objects.equals(propertyField, valueInfo.propertyField);
    }
}
