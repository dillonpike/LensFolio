package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.utility.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Value("${spring.datasource.url}")
    private String dataSource;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathStart = "/" + Utility.getApplicationLocation(dataSource);
        String directory =  PortfolioApplication.IMAGE_DIR + pathStart + "/";
        registry.addResourceHandler(pathStart + "/**").addResourceLocations("file:" + directory);
    }
}