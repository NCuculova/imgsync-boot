package com.ncuculova.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * Created by ncuculova on 9.11.15.
 */
@Configuration
public class OAuthConfiguration {

    @EnableAuthorizationServer
    static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        AuthenticationManager authenticationManager;


        @Autowired
        UserDetailsService userDetailsService;

        @Bean
        public AuthorizationCodeServices getCodeServices() {
            return new InMemoryAuthorizationCodeServices();
        }

        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
        }

        /**
         * * Configure the security of the Authorization Server, meaning the /oauth/token endpoint.
         * endpoints /oauth/token_key and /oauth/check_token
         * Client credentials are required to access the endpoints
         * defines the security constraints on the token endpoint.
         **/
        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            super.configure(oauthServer);
        }

        /**
         * Configure the {ClientDetailsService}, e.g. declaring individual clients and their properties. Note that
         * password grant is not enabled (even if some clients are allowed it) unless an {@link AuthenticationManager} is
         * supplied to the { #configure(AuthorizationServerEndpointsConfigurer)}. At least one client, or a fully
         * formed custom {ClientDetailsService} must be declared or the server will not start.
         **/
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            super.configure(clients);
            clients.inMemory()
                    // Confidential client where client secret can be kept safe (e.g. server side)
                    .withClient("img_sync").secret("img_sync_secret")
                    .authorities("USER", "ADMIN")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                    .scopes("read").accessTokenValiditySeconds(1800);
        }

        /**
         * Configure the non-security features of the Authorization Server endpoints, like token store, token
         * customizations, user approvals and grant types. You shouldn't need to do anything by default,
         * unless you need password grants, in which case you need to provide an {@link AuthenticationManager}.
         **/
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            super.configure(endpoints);
            endpoints.authenticationManager(authenticationManager)
                    .tokenStore(tokenStore());
        }
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/api/login", "/api/sign_up", "/favicon.ico").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/login?authorization_error=true")
                    .and()
                    .csrf()
                    .disable()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?authentication_error=true");
        }

    }
}
