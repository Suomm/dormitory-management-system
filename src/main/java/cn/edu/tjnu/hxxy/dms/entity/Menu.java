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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.List;

/**
 * Layuimini 主页菜单信息。
 *
 * @author 王帅
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Menu implements Serializable {

    private static final long serialVersionUID = -3059199207907238187L;

    private String title;
    private String icon;
    @Builder.Default
    private String href = "";
    private final String target = "_self";
    private List<Menu> child;

    @Contract(pure = true)
    public Menu(String title, String icon, String href) {
        this.title = title;
        this.icon = icon;
        this.href = href;
    }

}
