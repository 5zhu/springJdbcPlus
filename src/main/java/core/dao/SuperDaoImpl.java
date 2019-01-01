package core.dao;

import core.entity.SuperEntity;
import org.springframework.stereotype.Repository;

@Repository("superDao")
public class SuperDaoImpl<T extends SuperEntity> extends JdbcTemplateDaoSupport implements SuperDao<T> {

    public int insertDO(T t) throws Exception {
        return super.insert(t);
    }

    @Override
    public T getDo(Class<T> clazz, int id) {
        return (T) super.get(clazz, id);
    }

    @Override
    public int update(T oldDO, T newDO) throws Exception {
        return super.update(oldDO, newDO, true);
    }

    @Override
    public void deleteDO(Class<T> clazz, int id) throws Exception {
        super.delete(clazz, id);
    }
}
