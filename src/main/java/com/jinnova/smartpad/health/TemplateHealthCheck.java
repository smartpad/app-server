package com.jinnova.smartpad.health;

import com.yammer.metrics.core.HealthCheck;

public class TemplateHealthCheck extends HealthCheck {
    private final String templateHello;

    public TemplateHealthCheck(String templateHello) {
        super("templateHello");
        this.templateHello = templateHello;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(templateHello, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
}