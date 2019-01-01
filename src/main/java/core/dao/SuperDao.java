package core.dao;

import core.entity.SuperEntity;

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
    public T getDo(Class<T> tClass, int id);

    /**
     * 更新
     * @param oldDo
     * @param newDO
     * @return
     * @throws Exception
     */
    public int update(T oldDo, T newDO) throws Exception;

    /**
     * 删除
     *
     * @param id
     */
    public void deleteDO(Class<T> clazz, int id) throws Exception;
}
