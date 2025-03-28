package com.shivam.referral_tracking_system.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customPathOrder() {
        return openApi -> {
            Paths originalPaths = openApi.getPaths();

            Map<String, PathItem> orderedPaths = new LinkedHashMap<>();

            if (originalPaths.containsKey("/api/signup")) {
                orderedPaths.put("/api/signup", originalPaths.get("/api/signup"));
            }

            if (originalPaths.containsKey("/api/users/{userId}/complete-profile")) {
                orderedPaths.put("/api/users/{userId}/complete-profile",
                        originalPaths.get("/api/users/{userId}/complete-profile"));
            }

            for (Map.Entry<String, PathItem> entry : originalPaths.entrySet()) {
                if (!orderedPaths.containsKey(entry.getKey())) {
                    orderedPaths.put(entry.getKey(), entry.getValue());
                }
            }

            openApi.setPaths(new Paths());
            openApi.getPaths().putAll(orderedPaths);
        };
    }
}
