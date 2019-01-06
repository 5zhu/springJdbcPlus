package core.entity;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TableName {

    public String value() default "";

    public String pk() default "id";
}
