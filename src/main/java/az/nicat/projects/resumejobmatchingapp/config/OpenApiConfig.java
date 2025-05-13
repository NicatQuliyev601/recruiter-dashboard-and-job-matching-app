package az.nicat.projects.resumejobmatchingapp.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@OpenAPIDefinition(
        info = @Info(
                title = "Financial Assistant",
                version = "1.0",
                description = "AI-based Finance Assistant app"
        ),
        security = @SecurityRequirement(name = "bearerAuth"),
        servers = {
                @Server(url = "https://recruiter-dashboard-and-job-matching-app-production.up.railway.app")  
        }
)
public class OpenApiConfig {


}

