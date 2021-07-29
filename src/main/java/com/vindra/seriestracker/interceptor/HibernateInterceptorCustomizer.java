package com.vindra.seriestracker.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class HibernateInterceptorCustomizer implements HibernatePropertiesCustomizer {

    @Autowired
    KindleInterceptor kindleInterceptor;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", kindleInterceptor);
    }
}
