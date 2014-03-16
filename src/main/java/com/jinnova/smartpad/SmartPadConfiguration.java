package com.jinnova.smartpad;

import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class SmartPadConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private String templateHello;

    @NotEmpty
    @JsonProperty
    private String defaultSearchNoFound = "Cannot find any info!";

    public String getTemplateHello() {
        return templateHello;
    }

    public String getDefaultSearchNoFound() {
        return defaultSearchNoFound;
    }
}