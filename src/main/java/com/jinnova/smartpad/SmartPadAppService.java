package com.jinnova.smartpad;

import java.sql.SQLException;

import com.jinnova.smartpad.health.TemplateHealthCheck;
import com.jinnova.smartpad.partner.IDetailManager;
import com.jinnova.smartpad.partner.SmartpadCommon;
import com.jinnova.smartpad.resources.ActivityResource;
import com.jinnova.smartpad.resources.FeedResource;
import com.jinnova.smartpad.resources.FeedResourceWeb;
import com.jinnova.smartpad.resources.ImageResource;
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
    public void run(SmartPadConfiguration configuration, Environment environment) throws SQLException {
        SmartpadCommon.initialize("localhost", null, "smartpad_drill", "root", "", "./imaging/in-queue", "./imaging/root");
    	final String templateHello = configuration.getTemplateHello();
        final String defaultSearchNoFound = configuration.getDefaultSearchNoFound();
        environment.addResource(new FeedResource(defaultSearchNoFound, IDetailManager.REST_SCHEME + "/" + IDetailManager.REST_FEEDS));
        environment.addResource(new FeedResourceWeb(defaultSearchNoFound));
        environment.addResource(new ImageResource());
        environment.addResource(new ActivityResource());
        environment.addHealthCheck(new TemplateHealthCheck(templateHello));
        
        /*try {
			DetailManager.initialize();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}*/
        /*try {
			DBQuery.initialize("root", "", "jdbc:mysql://localhost/smartpad");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}*/
    }

}