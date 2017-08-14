package org.jianyi.yiriyigu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(Swagger.apiInfo()).select()
            .apis(
                RequestHandlerSelectors.basePackage("org.jianyi.yiriyigu.web"))
            .paths(PathSelectors.any()).build();
    }

    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("yiriyigu").description("yiriyigu")
            .termsOfServiceUrl("http://localhost:8080/yiriyigu").version("1.0")
            .build();
    }
}
