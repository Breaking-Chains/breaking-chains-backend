package com.breakingchains.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        String descriptionMarkdown = """
                ## Breaking Chains REST API Specification
                
                Welcome to the backend API specification for **Breaking Chains** - a platform designed to help users break free from habits and build streak resilience.
                
                ### Authentication Flow Guide
                1. **Authenticate**: Send a `POST` request to `/api/v1/auth/login` or `/api/v1/auth/register` with credentials.
                2. **Retrieve Token**: Extract the `accessToken` and `refreshToken` from the response.
                3. **Authorize in Swagger**: Click the **"Authorize"** button on the top right, paste your `accessToken` (without the `Bearer ` prefix), and click Authorize.
                4. **Token Expiration / Refresh**: The access token lasts 15 minutes. To refresh, call `POST /api/v1/auth/refresh` with the `refreshToken` to obtain a fresh access token.
                
                ### Platform Considerations
                * **Web Clients**: Store tokens securely (e.g. state memory, Secure HttpOnly cookies).
                * **Mobile Clients (iOS / Android)**: Store tokens in secure system keychains (iOS Keychain or Android EncryptedSharedPreferences).
                """;

        return new OpenAPI()
                .info(new Info()
                        .title("Breaking Chains API")
                        .version("1.0.0")
                        .description(descriptionMarkdown))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server"),
                        new Server().url("https://staging-api.breakingchains.com").description("Staging / QA Server"),
                        new Server().url("https://breaking-chains-backend.onrender.com").description("Production Server (Render)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
