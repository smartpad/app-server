package com.jinnova.smartpad;

import java.sql.SQLException;

import com.jinnova.smartpad.drilling.DetailManager;
import com.jinnova.smartpad.health.TemplateHealthCheck;
import com.jinnova.smartpad.partner.SmartpadCommon;
import com.jinnova.smartpad.resources.ActivityResource;
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
    public void run(SmartPadConfiguration configuration, Environment environment) {
    	final String templateHello = configuration.getTemplateHello();
        final String defaultSearchNoFound = configuration.getDefaultSearchNoFound();
        environment.addResource(new FeedResource(defaultSearchNoFound));
        environment.addResource(new ActivityResource());
        environment.addHealthCheck(new TemplateHealthCheck(templateHello));
        SmartpadCommon.initialize("localhost", null, "smartpad_drill", "root", "");
        try {
			DetailManager.initialize();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        /*try {
			DBQuery.initialize("root", "", "jdbc:mysql://localhost/smartpad");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}*/
    }

}