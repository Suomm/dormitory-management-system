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

package xyz.tran4f.dms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import static xyz.tran4f.dms.attribute.RabbitAttribute.*;

/**
 * <p>
 * 消息队列 Rabbit MQ 配置类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {

    // 交换机、队列的声明

    @Bean
    public Queue userLockedDelay() {
        return QueueBuilder.durable(QUEUE_USER_LOCKED_DELAY)
                .withArgument("x-dead-letter-exchange", EXCHANGE_USER_DIRECT)
                .withArgument("x-dead-letter-routing-key", QUEUE_USER_LOCKED_PROCESS)
                // 死信队列设置 12 小时后消息过期。
                .withArgument("x-message-ttl", 12 * 60 * 60 * 1000)
                .build();
    }

    @Bean
    public Queue email() {
        return new Queue(QUEUE_EMAIL);
    }

    @Bean
    public Queue task() {
        return new Queue(QUEUE_TASK);
    }

    @Bean
    public Queue userLockedProcess() {
        return new Queue(QUEUE_USER_LOCKED_PROCESS);
    }

    @Bean
    public DirectExchange userDirect() {
        return new DirectExchange(EXCHANGE_USER_DIRECT);
    }

    @Bean
    public Binding bindingLockedDelay() {
        return BindingBuilder.bind(userLockedDelay())
                .to(userDirect())
                .with(QUEUE_USER_LOCKED_DELAY);
    }

    @Bean
    public Binding bindingLockedProcess() {
        return BindingBuilder.bind(userLockedProcess())
                .to(userDirect())
                .with(QUEUE_USER_LOCKED_PROCESS);
    }


    // 配置 RabbitMQ 使用 JSON 方式处理对象。

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}