package com.springJdbcPlus;

import com.springJdbcPlus.dao.UserDaoImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JDBCTest {

    public static void main(String[] args) throws Exception {

        // TODO Auto-generated method stub
        ApplicationContext context =
                new ClassPathXmlApplicationContext("application-beans.xml");
        UserDaoImpl userJDBCTemplate =
                (UserDaoImpl)context.getBean("superDao");

        // 1.创建一条记录
        System.out.println("------Records Creation--------" );
        //userJDBCTemplate.insertVO(new User("geguofeng","1223dfcfdv12",181.5));

    }
}
