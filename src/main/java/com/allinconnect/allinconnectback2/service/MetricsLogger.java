package com.allinconnect.allinconnectback2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MetricsLogger {
    private static final Logger log = LoggerFactory.getLogger(MetricsLogger.class);

    public void log(String metricName, Map<String, Object> tags) {
        log.info("METRIC | Name: {} | Tags: {}", metricName, tags);
    }

    public void logError(String metricName, Map<String, Object> tags, Throwable e) {
        log.error("METRIC_ERROR | Name: {} | Tags: {} | Message: {}", metricName, tags, e.getMessage());
    }
}
