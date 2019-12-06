package com.eamonscullion.bankingdemobasic.app.config;

import static java.util.Collections.singletonList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
      .paths(PathSelectors.any())
      .build()
      .apiInfo(metadata())
      .securitySchemes(singletonList(apiKey()))
      .securityContexts(singletonList(securityContext()))
      .tags(new Tag("Authentication", "Operations about authentication"), remainingTags());
  }

  private ApiInfo metadata() {
    return new ApiInfoBuilder()
      .title("Banking Demo API")
      .description("For this demo, you can create your own account using the `POST /accounts` endpoint, or use the preconfigured `10000000` account (pin: 1111).\n\n You can log in using the `/auth/login` endpoint using your account number and pin, which will return a token in response.\n You can then click on the right top button `Authorize` and enter it with the prefix \"Bearer \".\n The bearer token will be passed with every request, providing you access to the rest of the app, enjoy!")
      .version("1.0.0")
      .build();
  }

  private ApiKey apiKey() {
    return new ApiKey("Bearer <token>", "Authorization", "Header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
      .securityReferences(singletonList(SecurityReference.builder()
        .reference("Bearer <token>")
        .scopes(new AuthorizationScope[0])
        .build()))
      .build();
  }

  private Tag[] remainingTags() {
    return new Tag[]{
      new Tag("Accounts", "Operations about accounts"),
      new Tag("Transactions", "Operations about transactions")
    };
  }
}
