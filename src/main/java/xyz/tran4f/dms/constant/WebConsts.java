/*
 * Copyright (C) 2020-2021 the original author or authors.
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

package xyz.tran4f.dms.constant;

/**
 * Web 应用中的全局属性。
 *
 * @author 王帅
 * @since 1.0
 */
public final class WebConsts {

    private WebConsts() {
    }

    /**
     * 每周查宿任务结束之后，在该目录下生成以任务 ID 为名称的文件夹，用于存放生成的文件。
     * 因此该目录被作为历史记录目录存放每周查宿生成的文件，并在学期结束之后删除这个文件夹
     * 中的所有内容。
     */
    public static final String WEB_PORTFOLIO_STORES = "./portfolio/stores/";

    /**
     * 每周查宿任务的临时资源目录，存放上传的优差宿舍的图片，该路径会在创建新任务时被删除。
     */
    public static final String WEB_PORTFOLIO_ASSETS = "./portfolio/assets/";

}
