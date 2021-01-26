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

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    @Nullable
    @Contract(value = "null -> fail", pure = true)
    public <T> T get(String key) {
        return get(key, null);
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
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public boolean listPush(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values) != null;
    }

    public void listRemove(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public int listSize(String key) {
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) { return 0; }
        return size.intValue();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listGet(String key) {
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }

    @SuppressWarnings("unchecked")
    public <T> T listFirst(String key) {
        List<T> v = (List<T>) redisTemplate.opsForList().leftPop(key);
        assert v != null;
        return v.get(0);
    }

    /**
     * <p>
     * 向 Redis Set 集合中插入数据。
     * </p>
     *
     * @param key 键
     * @param value 值
     * @return {@code null} 插入失败
     */
    @Contract("null,_ -> fail")
    public Long setAdd(String key, Object... value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public void unmodifiableSet(String key, Object... values) {
        redisTemplate.delete(key);
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * <p>
     * 从 Redis Set 集合中删除数据。
     * </p>
     *
     * @param key 键
     * @param values 值
     * @return {@code null} 插入失败
     */
    @Contract("null,_ -> fail")
    public Long setRemove(String key, Object[] values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * <p>
     * 获取 Set 集合中的所有成员。
     * </p>
     *
     * @param key 键
     * @return 集合中的值
     */
    @Contract(value = "null -> fail", pure = true)
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * <p>
     * 合并指定键的集合。
     * </p>
     *
     * @param keys 键
     * @return 合并之后的集合
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <T> Set<T> setUnion(Collection<String> keys) {
        return (Set<T>) redisTemplate.opsForSet().union(keys);
    }

}