package com.jinnova.smartpad;

import com.jinnova.smartpad.health.TemplateHealthCheck;
import com.jinnova.smartpad.resources.FeedResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class SmartPadAppService extends Service<SmartPadConfiguration> {
    public static void main(String[] args) throws Exception {
        new SmartPadAppService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SmartPadConfiguration> bootstrap) {
        bootstrap.setName("smart-pad");
    }

    @Override
    public void run(SmartPadConfiguration configuration,
                    Environment environment) {
    	final String templateHello = configuration.getTemplateHello();
        final String defaultSearchNoFound = configuration.getDefaultSearchNoFound();
        environment.addResource(new FeedResource(defaultSearchNoFound));
        environment.addHealthCheck(new TemplateHealthCheck(templateHello));
    }

}