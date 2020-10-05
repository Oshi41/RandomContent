package dash.dashmode.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {
    /**
     * This the category of the property
     *
     * @return
     */
    String category() default "main";

    /**
     * This is the key for the property, the default is the field name.
     *
     * @return
     */
    String key() default "";

    /**
     * This is a comment that will be supplied along with the config, use this to explain what the property for
     *
     * @return
     */
    String comment() default "";

    /**
     * This is a comment for property using tranlation
     *
     * @return
     */
    String commentLangKey() default "";
}
