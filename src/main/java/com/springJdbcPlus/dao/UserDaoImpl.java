package com.springJdbcPlus.dao;

import com.springJdbcPlus.entity.User;
import core.dao.SuperDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl extends SuperDaoImpl<User> implements UserDao  {

   /* @Autowired
    private JdbcTemplate jdbcTemplate;*/

   /* @Override
    public int insertUser(User user) throws Exception {
        String sql = "insert into t_user (username,password,account) values (?,?,?)";
        this.jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getAccount());
        System.out.println("Created Record: " + user);
        return 0;
    }*/

}
