package com.devsenior.jmorera.bibliokeep.security;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file.directory}")
    private String directoryName; // ./files/upload

    @Value("${app.file.publish-path}")
    private String publishPathName; // /public

    @Value("${app.file.cache-period}")
    private Integer cachePeriod;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Limpiamos el publishPath para que sea exactamente "/public/**"
        String pathPattern = publishPathName.endsWith("/") ? publishPathName + "**" : publishPathName + "/**";
        if (!pathPattern.startsWith("/")) {
            pathPattern = "/" + pathPattern;
        }

        // 2. Preparamos la ruta física del sistema de archivos
        // Es vital que termine en "/" para que Spring busque DENTRO de la carpeta
        String location = directoryName.endsWith("/") ? directoryName : directoryName + "/";

        // 3. Usamos "file:" seguido de la ruta para recursos externos al JAR
        registry.addResourceHandler(pathPattern)
                .addResourceLocations("file:" + location)
                .setCachePeriod(cachePeriod);

        // Log preventivo para que veas en consola qué está mapeando Spring
        System.out.println("Mapping " + pathPattern + " to physical location: " + new File(location).getAbsolutePath());
    }
}