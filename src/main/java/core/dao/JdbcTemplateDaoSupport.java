package core.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.entity.PageView;
import core.entity.SuperEntity;
import core.utils.RowMapperLoader;
import core.utils.SqlGenerater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import java.util.*;

public abstract class JdbcTemplateDaoSupport<T extends SuperEntity> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateDaoSupport.class);

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected <T extends SuperEntity> int insert(T t) throws Exception {
        String sql = SqlGenerater.get().generateInsertSql(t);
        logger.debug("execute insert : {}", sql);
        return this.executeInsert(sql, t);
    }

    private <T extends SuperEntity> int executeInsert(String sql, T t) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource source = new DefinedBeanPropertySqlParameterSource(t);
        this.namedParameterJdbcTemplate.update(sql, source, holder,
                new String[] { t.getDBColumnName(t.getPkFieldName()) });
        Number number = holder.getKey();
        return Optional.ofNullable(number).map(Number::intValue)
                .orElseThrow(() -> new RuntimeException());
    }

    protected  <T extends SuperEntity> void delete(Class<T> clazz, int id) throws IllegalAccessException, InstantiationException {
        SuperEntity superEntity = clazz.newInstance();
        String deleteSql = SqlGenerater.get().generateDeleteSql(superEntity);
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        this.namedParameterJdbcTemplate.update(deleteSql, map);
    }

    protected <T extends SuperEntity> int update(T oldDO, T newDO) throws Exception{

        StringBuffer updateSql = new StringBuffer(" UPDATE ");
        StringBuffer updateWhereSql = new StringBuffer(" WHERE ");

        updateSql.append(oldDO.getTableName()).append(" SET ");

        List<String> fields = oldDO.getAttributeNames();
        List<Object> values = Lists.newArrayList();
        boolean isUpdate = false; // 是否需要更新的标示

        for (String field : fields){
            //通过DO的Field 获取该DO Field的值
            Object newValObj = newDO.getAttributeValue(field);
            //拼装where条件
            if(oldDO.getPkFieldName().equalsIgnoreCase(field)){
                updateWhereSql.append(oldDO.getDBColumnName4Update(field)).append("=?");
            } else {
                //值比较，不同才更新
                String oldVal = Optional.ofNullable(oldDO.getAttributeValue(field))
                        .map(Object::toString).orElse("");
                String newVal = Optional.ofNullable(newValObj).map(Object::toString).orElse("");
                if (!Objects.equals(oldVal, newVal)){
                    updateSql.append(oldDO.getDBColumnName4Update(field)).append("=?");
                    values.add(newDO.getAttributeValue(field));
                    updateSql.append(",");
                    isUpdate = true;
                }
            }
        }

        //所有字段都无更新，直接返回，不执行update
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
        logger.debug("execute update sql : {}", sql);
        return jdbcTemplate.update(sql, values.toArray());
    }


    protected <T extends SuperEntity> SuperEntity get(Class<T> clazz, Object id){
        SuperEntity superEntity;
        try {
            superEntity = clazz.newInstance();
            StringBuffer getSql = new StringBuffer(SqlGenerater.get().generateSelectSql(superEntity))
                    .append(" where ").append(superEntity.getDBColumnName(superEntity.getPkFieldName()))
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

    protected <T extends SuperEntity> PageView queryForPageView(T t, String order) throws Exception {
        return this.queryForPageView(t.getClass(), t, order, 1, 2);
    }

    private <T extends SuperEntity> PageView queryForPageView(Class<? extends SuperEntity> clazz,
                                                          T t, String order, int offset, int pageSize) throws Exception {
        PageView pageView = new PageView();
        SqlGenerater sqlGenerater = SqlGenerater.get();
        String querySql = sqlGenerater.generateSelectSql(t);
        List<Object> params = Lists.newArrayList();
        String whereSql = sqlGenerater.generateWhereSql(t, params);
        if(!StringUtils.isEmpty(whereSql)){
            querySql = querySql.concat(whereSql);
        }

        if (!StringUtils.isEmpty(order)){
            querySql = querySql.concat(" order by ").concat(order);
        } else if(StringUtils.isEmpty(order) && !StringUtils.isEmpty(t.getPkFieldName())){
            querySql = querySql.concat(" order by ").concat(t.getPkFieldName()).concat(" desc ");
        }
        querySql = querySql.concat(" limit ").concat(String.valueOf(pageSize)).concat(" offset ").concat(String.valueOf(offset));
        pageView.setDatas(this.jdbcTemplate.query(querySql, params.toArray(), new RowMapperLoader<T>(clazz)));
        return pageView;
    }
}
