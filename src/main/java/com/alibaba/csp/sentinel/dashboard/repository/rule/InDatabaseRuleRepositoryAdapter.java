/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.repository.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leyou
 */
public abstract class InDatabaseRuleRepositoryAdapter<T extends RuleEntity> implements RuleRepository<T, Long> {

    /**
     * {@code <machine, <id, rule>>}
     */
    private Map<MachineInfo, Map<Long, T>> machineRules = new ConcurrentHashMap<>(16);
//    private Map<Long, T> allRules = new ConcurrentHashMap<>(16);

    private Map<String, Map<Long, T>> appRules = new ConcurrentHashMap<>(16);

    private static final int MAX_RULES_SIZE = 10000;

    @Resource
    ApplicationContext applicationContext;

    public void init() {
        BaseMapper<T> mapper = getMapper();
        List<T> allRules = mapper.selectList(new LambdaQueryWrapper<>());
        if (allRules != null && allRules.size() > 0) {
            for (T rule : allRules) {
                save(rule);
            }
        }
    }

    @Override
    public T save(T entity) {
//        if (entity.getId() == null) {
//            entity.setId(nextId());
//        }
        T processedEntity = preProcess(entity);
        if (processedEntity != null) {
            BaseMapper<T> mapper = getMapper();
            if (entity.getId() != null) {
                mapper.updateById(entity);
            } else {
                mapper.insert(entity);
            }


//            allRules.put(processedEntity.getId(), processedEntity);


            machineRules.computeIfAbsent(MachineInfo.of(processedEntity.getApp(), processedEntity.getIp(),
                    processedEntity.getPort()), e -> new ConcurrentHashMap<>(32))
                    .put(processedEntity.getId(), processedEntity);
            appRules.computeIfAbsent(processedEntity.getApp(), v -> new ConcurrentHashMap<>(32))
                    .put(processedEntity.getId(), processedEntity);
        }

        return processedEntity;
    }

    public abstract void publishMachineRules(String app, String ip, int port);

    private BaseMapper<T> getMapper() {
        ParameterizedType superGenericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (BaseMapper<T>) applicationContext.getBean(StringUtils.uncapitalize(((Class) superGenericSuperclass.getActualTypeArguments()[0]).getSimpleName() + "Mapper"));
    }

    @Override
    public List<T> saveAll(List<T> rules) {
        // TODO: check here.
        BaseMapper<T> mapper = getMapper();
        mapper.delete(new LambdaQueryWrapper<>());


//        allRules.clear();
        machineRules.clear();
        appRules.clear();

        if (rules == null) {
            return null;
        }
        List<T> savedRules = new ArrayList<>(rules.size());
        for (T rule : rules) {
            savedRules.add(save(rule));
        }
        return savedRules;
    }

    @Override
    public T delete(Long id) {
        BaseMapper<T> mapper = getMapper();
        T entity = mapper.selectById(id);
        mapper.deleteById(id);
        if (entity != null) {
            if (appRules.get(entity.getApp()) != null) {
                appRules.get(entity.getApp()).remove(id);
            }
            machineRules.get(MachineInfo.of(entity.getApp(), entity.getIp(), entity.getPort())).remove(id);
        }
        return entity;
    }

    @Override
    public T findById(Long id) {
        BaseMapper<T> mapper = getMapper();
        return mapper.selectById(id);
    }

    @Override
    public List<T> findAllByMachine(MachineInfo machineInfo) {
        Map<Long, T> entities = machineRules.get(machineInfo);
        if (entities == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(entities.values());
    }


    @Override
    public List<T> findAllByApp(String appName) {
        AssertUtil.notEmpty(appName, "appName cannot be empty");
        Map<Long, T> entities = appRules.get(appName);
        if (entities == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(entities.values());
    }

    public void clearAll() {
        BaseMapper<T> mapper = getMapper();
        mapper.delete(new LambdaQueryWrapper<>());
        machineRules.clear();
        appRules.clear();
    }

    protected T preProcess(T entity) {
        return entity;
    }

    /**
     * Get next unused id.
     *
     * @return next unused id
     */
    abstract protected long nextId();
}
