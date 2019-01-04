package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import core.utils.NameHandler;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SuperEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Class<? extends Annotation> USERDEFINED = UserDefined.class;

    /** 排除的属性 */
    private final String SERIAL_VERSION_UID = "serialVersionUID";

    public String getTableName(){
        if(this.getClass().isAnnotationPresent(TableName.class)){
            TableName tableName = this.getClass().getAnnotation(TableName.class);
            return tableName.value();
        }
        return "";
    }

    public String getPkFieldName(){
        if(this.getClass().isAnnotationPresent(TableName.class)){
            TableName tableName = this.getClass().getAnnotation(TableName.class);
            return tableName.pk();
        }
        return "";
    }

    @JsonIgnore
    public List<String> getAttributeNames(){
        if (null == FiledCache.getField(this.getClass().getName())) {
            Field[] fieldArray = this.getClass().getDeclaredFields();
            List<String> fields = Arrays.stream(fieldArray)
                    .filter(f -> !isIgnore_USERDEFINED_Field(f))
                    .map(Field::getName).collect(Collectors.toList());
            if (!this.getClass().getSuperclass().equals(SuperEntity.class)) {
                try {
                    SuperEntity superVo = (SuperEntity) this.getClass().getSuperclass()
                            .newInstance();
                    fields.addAll(superVo.getAttributeNames());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            FiledCache.setField(this.getClass().getName(), fields);
        }
        return FiledCache.getField(this.getClass().getName());
    }

    private boolean isIgnore_USERDEFINED_Field(Field f) {
        boolean isIgnore = false;

        if (SERIAL_VERSION_UID.equalsIgnoreCase(f.getName())) {
            isIgnore = true;
        }else if (f.isAnnotationPresent(USERDEFINED)) {
            UserDefined ud = (UserDefined) f.getAnnotation(USERDEFINED);
        }
        return isIgnore;
    }

    static class FiledCache {
        // 属性（不带注解）缓存 （与数据库有对应列的属性）
        static Cache<String, List<String>> fieldMap = CacheBuilder.newBuilder().build();
        // 属性（带转换注解）缓存
        static Cache<String, Map<String, String>> fieldTransitionMap = CacheBuilder.newBuilder().build();
        // 属性（字段混淆，代码属性名-数据库字段值）缓存
        static Cache<String, Map<String, String>> fieldDBMap = CacheBuilder.newBuilder().build();

        public static Map<String, String> getFieldDbMap(String className) {
            return fieldDBMap.getIfPresent(className);
        }

        public static void setFieldDbMap(String className, Map<String, String> fieldMap) {
            fieldDBMap.put(className, fieldMap);
        }

        static List<String> getField(String className) {
            return fieldMap.getIfPresent(className);
        }

        static void setField(String className, List<String> field_list) {
            fieldMap.put(className, field_list);
        }

        static Map<String, String> getTransitionField(String className) {
            return fieldTransitionMap.getIfPresent(className);
        }

    }

    public Object getAttributeValue(String name) throws Exception {
        Field field = this.getClass().getDeclaredField(name);
        //设置对象的访问权限，保证对private的属性的访问
        field.setAccessible(true);
        return field.get(this);
    }

    public String getDBAttributeName(String fieldName) {
        Map<String, String> map = this.getAttributeDBnames();
        return map.get(fieldName) == null ? NameHandler.toLowerCamel(fieldName) : map.get(fieldName);
    }

    private Map<String,String> getAttributeDBnames() {
        if (null == FiledCache.getTransitionField(this.getClass().getName())) {
            Field[] f = this.getClass().getDeclaredFields();
            Map<String, String> field_map = new HashMap<String, String>();
            for (int i = 0; i < f.length; i++) {
                if (f[i].isAnnotationPresent(USERDEFINED)) {
                    UserDefined ud = (UserDefined) f[i]
                            .getAnnotation(USERDEFINED);
                    String db_fieldName = ud.transitionFunction();

                    if (!"".equals(db_fieldName)) {//与数据库对应配置
                        field_map.put(f[i].getName(),db_fieldName);
                    }
                }
            }
            if (!this.getClass().getSuperclass().equals(SuperEntity.class)) {
                try {
                    SuperEntity superEntity = (SuperEntity) this.getClass().getSuperclass()
                            .newInstance();
                    field_map.putAll(superEntity.getAttributeDBnames());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            FiledCache.setFieldDbMap(this.getClass().getName(), field_map);
        }
        return FiledCache.getFieldDbMap(this.getClass().getName());
    }
}
