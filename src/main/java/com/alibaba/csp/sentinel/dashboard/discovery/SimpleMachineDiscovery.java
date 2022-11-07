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
package com.alibaba.csp.sentinel.dashboard.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.csp.sentinel.dashboard.DashboardApplication;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InDatabaseRuleRepositoryAdapter;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;
import com.alibaba.csp.sentinel.util.AssertUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author leyou
 */
@Component
public class SimpleMachineDiscovery implements MachineDiscovery {

    private final ConcurrentMap<String, AppInfo> apps = new ConcurrentHashMap<>();

    @Resource
    ApplicationContext applicationContext;

    private static Logger logger = LoggerFactory.getLogger(SimpleMachineDiscovery.class);


    @Override
    public long addMachine(MachineInfo machineInfo) {
        logger.info("addMachine");
        AssertUtil.notNull(machineInfo, "machineInfo cannot be null");
        AppInfo appInfo = apps.get(machineInfo.getApp());
        boolean isFirst = false;
        if (appInfo == null) {//首次注册，发送配置
            appInfo = new AppInfo(machineInfo.getApp(), machineInfo.getAppType());
            apps.put(machineInfo.getApp(), appInfo);

            //回调推送配置
            isFirst = true;
        }
        MachineInfo machine = appInfo.getMachine(machineInfo.getIp(), machineInfo.getPort()).orElse(null);
        //当掉线超过10秒 就重新设置规则
        boolean isHealthy = true;
        isHealthy = isHealthy(machine, isHealthy);
        if (machine == null || !isHealthy)//如果是从来没有注册过，或者是重连
        {
            isFirst = true;
        }

        //添加新的机器信息
        appInfo.addMachine(machineInfo);

        if (isFirst) {
            logger.info("重新设置规则");
            sendRuleToMachine(machineInfo);
        }
        return 1;
    }

    private boolean isHealthy(MachineInfo machine, boolean isHealthy) {
        if (machine != null && (System.currentTimeMillis() - machine.getLastHeartbeat()) >= (1000 * 11)) {
            isHealthy = false;
        }
        if (machine != null && (System.currentTimeMillis() - machine.getLastHeartbeat()) < (1000 * 10)) {
            isHealthy = false;
        }
        return isHealthy;
    }

    //向机器发送配置
    public void sendRuleToMachine(MachineInfo machineInfo) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(InDatabaseRuleRepositoryAdapter.class);
        for (String beanName : beanNamesForType) {
            ((InDatabaseRuleRepositoryAdapter) applicationContext.getBean(beanName)).publishMachineRules(machineInfo.getApp(), machineInfo.getIp(), machineInfo.getPort());
        }
    }

    @Override
    public boolean removeMachine(String app, String ip, int port) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        AppInfo appInfo = apps.get(app);
        if (appInfo != null) {
            return appInfo.removeMachine(ip, port);
        }
        return false;
    }

    @Override
    public List<String> getAppNames() {
        return new ArrayList<>(apps.keySet());
    }

    @Override
    public AppInfo getDetailApp(String app) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        return apps.get(app);
    }

    @Override
    public Set<AppInfo> getBriefApps() {
        return new HashSet<>(apps.values());
    }

    @Override
    public void removeApp(String app) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        apps.remove(app);
    }

}
