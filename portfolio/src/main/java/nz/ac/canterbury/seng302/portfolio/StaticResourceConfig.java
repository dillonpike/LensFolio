package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.utility.GeneralUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is used to set up dynamic file sharing through spring-boot.
 * This is needed to show images outside the static folder.
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Value("${spring.datasource.url}")
    private String dataSource;

    /**
     * Adds all images in the currently running system (dev, test, prod) to the visible files spring can share.
     * @param registry  The current "visible" files.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathStart = "/" + GeneralUtility.getApplicationLocation(dataSource);
        String directory =  PortfolioApplication.getImageDir() + pathStart + "/";
        registry.addResourceHandler(pathStart + "/**").addResourceLocations("file:" + directory);
    }
}