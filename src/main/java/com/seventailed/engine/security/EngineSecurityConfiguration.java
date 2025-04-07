package com.seventailed.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class EngineSecurityConfiguration {
    @Autowired
    private EngineUserAuthenticationProvider engineUserAuthenticationProvider;

    @Autowired
    private EngineOAuth2Service engineOAuth2Service;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CsrfConfigurer<HttpSecurity> httpSecurityCsrfConfigurer) {
                httpSecurityCsrfConfigurer.csrfTokenRepository(new CookieCsrfTokenRepository());
            }
        }).authorizeHttpRequests(new Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
                authorizationManagerRequestMatcherRegistry.requestMatchers("/","/login", "/logout", "/register", "/confirmEmail", "/error", "/oauth2/**", "/media/**", "/stylesheets/**", "/js/**", "/3d-objects/**", "/fonts/**").permitAll().anyRequest().authenticated();
            }
        }).authenticationProvider(engineUserAuthenticationProvider).formLogin(new Customizer<FormLoginConfigurer<HttpSecurity>>() {
            @Override
            public void customize(FormLoginConfigurer<HttpSecurity> httpSecurityFormLoginConfigurer) {
                httpSecurityFormLoginConfigurer.loginPage("/login");
                httpSecurityFormLoginConfigurer.defaultSuccessUrl("/verify", true);
                httpSecurityFormLoginConfigurer.failureUrl("/login?error=true");
                httpSecurityFormLoginConfigurer.permitAll();
            }
        }).oauth2Login(new Customizer<OAuth2LoginConfigurer<HttpSecurity>>() {
            @Override
            public void customize(OAuth2LoginConfigurer<HttpSecurity> httpSecurityOAuth2LoginConfigurer) {
                httpSecurityOAuth2LoginConfigurer.loginPage("/login");
                httpSecurityOAuth2LoginConfigurer.failureUrl("/login?error=true");
                httpSecurityOAuth2LoginConfigurer.defaultSuccessUrl("/account", true);
                httpSecurityOAuth2LoginConfigurer.userInfoEndpoint(new Customizer<OAuth2LoginConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.UserInfoEndpointConfig>() {
                    @Override
                    public void customize(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig userInfoEndpointConfig) {
                        userInfoEndpointConfig.userService(engineOAuth2Service);
                    }
                });
            }
        }).logout(new Customizer<LogoutConfigurer<HttpSecurity>>() {
            @Override
            public void customize(LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer) {
                httpSecurityLogoutConfigurer.logoutUrl("/logout");
                httpSecurityLogoutConfigurer.logoutSuccessUrl("/login");
                httpSecurityLogoutConfigurer.invalidateHttpSession(true);
                httpSecurityLogoutConfigurer.clearAuthentication(true);
                httpSecurityLogoutConfigurer.deleteCookies("JSESSIONID");
            }
        });

        return httpSecurity.build();
    }
}
