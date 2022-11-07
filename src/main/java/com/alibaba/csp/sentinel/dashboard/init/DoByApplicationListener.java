package com.alibaba.csp.sentinel.dashboard.init;

import com.alibaba.csp.sentinel.dashboard.repository.rule.InDatabaseRuleRepositoryAdapter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DoByApplicationListener implements ApplicationListener<ApplicationStartedEvent> {
    public DoByApplicationListener() {
        System.out.println("DoByApplicationListener constructor");
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        String[] beanNamesForTypes = event.getApplicationContext().getBeanFactory().getBeanNamesForType(InDatabaseRuleRepositoryAdapter.class);
        for (String beanName : beanNamesForTypes) {
            InDatabaseRuleRepositoryAdapter bean = (InDatabaseRuleRepositoryAdapter) event.getApplicationContext().getBean(beanName);
            bean.init();
        }
    }
}