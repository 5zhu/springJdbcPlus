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

    public String generateDeleteSql(T t){
        StringBuffer delete_sql = new StringBuffer(" delete from ");
        delete_sql.append(t.getTableName()).append(" where ")
                .append(t.getDBAttributeName(t.getPkFieldName()))
                .append("=:id");
        return delete_sql.toString();
    }

    public String generateSelectSql(T t) {
        StringBuffer sql = new StringBuffer(" SELECT ");
        String fieldInDB = t.getAttributeNames().stream()
                .map(s -> t.getDBAttributeName(s))
                .collect(Collectors.joining(","));
        sql.append(fieldInDB).append(" FROM ").append(t.getTableName()).append(" ");
        return sql.toString();
    }

}
