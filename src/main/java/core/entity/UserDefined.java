package core.entity;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UserDefined {

    /**
     * 转化函数
     * @return
     */
     String transitionFunction() default "";

     //TODO 这种做法查询没有问题， 修改待讨论

}
