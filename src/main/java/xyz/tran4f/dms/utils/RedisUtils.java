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

package xyz.tran4f.dms.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import xyz.tran4f.dms.exception.MissingAttributeException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Redis 缓存操作的工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Component
public final class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * <p>
     * 判断缓存中是否包含这个键。
     * </p>
     *
     * @param key 键
     * @return {@code true} 包含该键，{@code false} 不含该键
     */
    @Contract(value = "null -> fail", pure = true)
    public boolean hasKey(String key) {
        Boolean exist = redisTemplate.hasKey(key);
        return exist != null && exist;
    }

    /**
     * <p>
     * 向缓存中存入键值对。
     * </p>
     *
     * @param key 键
     * @param value 值
     */
    @Contract("null,_ -> fail; _,null -> fail;")
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * <p>
     * 向缓存中存入键值对，并指定其过期时间。
     * </p>
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    @Contract("null,_,_,_ -> fail; _,null,_,_ -> fail; _,_,_,null -> fail;")
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * <p>
     * 根据键获取缓存中对应的值。
     * </p>
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
     * <p>
     * 根据键获取缓存中对应的值，不含该值则使用默认值。
     * </p>
     *
     * @param key 键
     * @param defaultValue 默认值
     * @param <T> 值的类型
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

    /**
     * <p>
     * 从缓存中删除键值对。
     * </p>
     *
     * @param key 键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Contract(value = "null -> fail")
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Contract(value = "null -> fail")
    public Long delete(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * <p>
     * 获取 Redis Set 集合中元素的数量。
     * </p>
     *
     * @param key 键
     * @return 数量
     */
    public int sSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        if (size == null) { return 0; }
        return size.intValue();
    }

    /**
     * <p>
     * 向 Redis Set 集合中插入数据。
     * </p>
     *
     * @param key 键
     * @param value 值
     */
    @Contract("null,_ -> fail")
    public void sSet(String key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * <p>
     * 从 Redis Set 集合中删除数据。
     * </p>
     *
     * @param key 键
     * @param value 值
     */
    @Contract("null,_ -> fail")
    public void sRemove(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public void hash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hash(String key, Map<String, ?> m) {
        redisTemplate.opsForHash().putAll(key, m);
    }

    public Long remove(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    @SuppressWarnings("unchecked")
    public <T> T pop(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> values(String key) {
        return (List<T>) redisTemplate.opsForHash().values(key);
    }

}