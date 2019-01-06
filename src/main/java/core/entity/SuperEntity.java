package core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import core.utils.NameHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SuperEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static Class<? extends Annotation> USERDEFINED = UserDefined.class;

    private final static String SERIAL_VERSION_UID = "serialVersionUID";

    @Override
    public SuperEntity clone() {
        try {
            SuperEntity newEntity = this.getClass().newInstance();
            BeanUtils.copyProperties(this, newEntity);
            return newEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public String getTableName(){
        if(this.getClass().isAnnotationPresent(TableName.class)){
            TableName tableName = this.getClass().getAnnotation(TableName.class);
            return tableName.value();
        }
        throw new RuntimeException("请给"+this.getClass().getName()+"添加@TableName注解");
    }

    @JsonIgnore
    public String getPkFieldName(){
        if(this.getClass().isAnnotationPresent(TableName.class)){
            TableName tableName = this.getClass().getAnnotation(TableName.class);
            return tableName.pk();
        }
        return "";
    }

    private static boolean isIgnoreField(Field f) {
        return SERIAL_VERSION_UID.equalsIgnoreCase(f.getName());
    }

    static class CacheModel {
        // entity class属性（不带UserDefined注解）缓存 （与数据库有对应列的属性）
        static Cache<String, List<String>> fieldMap = CacheBuilder.newBuilder().build();
        // 属性（带转换注解）缓存 提供给查询方法使用
        static Cache<String, Map<String, String>> fieldTransitionSelectMap = CacheBuilder.newBuilder().build();

        static Cache<String, Map<String, String>> fieldTransitionUpdateMap = CacheBuilder.newBuilder().build();

        static List<String> getField(String className) {
            return fieldMap.getIfPresent(className);
        }

        static void setField(String className, List<String> field_list) {
            fieldMap.put(className, field_list);
        }

        static Map<String, String> getTransitionField(String className) {
            return fieldTransitionSelectMap.getIfPresent(className);
        }

        static void setTransitionMap(String className, Map<String, String> fieldMap){
            fieldTransitionSelectMap.put(className, fieldMap);
        }

        static Map<String, String> getTransitionUpdateField(String className) {
            return fieldTransitionUpdateMap.getIfPresent(className);
        }

        static void setTransitionUpdateMap(String className, Map<String, String> fieldMap){
            fieldTransitionUpdateMap.put(className, fieldMap);
        }
    }

    /**
     * 获取class中与DB中对于的字段，
     * @return
     */
    @JsonIgnore
    public List<String> getAttributeNames(){
        if (null == CacheModel.getField(this.getClass().getName())) {
            Field[] fieldArray = this.getClass().getDeclaredFields();
            List<String> fields = Arrays.stream(fieldArray)
                    .filter(f -> !isIgnoreField(f))
                    .map(Field::getName).collect(Collectors.toList());
            if (!this.getClass().getSuperclass().equals(SuperEntity.class)) {
                try {
                    SuperEntity superEntity = (SuperEntity) this.getClass().getSuperclass().newInstance();
                    fields.addAll(superEntity.getAttributeNames());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CacheModel.setField(this.getClass().getName(), fields);
        }
        return CacheModel.getField(this.getClass().getName());
    }



    @JsonIgnore
    public Object getAttributeValue(String name) throws Exception {
        Field field = this.getClass().getDeclaredField(name);
        //设置对象的访问权限，保证对private的属性的访问
        field.setAccessible(true);
        Object value = field.get(this);
        field.setAccessible(false);
        return value;
    }

    /**
     * 通过实体类中的字段名获取table中对应的列名，UserDefined做函数转换
     * @param fieldName
     * @return
     */
    @JsonIgnore
    public String getDBColumnName(String fieldName) {
        Map<String, String> map = this.getUserDefined4Select();
        return map.get(fieldName) == null ? NameHandler.toLowerCamel(fieldName) : map.get(fieldName);
    }

    @JsonIgnore
    public String getDBColumnName4Update(String fieldName) {
        return NameHandler.toLowerCamel(fieldName);
    }


    /**
     * 获取entity中UserDefined对应Table所有字段column
     * @return Map<String,String> key:entity中的FieldName，value：table中的column
     */
    @JsonIgnore
    private Map<String,String> getUserDefined4Select() {
        if (null == CacheModel.getTransitionField(this.getClass().getName())) {
            Field[] fields = this.getClass().getDeclaredFields();
            Map<String, String> field_map = Maps.newConcurrentMap();
            for(Field field : fields){
                if (field.isAnnotationPresent(USERDEFINED)) {
                    UserDefined ud = (UserDefined) field.getAnnotation(USERDEFINED);
                    String db_fieldName = ud.transitionFunction();
                    if (!"".equals(db_fieldName)) {
                        field_map.put(field.getName(), db_fieldName.concat(" as ").concat(field.getName()));
                    }
                }
            }
            CacheModel.setTransitionMap(this.getClass().getName(), field_map);
        }
        return CacheModel.getTransitionField(this.getClass().getName());
    }
}
