package core.dao;

import core.entity.SuperEntity;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.sql.Types;

/**
 *
 * @Auther:
 * @Date: 2019/1/4
 * @Description: 实现自定义的BeanPropertySqlParameterSource，支持entity对象中的枚举类型字段
 */
public class DefinedBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource {

    public <T extends SuperEntity> DefinedBeanPropertySqlParameterSource(T t) {
        super(t);
    }

    @Override
    public int getSqlType(String var){
       int type = super.getSqlType(var);
       if(type == TYPE_UNKNOWN && hasValue(var)) {
           if (getValue(var).getClass().isEnum()){
               type = Types.VARCHAR;
           }
       }
       return type;
    }

}
