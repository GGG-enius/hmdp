package com.hmdp.config;

import com.hmdp.utils.LoginInterceptor;
import com.hmdp.utils.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration//说明该类由spring构建
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override //添加拦截器                      //拦截器的注册器
    public void addInterceptors(InterceptorRegistry registry) {
        //登录校验拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(//排除哪些不被拦截
                        "/user/code",
                        "/voucher/**",
                        "/blog/hot",
                        "/shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/user/login"
                ).order(1);//拦部分请求,order控制执行顺序
        //token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);//所有请求都拦（保证token一直刷新）
    }
}
