package clightning.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RpcMethod {
    String name() default "";

    String description() default "";

    String longDescription() default "";
}
