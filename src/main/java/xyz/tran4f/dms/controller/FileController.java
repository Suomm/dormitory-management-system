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

import lombok.Cleanup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import xyz.tran4f.dms.pojo.Response;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URLEncoder;

import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_ASSETS;

/**
 * <p>
 * 文件模块的具体业务流程控制。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@ApiIgnore
@Validated
@RestController
public class FileController {

    /**
     * <p>
     * 上传查宿过程中拍摄的图片。
     * </p>
     *
     * @param file 照片文件
     * @param room 房间号
     * @param index 照片索引
     * @return 保存成功的图片名称
     * @throws IOException 保存文件失败抛出此异常
     */
    @PostMapping("/upload/{room}/{index}")
    public Response upload(MultipartFile file,
                           @PathVariable String room,
                           @PathVariable String index) throws IOException {
        String filename = file.getOriginalFilename();
        // TODO 上传文件过大处理
        assert filename != null;
        int begin = filename.lastIndexOf('.');
        filename = room + index + filename.substring(begin);
        @Cleanup InputStream in = file.getInputStream();
        // TODO 是否包含同名文件（不含后缀名）
        File dest = new File(WEB_PORTFOLIO_ASSETS + room + "/" + filename);
        FileUtils.forceMkdirParent(dest);
        @Cleanup FileOutputStream fos = new FileOutputStream(dest);
        @Cleanup BufferedOutputStream bos = new BufferedOutputStream(fos);
        IOUtils.copy(in, bos);
        return Response.ok(filename.substring(0, filename.lastIndexOf('.')));
    }

    /**
     * <p>
     * 下载项目下的资源。<b>默认路径：./portfolio/<b/>。
     * </p>
     *
     * @param filename 文件名称
     * @return 文件下载流
     * @throws IOException 找不到文件或者生成流失败
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@NotNull String filename) throws IOException {
        FileSystemResource resource = new FileSystemResource("./portfolio/" + filename);
        int index = filename.lastIndexOf('/');
        filename = filename.substring(index + 1);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(URLEncoder.encode(filename, "UTF-8")).build());
        headers.add("content-type", "application/octet-stream");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(resource.getInputStream()));
    }

}
