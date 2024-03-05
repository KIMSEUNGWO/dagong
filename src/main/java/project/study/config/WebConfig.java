package project.study.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.study.customAnnotation.argumentresolver.PathRoomArgumentResolver;
import project.study.customAnnotation.argumentresolver.SessionLoginArgumentResolver;
import project.study.intercept.LoginInterceptor;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.RoomJpaRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MemberJpaRepository memberJpaRepository;
    private final RoomJpaRepository roomJpaRepository;
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigFactory().createMultipartConfig();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SessionLoginArgumentResolver(memberJpaRepository));
        resolvers.add(new PathRoomArgumentResolver(roomJpaRepository));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor(memberJpaRepository))
//            .order(1)
//            .addPathPatterns("/room/**")
//            .excludePathPatterns("/resource/**");
    }
}
