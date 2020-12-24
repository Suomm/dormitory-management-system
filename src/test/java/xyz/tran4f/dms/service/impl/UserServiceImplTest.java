/*
 * Copyright (C) 2020 Wang Shuai (suomm.macher@foxmail.com)
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

package xyz.tran4f.dms.service.impl;

import org.apache.tomcat.util.security.MD5Encoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.DigestUtils;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author 王帅
 * @since 1.0
 */
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void getKey() {
        String secretKey = UUID.randomUUID().toString(); //密钥
        Timestamp outDate = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000);//30分钟后过期
        long date = outDate.getTime() / 1000 * 1000;     //忽略毫秒数
        String key = "王帅" + "$" + date + "$" + secretKey;
        String digitalSignature = DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));     //数字签名
        String emailTitle = "密码找回";
        String basePath = "http://localhost:8080/dms/";
        String resetPassHref = basePath + "user/reset_password?sid=" + digitalSignature + "&userName=lhw";
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href=" + resetPassHref + "target='_BLANK'>点击我重新设置密码</a>" +
                "<br/>tips:本邮件超过30分钟,链接将会失效，需要重新申请'找回密码'" + key + "\t" + digitalSignature;
        System.out.print(resetPassHref);
    }

    @Test
    public void transfer() throws Exception {
        String secretKey = UUID.randomUUID().toString(); //密钥
        Timestamp outDate = new Timestamp(System.currentTimeMillis()+30*60*1000);//30分钟后过期
        long date = outDate.getTime()/1000*1000;     //忽略毫秒数
        String key = "王帅"+"$"+date+"$"+secretKey;
        String digitalSignature = MD5Encoder.encode(key.getBytes(StandardCharsets.UTF_8));     //数字签名
        String emailTitle = "密码找回";
        String basePath = "http://localhost:8080/dms/";
        String resetPassHref = basePath+"user/reset_password?sid="+digitalSignature+"&userName=lhw";
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href="+resetPassHref+ "target='_BLANK'>点击我重新设置密码</a>" +
                "<br/>tips:本邮件超过30分钟,链接将会失效，需要重新申请'找回密码'"+key+"\t"+digitalSignature;
        System.out.print(resetPassHref);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        //发送者
        helper.setFrom("suomm.macher@foxmail.com");
        //接收者
        helper.setTo("2319397152@qq.com");
        //邮件主题
        helper.setSubject(emailTitle);
        //邮件内容
        helper.setText(emailContent, true);

//        javaMailSender.send(message);
    }


}