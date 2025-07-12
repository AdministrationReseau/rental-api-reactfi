package inc.yowyob.rental_api_reactive.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Rental API Reactive")
                .version("1.0.0")
                .description("API réactive pour la gestion de location de véhicules multi-agent")
                .contact(new Contact()
                    .name("Yowyob Inc")
                    .email("contact@yowyob.inc")
                    .url("https://yowyob.inc"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Serveur de développement"),
                new Server()
                    .url("https://api.rental.yowyob.inc")
                    .description("Serveur de production")
            ));
    }
}
