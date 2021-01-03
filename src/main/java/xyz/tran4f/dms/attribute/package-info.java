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

/**
 * <p>
 * 提供包含自定义公共属性的参数类。
 * </p>
 * <p>
 * 该参数类仅封装一些常用的统一常量，方便后期维护。应该满足以下模板样式：
 * <blockquote><pre>
 *     public final class Attribute {
 *
 *         private Attribute() {}
 *
 *         public static final ... 定义的常量
 *
 *     }
 * </pre></blockquote>
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
package xyz.tran4f.dms.attribute;