package core.utils;

import com.google.common.base.CaseFormat;

import java.util.Optional;

/**
 * @Auther:
 * @Date: 2018/12/12
 * @Description:
 */
public class NameHandler {

    public static String toLowerCamel(String name){
        return Optional.ofNullable(name)
                .map(str -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str))
                .orElse("");
    }
    //CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "TestData")
    public static String toLowerUnderscore(String name){
        return Optional.ofNullable(name)
                .map(str -> CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str))
                .orElse("");
    }
}
