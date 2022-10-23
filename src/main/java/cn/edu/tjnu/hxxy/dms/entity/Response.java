/*
 * Copyright (C) 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.edu.tjnu.hxxy.dms.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

/**
 * 响应状态与数据的封装。
 *
 * @author 王帅
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "响应信息封装")
public class Response implements Serializable {

    private static final long serialVersionUID = -419758422553049745L;

    /**
     * 响应成功状态码。
     */
    public static final int OK = 1;

    /**
     * 响应失败状态码。
     */
    public static final int ERROR = -1;

    @ApiModelProperty(value = "响应状态码", required = true)
    private int code;

    @ApiModelProperty(value = "响应信息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private Object data;

    /**
     * 根据响应信息构建一个成功的响应。
     *
     * @param msg 响应信息
     * @return 响应实体
     */
    @Contract(pure = true)
    public static Response ok(String msg) {
        return new Response(OK, msg, null);
    }

    /**
     * 根据响应数据构建一个成功的响应。
     *
     * @param data 响应数据
     * @return 响应实体
     */
    @Contract(pure = true)
    public static Response ok(Object data) {
        return new Response(OK, null, data);
    }

    /**
     * 根据响应信息构建一个失败的响应。
     *
     * @param msg 响应信息
     * @return 响应实体
     */
    @Contract(pure = true)
    public static Response error(String msg) {
        return new Response(ERROR, msg, null);
    }

}
