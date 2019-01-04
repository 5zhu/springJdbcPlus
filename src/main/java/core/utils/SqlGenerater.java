package core.utils;

import core.entity.SuperEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Sql 生成工具类
 */
public class SqlGenerater<T extends SuperEntity> {

    private SqlGenerater(){}

    private static class Holder{
        private final static SqlGenerater instance = new SqlGenerater();
    }

    public static SqlGenerater get(){
        return Holder.instance;
    }

    public String generateInsertSql(T t) throws Exception {
        StringBuffer insert_sql = new StringBuffer(" insert into ");
        StringBuffer insert_value_sql = new StringBuffer(" values (");
        insert_sql.append(t.getTableName()).append("(");
        List<String> fieldList = t.getAttributeNames();
        String split = "";
        for (String fieldName : fieldList) {
            if (Objects.nonNull(t.getAttributeValue(fieldName))) {
                insert_sql.append(split).append(t.getDBAttributeName(fieldName));
                insert_value_sql.append(split).append(":").append(fieldName);
                split = ",";
            }
        }
        insert_sql.append(")");
        insert_value_sql.append(")");
        return insert_sql.append(insert_value_sql).toString();
    }

    public <T extends SuperEntity> String generateDeleteSql(T t){
        StringBuffer delete_sql = new StringBuffer(" delete from ");
        delete_sql.append(t.getTableName()).append(" where ")
                .append(t.getDBAttributeName(t.getPkFieldName()))
                .append("=:id");
        return delete_sql.toString();
    }

    public <T extends SuperEntity> String generateSelectSql(T t) {
        StringBuffer sql = new StringBuffer(" SELECT ");
        String fieldInDB = t.getAttributeNames().stream()
                .map(s -> t.getDBAttributeName(s))
                .collect(Collectors.joining(","));
        sql.append(fieldInDB).append(" FROM ").append(t.getTableName()).append(" ");
        return sql.toString();
    }

    public <T extends SuperEntity> String generateWhereSql(T t, List<Object> params) throws Exception {
        StringBuffer whereSql = new StringBuffer();
        if(Objects.nonNull(t)){
            List<String> fields = t.getAttributeNames();
            for (String field : fields){
                Object fieldValue = t.getAttributeValue(field);
                field = t.getDBAttributeName(field);
                if(Objects.nonNull(fieldValue)){
                    if(whereSql.length() > 0) {
                        whereSql.append(" and ");
                    }
                    if(fieldValue instanceof String){
                        whereSql.append(field).append(" like ? ");
                        params.add("%"+fieldValue+"%");
                    } else {
                        whereSql.append(field).append("= ? ");
                        params.add(fieldValue);
                    }
                }
            }
        }
        return whereSql.toString();
    }

}
