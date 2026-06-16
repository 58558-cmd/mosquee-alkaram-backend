package be.alkaram.mosquee.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class StripeWebhookConfig {

    // S'assurer que le body du webhook Stripe arrive en raw (non parsé)
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> stripeRawBodyFilter() {
        FilterRegistrationBean<OncePerRequestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain)
                    throws ServletException, IOException {
                chain.doFilter(request, response);
            }
        });
        bean.addUrlPatterns("/api/stripe/webhook");
        bean.setOrder(1);
        return bean;
    }
}