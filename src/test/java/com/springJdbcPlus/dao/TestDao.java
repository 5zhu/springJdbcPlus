package com.springJdbcPlus.dao;

import com.springJdbcPlus.entity.GenderEnum;
import com.springJdbcPlus.entity.User;
import core.entity.PageView;
import core.utils.RowMapperLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-beans.xml")
public class TestDao {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TestDao.class);

    @Test
    public void insert() throws Exception {
        /*User user = new User(20,"geguofeng","1223dfcfdv12",181.5);
        user.setGender(1);
        //user.setIdCard("410527");
        int i = userDao.insertDO(user);
        User user1 = new User(21,"geguofeng","1223dfcfdv12",181.5);
        user1.setGender(1);
        //user1.setIdCard("410527");
        int i1 = userDao.insertDO(user1);
        User user2 = new User(22,"geguofeng","1223dfcfdv12",181.5);
        user2.setGender(1);
        int i2 = userDao.insertDO(user2);
        User user3 = new User(23,"geguofeng","1223dfcfdv12",181.5);
        user3.setGender(1);
        int i3 = userDao.insertDO(user3);
        User user4 = new User(24,"geguofeng","1223dfcfdv12",181.5);
        user4.setGender(1);
        int i4 = userDao.insertDO(user4);

        User user5 = new User(25,"geguofeng","1223dfcfdv12",181.5);
        user5.setGender(1);
        int i5 = userDao.insertDO(user5);
        User user6 = new User(26,"geguofeng","1223dfcfdv12",181.5);
        user6.setGender(1);
        int i6 = userDao.insertDO(user6);
        User user7 = new User(27,"geguofeng","1223dfcfdv12",181.5);
        user7.setGender(1);
        int i7 = userDao.insertDO(user7);*/
        User user8 = new User(32, "tom", "1234312", 100.5);
        user8.setGenderEnum(GenderEnum.MALE);
        user8.setGender(1);
        userDao.insertDO(user8);



    }

    @Test
    public void del() throws Exception {
        userDao.deleteDO(User.class, 21);
        userDao.deleteDO(User.class, 22);
        userDao.deleteDO(User.class, 23);
        userDao.deleteDO(User.class, 24);
    }

    @Test
    public void update() throws Exception {
        User user = userDao.getById(User.class, 32);
        //user.setIdCard("410527");
        User user2 = (User)user.clone();
        user2.setGender(3);

        userDao.updateDO(user, user2);
    }

    @Test
    public void get() {
        User user = userDao.getById(User.class, 32);
        System.out.println(user);
    }

    @Test
    public void testQueryPage() throws Exception {
        User u = new User();
        PageView page = userDao.queryForPage(u);
    }

    @Test
    public void getById(){
        String sql = "SELECT id,username,password,account,gender FROM t_user t where t.id=?";
        User user = jdbcTemplate.queryForObject(sql, new RowMapperLoader<User>(User.class), 3);
        System.out.println(user.getUsername());

    }

    @Test
    public void getById2(){
        String sql = "SELECT id,username,password,account,gender FROM t_user t where t.id=?";
        RowMapper mapper = BeanPropertyRowMapper.newInstance(User.class);
        User user = (User) jdbcTemplate.queryForObject(sql, mapper, 3);
        System.out.println(user.getUsername());

    }

    @Test
    public void queryForList() throws Exception {
        User user = new User();
        user.setUsername("geguofeng");
        List<User> users = this.userDao.queryForList(user);
        logger.info("query user count {}", users.size());
    }

}
