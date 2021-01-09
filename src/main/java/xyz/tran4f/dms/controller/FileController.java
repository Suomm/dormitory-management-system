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

package xyz.tran4f.dms.controller;

import io.swagger.annotations.Api;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 2021/1/6
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@RestController
//@Secured({"ROLE_MANAGER"})
@Api(tags = "文件模块的程序接口")
public class FileController {

    @PostMapping("upload")
    public void upload(MultipartFile image) throws IOException {
        System.out.println(image.getOriginalFilename());
        // suffix
        image.transferTo(new File("./portfolio/"));
    }

    @GetMapping("download/{filename}")
    public ResponseEntity<FileSystemResource> download(@PathVariable String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename).build());
        headers.add("content-type", "application/octet-stream");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().headers(headers).body(new FileSystemResource("./README.md"));
    }

}
