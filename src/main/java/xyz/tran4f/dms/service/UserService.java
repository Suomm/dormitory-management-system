package xyz.tran4f.dms.service;

import xyz.tran4f.dms.domain.User;

/**
 * @author 王帅
 * @since 1.0
 */
public interface UserService {

    void transfer(Integer s, Integer t, Integer money);

    User login(String username, String password);

}
