package xyz.tran4f.dms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tran4f.dms.domain.User;
import xyz.tran4f.dms.mapper.UserMapper;
import xyz.tran4f.dms.service.UserService;

/**
 * @author 王帅
 * @since 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void transfer(Integer s, Integer t, Integer money) {
        User source = userMapper.findById(s);
        User target = userMapper.findById(t);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        userMapper.updateUser(source);
        userMapper.updateUser(target);
    }

    @Override
    public User login(String username, String password) {
        // 校验
        return userMapper.findByName(username);
    }
}
