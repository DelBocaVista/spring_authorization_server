package com.example.AuthorizationServer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private static final String RESOURCE_ID = "resource-server-rest-api";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .authorizeRequests()
                    .antMatchers("/organizations/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                    .antMatchers("/users/verify").hasAnyAuthority("USER", "ADMIN", "SUPERADMIN") // Maybe should be just USER?
                    .antMatchers("/users/admins/**").hasAuthority("SUPERADMIN")
                    .antMatchers("/users/**").hasAuthority("ADMIN")
                .anyRequest().authenticated();
    }
}
