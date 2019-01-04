package core.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.entity.SuperEntity;
import core.utils.RowMapperLoader;
import core.utils.SqlGenerater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import java.util.*;

public abstract class JdbcTemplateDaoSupport<T extends SuperEntity> {

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected int insert(T t) throws Exception {
        String sql = SqlGenerater.get().generateInsertSql(t);
        return this.executeInsert(sql, t);
    }

    private int executeInsert(String sql, T t) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource source = new DefinedBeanPropertySqlParameterSource(t);
        this.namedParameterJdbcTemplate.update(sql, source, holder,
                new String[] { t.getDBAttributeName(t.getPkFieldName()) });
        Number number = holder.getKey();
        return Optional.ofNullable(number).map(Number::intValue).orElse(0);
    }

    protected  <T extends SuperEntity> void delete(Class<T> clazz, int id) throws IllegalAccessException, InstantiationException {
        SuperEntity superEntity = clazz.newInstance();
        String deleteSql = SqlGenerater.get().generateDeleteSql(superEntity);
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        this.namedParameterJdbcTemplate.update(deleteSql, map);
    }

    protected <T extends SuperEntity> int update(T oldDO, T newDO, boolean isNullUpdate) throws Exception{

        StringBuffer updateSql = new StringBuffer(" UPDATE ");
        StringBuffer updateWhereSql = new StringBuffer(" WHERE ");

        updateSql.append(oldDO.getTableName()).append(" SET ");

        List<String> fields = oldDO.getAttributeNames();
        List<Object> values = Lists.newArrayList();
        boolean isUpdate = false;

        for (String field : fields){
            Object newValObj = newDO.getAttributeValue(field);
            if(!isNullUpdate && Objects.nonNull(newValObj)){
                continue;
            }
            if(oldDO.getPkFieldName().equalsIgnoreCase(field)){
                updateWhereSql.append(oldDO.getDBAttributeName(field)).append("=?");
            } else {
                String oldVal = Optional.ofNullable(oldDO.getAttributeValue(field))
                        .map(Object::toString)
                        .orElse("");
                String newVal = Optional.ofNullable(newValObj)
                        .map(Object::toString)
                        .orElse("");
                if (!Objects.equals(oldVal, newVal)){
                    updateSql.append(oldDO.getDBAttributeName(field)).append("=?");
                    values.add(newDO.getAttributeValue(field));
                    updateSql.append(",");
                    isUpdate = true;
                }
            }
        }

        if (!isUpdate){
            return 0;
        }

        if(updateSql.lastIndexOf(",") == updateSql.length() - 1) {
            updateSql.deleteCharAt(updateSql.length() - 1);
        }

        values.add(oldDO.getAttributeValue(oldDO.getPkFieldName()));
        return executeUpdate(updateSql.append(updateWhereSql).toString(), values);
    }

    protected int executeUpdate(String sql, List<Object> values){
        return jdbcTemplate.update(sql, values.toArray());
    }


    protected <T extends SuperEntity> SuperEntity get(Class<T> clazz, Object id){
        SuperEntity superEntity;
        try {
            superEntity = clazz.newInstance();
            StringBuffer getSql = new StringBuffer(SqlGenerater.get().generateSelectSql(superEntity))
                    .append(" where ").append(superEntity.getDBAttributeName(superEntity.getPkFieldName()))
                    .append("=?");
            return this.jdbcTemplate.queryForObject(getSql.toString(), new RowMapperLoader<T>(clazz), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T extends SuperEntity> List<T> findList(T t) throws Exception {
        return query(t.getClass(), t);
    }

    private <T extends SuperEntity> List<T> query(Class<? extends SuperEntity> clazz, T t) throws Exception {
        SqlGenerater sqlGenerater = SqlGenerater.get();
        List<Object> params = Lists.newArrayList();
        String selectSql = sqlGenerater.generateSelectSql(t);
        String whereSql = sqlGenerater.generateWhereSql(t, params);
        if(!StringUtils.isEmpty(whereSql)){
            selectSql = new StringBuffer(selectSql).append(" where ").append(whereSql).toString();
        }
        return this.jdbcTemplate.query(selectSql, params.toArray(), new RowMapperLoader<T>(clazz));
    }
}
