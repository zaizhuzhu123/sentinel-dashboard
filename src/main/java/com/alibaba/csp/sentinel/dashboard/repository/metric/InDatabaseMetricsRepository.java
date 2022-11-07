package com.alibaba.csp.sentinel.dashboard.repository.metric;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.mapper.MetricEntityMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class InDatabaseMetricsRepository implements MetricsRepository<MetricEntity> {


    @Resource
    private MetricEntityMapper metricEntityMapper;

    @Override
    public void save(MetricEntity metric) {
        metricEntityMapper.insert(metric);
        //删除5分钟以前的数据
        LambdaQueryWrapper<MetricEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(MetricEntity::getTimestamp, new Date(System.currentTimeMillis() - (1000 * 60 * 10)));
        metricEntityMapper.delete(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        metrics.forEach(this::save);
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        LambdaQueryWrapper<MetricEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(app), MetricEntity::getApp, app);
        queryWrapper.eq(StringUtils.isNotBlank(resource), MetricEntity::getResource, resource);
        queryWrapper.gt(MetricEntity::getTimestamp, new Date(startTime - (1000 * 60 * 5)));
        queryWrapper.lt(MetricEntity::getTimestamp, new Date(endTime));
        return metricEntityMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtils.isBlank(app)) {
            return results;
        }
        LambdaQueryWrapper<MetricEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(app), MetricEntity::getApp, app);
        queryWrapper.gt(MetricEntity::getTimestamp, System.currentTimeMillis() - (1000 * 60));
        List<MetricEntity> metricEntities = metricEntityMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(metricEntities)) {
            return results;
        }
        Map<String, MetricEntity> resourceCount = new HashMap<>(32);
        for (MetricEntity metricEntity : metricEntities) {
            String resource = metricEntity.getResource();
            if (resourceCount.containsKey(resource)) {
                MetricEntity oldEntity = resourceCount.get(resource);
                oldEntity.addPassQps(metricEntity.getPassQps());
                oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                oldEntity.addBlockQps(metricEntity.getBlockQps());
                oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                oldEntity.addCount(1);
            } else {
                resourceCount.put(resource, MetricEntity.copyOf(metricEntity));
            }
        }
        return resourceCount.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    MetricEntity e1 = o1.getValue();
                    MetricEntity e2 = o2.getValue();
                    int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                    if (t != 0) {
                        return t;
                    }
                    return e2.getPassQps().compareTo(e1.getPassQps());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
