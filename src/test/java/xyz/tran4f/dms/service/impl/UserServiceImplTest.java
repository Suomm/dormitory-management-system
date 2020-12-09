package xyz.tran4f.dms.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.tran4f.dms.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 王帅
 * @since 1.0
 */
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void transfer() {
        userService.transfer(1, 2, 100);
    }
}