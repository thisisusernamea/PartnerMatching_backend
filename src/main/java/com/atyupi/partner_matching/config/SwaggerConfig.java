package com.atyupi.partner_matching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Contact;
@Configuration
@EnableSwagger2// 开启Swagger2的自动配置
@Profile({"dev","test"})// 动态配置当项目处于test、dev环境时显示swagger，处于prod时不显示
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean //配置docket以配置Swagger具体参数
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.atyupi.partner_matching.controller")) // 只扫描controller接口
                .paths(PathSelectors.any()) // 配置如何通过path过滤,即这里扫描所有请求的接口
                .build();
    }

    //配置文档信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户中心") // 标题
                .description("用户中心项目接口文档") //描述
                .version("v1.0") // 版本
                .contact(new Contact("cjj", "https://github/thisisusernamea", "2742895138@qq.com"))
                .build();
    }

    /**
     * 解决高版本springboot无法访问http://localhost:8080/swagger-ui.html
     * @param registry void
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
