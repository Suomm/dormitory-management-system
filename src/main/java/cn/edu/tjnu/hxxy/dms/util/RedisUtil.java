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

package cn.edu.tjnu.hxxy.dms.util;

import cn.edu.tjnu.hxxy.dms.exception.MissingAttributeException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作的工具类。
 *
 * @author 王帅
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public final class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 判断缓存中是否包含这个键。
     *
     * @param key 键
     * @return {@code true} 包含该键，{@code false} 不含该键
     */
    @Contract(value = "null -> fail", pure = true)
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 从缓存中删除键值对。
     *
     * @param key 键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Contract("null -> fail")
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 从缓存中批量删除键值对。
     *
     * @param keys 键
     * @return 被删除键值对的数量
     */
    @Contract("null -> fail")
    public Long delete(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }

    // ===== Redis String 的相关操作 =====

    /**
     * 向缓存中存入键值对。
     *
     * @param key   键
     * @param value 值
     */
    @Contract("null,_ -> fail; _,null -> fail;")
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 向缓存中存入键值对，并指定其过期时间。
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    @Contract("null,_,_,_ -> fail; _,null,_,_ -> fail; _,_,_,null -> fail;")
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 根据键获取缓存中对应的值。
     *
     * @param key 键
     * @param <T> 值的类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Contract(value = "null -> fail", pure = true)
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据键获取缓存中对应的值，如果键不存在则抛出异常。
     *
     * @param key 键
     * @param msg 提示信息
     * @param <T> 值的类型
     * @return 值
     * @exception MissingAttributeException 不含有该键的时候抛出此异常
     */
    @NotNull
    @SuppressWarnings("unchecked")
    @Contract(value = "null,_ -> fail", pure = true)
    public <T> T orElseThrow(String key, String msg) {
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) {
            throw new MissingAttributeException(msg);
        }
        return (T) o;
    }

    /**
     * 根据键获取缓存中对应的值，不含该值则使用默认值。
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <T>          值的类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    @Contract(value = "null,_ -> fail", pure = true)
    public <T> T get(String key, T defaultValue) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return defaultValue;
        } else {
            return (T) value;
        }
    }

    // ===== Redis Set 的相关操作 =====

    /**
     * 获取 Redis Set 集合中元素的数量。
     *
     * @param key 键
     * @return 数量
     */
    @Contract("null -> fail")
    public int sSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        if (size == null) {
            return 0;
        }
        return size.intValue();
    }

    /**
     * 向 Redis Set 集合中插入数据。
     *
     * @param key   键
     * @param value 值
     */
    @Contract("null,_ -> fail")
    public void sSet(String key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 从 Redis Set 集合中删除数据。
     *
     * @param key   键
     * @param value 值
     */
    @Contract("null,_ -> fail")
    public void sRemove(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    // ===== Redis Hash 的相关操作 =====

    /**
     * 向 Redis Hash 中添加数据。
     *
     * @param key 键
     * @param hashKey 哈希键
     * @param value 值
     */
    @Contract("null,_,_ -> fail")
    public void hash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 向 Redis Hash 中批量添加数据。
     *
     * @param key 键
     * @param m 包含哈希键和值的集合
     */
    @Contract("null,_ -> fail")
    public void hash(String key, Map<String, ?> m) {
        redisTemplate.opsForHash().putAll(key, m);
    }

    /**
     * 从 Redis Hash 中删除数据。
     *
     * @param key 键
     * @param hashKeys 哈希键
     * @return 被删除数据的数量
     */
    @Contract("null,_ -> fail")
    public Long remove(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 从 Redis Hash 中取出数据。
     *
     * @param key 键
     * @param hashKey 哈希键
     * @param <T> 值的类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    @Contract("null,_ -> fail")
    public <T> T pop(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 取出 Redis Hash 中的所有值。
     *
     * @param key 键
     * @param <T> 值的类型
     * @return 哈希表中的所有值
     */
    @SuppressWarnings("unchecked")
    @Contract("null -> fail")
    public <T> List<T> values(String key) {
        return (List<T>) redisTemplate.opsForHash().values(key);
    }

}