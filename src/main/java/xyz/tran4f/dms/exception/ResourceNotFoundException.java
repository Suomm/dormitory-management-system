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

package xyz.tran4f.dms.exception;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.DOWNLOAD_FILE_NOT_FOUND;

/**
 * <p>
 * 2021/1/23
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class ResourceNotFoundException extends MessageException {

    private static final long serialVersionUID = -531777133286386231L;

    public ResourceNotFoundException() {
        super(DOWNLOAD_FILE_NOT_FOUND);
    }

}
