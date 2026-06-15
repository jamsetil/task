package org.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI gymCrmOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("gym crm api")
                        .description("rest endpoints for gym crm. use POST /auth/login to obtain a JWT, "
                                + "then click Authorize and enter: Bearer <token>")
                        .version("1.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token from POST /auth/login or create profile response")));
    }

    @Bean
    public OpenApiCustomizer securedEndpointsCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((path, pathItem) -> {
                secureOperation(pathItem.getGet(), path, "GET");
                secureOperation(pathItem.getPost(), path, "POST");
                secureOperation(pathItem.getPut(), path, "PUT");
                secureOperation(pathItem.getPatch(), path, "PATCH");
                secureOperation(pathItem.getDelete(), path, "DELETE");
            });
        };
    }

    private void secureOperation(io.swagger.v3.oas.models.Operation operation, String path, String method) {
        if (operation == null || isPublicEndpoint(path, method)) {
            return;
        }
        operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    private boolean isPublicEndpoint(String path, String method) {
        return "POST".equals(method) && (
                "/auth/login".equals(path)
                        || "/trainees".equals(path)
                        || "/trainers".equals(path));
    }
}
