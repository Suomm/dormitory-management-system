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

package cn.edu.tjnu.hxxy.dms.event;

import cn.edu.tjnu.hxxy.dms.constant.RabbitConst;
import cn.edu.tjnu.hxxy.dms.entity.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 监听 {@link RabbitConst#QUEUE_EMAIL} 消息队列，接收封装有关信息的 {@link Email} 对象，
 * 实现异步发送邮件功能。
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendListener {

    /**
     * 注入邮件的发送者邮箱地址。
     */
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender javaMailSender;

    @RabbitListener(queues = {RabbitConst.QUEUE_EMAIL})
    public void sendEmail(@NotNull Email email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(from);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(email.getText(), true);
        javaMailSender.send(message);
        log.info("发送主题为 {} 的邮件至邮箱 {}", email.getSubject(), email.getTo());
    }

}
