package supercash.distancematrix;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configures Swagger using SpringFox project.
 *
 * http://springfox.github.io/springfox/docs/current/
 *
 * Full example https://github.com/springfox/springfox-demos/blob/master/boot-swagger/src/main/java/springfoxdemo
 *                /boot/swagger/Application.java
 *
 * Blog: http://heidloff.net/article/usage-of-swagger-2-0-in-spring-boot-applications-to-document-apis/
 * Blog2: http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * @author mdesales
 *
 */
@Configuration
public class SwaggerConfig {

  private static final Contact SWAGGER_CONTACT = new Contact("Marcello de Sales",
      "https://gitlab.com/marcellodesales", "marcello@super.cash");

  /**
   * @return The Docket for the Publisher endpoints. According to the documentation, Docket stands for A summary or
   * other brief statement of the contents of a document; an abstract.
   */
  @Bean
  public Docket publisherApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        //.groupName("publisher") If the devInternal supports groups, then group them here
        .apiInfo(apiInfo())
        .select()
        // Only show the endpoints from this package, not the error controller
        // https://stackoverflow.com/questions/32941917/remove-basic-error-controller-in-springfox-swaggerui/33720866#33720866
        .apis(RequestHandlerSelectors.basePackage(SwaggerConfig.class.getPackage().getName()))
        .build();
  }

  /**
   * @return The basic information about the swagger configuration for all Docket beans above.
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Super Cash - Distance Matrix Service")
        .description("Gets the distance between two points")
        .termsOfServiceUrl("https://gitlab.com/supercash/privacy")
        .contact(SWAGGER_CONTACT)
        .license("SuperCash Proprietary")
        .licenseUrl("https://github.com/supercash/proprietary")
        //.version("v1")
        .build();
  }
}