package xyz.tran4f.dms.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.tran4f.dms.domain.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 王帅
 * @since 1.0
 */
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void findAll() {
        userMapper.findAll().forEach(System.out::println);
    }

    @Test
    public void findById() {
        System.out.println(userMapper.findById(3));
    }

    @Test
    public void findByName() {
        System.out.println(userMapper.findByName("张三"));
    }

    @Test
    public void updateUser() {
        User u = new User(3, "赵四", "abide", 20);
        userMapper.updateUser(u);
    }
}