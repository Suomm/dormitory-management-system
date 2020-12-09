package xyz.tran4f.dms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 王帅
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -5246550721989479832L;

    private Integer id;
    private String username;
    private String password;
    private Integer money;

}
