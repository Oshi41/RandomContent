package dash.dashmode.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FloatValidate {
    /**
     * Minimum value inclusive for validation
     *
     * @return
     */
    float minValue() default Float.MIN_VALUE;

    /**
     * Maximum value inclusive for validation
     *
     * @return
     */
    float maxValue() default Float.MAX_VALUE;
}
