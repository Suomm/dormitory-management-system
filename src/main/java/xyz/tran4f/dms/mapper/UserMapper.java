package xyz.tran4f.dms.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import xyz.tran4f.dms.domain.User;

import java.util.List;

/**
 * @author 王帅
 * @since 1.0
 */
@Repository
public interface UserMapper {

    @Select("select * from user;")
    List<User> findAll();

    @Select("select * from user where id = #{id};")
    User findById(Integer id);

    @Select("select * from user where username = #{username};")
    User findByName(String username);

    @Update("update user set username = #{username}, password = #{password}, money = #{money} where id = #{id};")
    void updateUser(User user);

}
