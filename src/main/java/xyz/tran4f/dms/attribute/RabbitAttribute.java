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

package xyz.tran4f.dms.attribute;

/**
 * <p>
 * 消息队列 Rabbit MQ 的相关交换机、队列名称。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class RabbitAttribute {

    private RabbitAttribute() {
    }

    /**
     * <p>
     * 设置用户解锁操作的死信队列，与交换机 {@value EXCHANGE_USER_DIRECT} 绑定。
     * 消息达到指定的时间之后，将发送到 {@value QUEUE_USER_LOCKED_PROCESS} 队列，
     * 因此监听者无需监听该队列。
     * </p>
     */
    public static final String QUEUE_USER_LOCKED_DELAY = "user.locked.delay";

    /**
     * <p>
     * 设置用户解锁操作的实际监听队列，与交换机 {@value EXCHANGE_USER_DIRECT} 绑定。
     * 监听到消息之后，根据队列中的学号信息取消锁定用户。
     * </p>
     */
    public static final String QUEUE_USER_LOCKED_PROCESS = "user.locked.process";

    /**
     * <p>
     * 用户发送邮件的有关信息将发送到该队列，监听该队列以实现邮件异步发送。
     * </p>
     */
    public static final String QUEUE_EMAIL = "email";

    /**
     * <p>
     * 当最后一个具体任务完成时，会将任务菜单的 ID 发送到该队类中。系统后台将进行业务处理，
     * 根据信息生成各种文件并保存。
     * </p>
     */
    public static final String QUEUE_TASK = "task";

    /**
     * <p>
     * 实现用户锁定功能的交换机，绑定了死信队列 {@value QUEUE_USER_LOCKED_DELAY}
     * 和实际监听处理队列 {@value QUEUE_USER_LOCKED_PROCESS}。
     * </p>
     */
    public static final String EXCHANGE_USER_DIRECT = "user.direct";

}
