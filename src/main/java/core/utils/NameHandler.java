package core.utils;

import com.google.common.base.CaseFormat;

import java.util.Optional;

/**
 * @Auther:
 * @Date: 2018/12/12
 * @Description:
 */
public class NameHandler {

    /**
     * testData -> test_data
     * @param name
     * @return
     */
    public static String toLowerCamel(String name){
        return Optional.ofNullable(name)
                .map(str -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str))
                .orElse("");
    }

    /**
     * test_data -> testData
     * @param name
     * @return
     */
    //CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "TestData")
    public static String toLowerUnderscore(String name){
        return Optional.ofNullable(name)
                .map(str -> CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str))
                .orElse("");
    }

    /**
     * testData -> setTestData
     * @param fieldName
     * @return
     */
    public static String getSetMethodName(String fieldName){
        return Optional.ofNullable(fieldName).map(str -> "set".concat(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, str)))
                .orElse("");
    }
}
