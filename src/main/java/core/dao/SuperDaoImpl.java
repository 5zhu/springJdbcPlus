package core.dao;

import core.entity.PageView;
import core.entity.SuperEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("superDao")
public class SuperDaoImpl<T extends SuperEntity> extends JdbcTemplateDaoSupport implements SuperDao<T> {

    public int insertDO(T t) throws Exception {
        return super.insert(t);
    }

    @Override
    public T getById(Class<T> clazz, int id) {
        return (T) super.get(clazz, id);
    }

    @Override
    public int updateDO(T oldDO, T newDO) throws Exception {
        return super.update(oldDO, newDO);
    }

    @Override
    public void deleteDO(Class<T> clazz, int id) throws Exception {
        super.delete(clazz, id);
    }

    @Override
    public List<T> queryForList(T t) throws Exception {
        return super.findList(t);
    }

    @Override
    public PageView queryForPage(T t) throws Exception {
        return this.queryForPage(t, null);
    }

    @Override
    public PageView queryForPage(T t, String order) throws Exception {
        return super.queryForPageView(t, order);
    }
}
