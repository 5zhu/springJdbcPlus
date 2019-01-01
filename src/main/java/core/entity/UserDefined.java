package core.entity;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UserDefined {

    public String transitionSource() default "";

    public String transitionFunction() default "";

}
