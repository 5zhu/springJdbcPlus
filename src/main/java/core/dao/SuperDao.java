package core.dao;

import core.entity.PageView;
import core.entity.SuperEntity;

import java.util.List;

public interface SuperDao<T extends SuperEntity> {

    /**
     * 新增
     *
     * @param t
     * @return
     */
    public int insertDO(T t) throws Exception;

    /**
     * 通过主键获取DO
     * @param tClass
     * @param id
     * @return
     */
    public T getById(Class<T> tClass, int id);

    /**
     * 更新
     * @param oldDo
     * @param newDO
     * @return
     * @throws Exception
     */
    public int updateDO(T oldDo, T newDO) throws Exception;

    /**
     * 删除
     *
     * @param id
     */
    public void deleteDO(Class<T> clazz, int id) throws Exception;

    /**
     * 查询List
     * @param t
     * @return
     * @throws Exception
     */
    public List<T> queryForList(T t) throws Exception;

    /**
     * 分页查询
     * @param t
     * @return
     * @throws Exception
     */
    public PageView queryForPage(T t) throws Exception;

    public PageView queryForPage(T t, String order) throws Exception;
}
