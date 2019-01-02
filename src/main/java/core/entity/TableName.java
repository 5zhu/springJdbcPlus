package core.entity;

/**
 * @Auther: geguofeng
 * @Date: 2019/1/2
 * @Description:
 */
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TableName {

    public String value() default "";

    public String pk() default "id";
}
