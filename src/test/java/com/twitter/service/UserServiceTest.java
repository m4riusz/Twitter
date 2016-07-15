package com.twitter.service;

import com.twitter.dao.UserDao;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mariusz on 14.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
public class UserServiceTest {

    private UserDao userDao;

    private UserService userService;

    @BeforeClass
    private void setUserDao() {
        userDao = Mockito.mock(UserDao.class);
    }

    @Test
    private void getAllUsersTest(){
        userService.getAllUsers();
    }

}
