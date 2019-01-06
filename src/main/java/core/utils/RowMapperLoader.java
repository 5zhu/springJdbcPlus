package core.utils;

import com.google.common.collect.Maps;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * @Auther:
 * @Date: 2018/12/23
 * @Description:
 */
public class RowMapperLoader<T> implements RowMapper<T> {


    private Class<?> targetClass;

    private HashMap<String, Field> fieldHashMap;

    public RowMapperLoader(Class<?> targetClass){
        this.targetClass = targetClass;
        fieldHashMap = Maps.newHashMap();
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields){
            fieldHashMap.put(field.getName(), field);
        }
    }

    @Override
    public T mapRow(ResultSet rs, int arg1) {
        T obj = null;

        try {
            obj = (T) targetClass.newInstance();

            final ResultSetMetaData metaData = rs.getMetaData();
            int columnLength = metaData.getColumnCount();
            String columnName;

            for (int i = 1; i <= columnLength; i++) {
                columnName = metaData.getColumnName(i);
                Class fieldClazz = fieldHashMap.get(NameHandler.toLowerUnderscore(columnName)).getType();
                Field field = fieldHashMap.get(NameHandler.toLowerUnderscore(columnName));
                field.setAccessible(true);

                if (fieldClazz == int.class || fieldClazz == Integer.class) {
                    field.set(obj, rs.getInt(columnName));
                } else if (fieldClazz == boolean.class || fieldClazz == Boolean.class) {
                    field.set(obj, rs.getBoolean(columnName));
                } else if (fieldClazz == String.class) {
                    field.set(obj, rs.getString(columnName));
                } else if (fieldClazz == float.class) {
                    field.set(obj, rs.getFloat(columnName));
                } else if (fieldClazz == double.class || fieldClazz == Double.class) {
                    field.set(obj, rs.getDouble(columnName));
                } else if (fieldClazz == BigDecimal.class) {
                    field.set(obj, rs.getBigDecimal(columnName));
                } else if (fieldClazz == short.class || fieldClazz == Short.class) {
                    field.set(obj, rs.getShort(columnName));
                } else if (fieldClazz == Date.class) {
                    field.set(obj, rs.getDate(columnName));
                } else if (fieldClazz == Timestamp.class) {
                    field.set(obj, rs.getTimestamp(columnName));
                } else if (fieldClazz == Long.class || fieldClazz == long.class) {
                    field.set(obj, rs.getLong(columnName));
                } else if (!StringUtils.isEmpty(rs.getString(columnName)) && fieldClazz.isEnum() ){
                    Method method = obj.getClass().getMethod(NameHandler.getSetMethodName(field.getName()), fieldClazz);
                    method.invoke(obj, Enum.valueOf(fieldClazz, rs.getString(columnName)));
                }

                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
