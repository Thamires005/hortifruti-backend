package br.unip.ads.pim.meuhortifruti.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.keycloak")
@Data
public class KeycloakProperties {

    private String realm;
    private String clientId;
    private String clientSecret;
}
