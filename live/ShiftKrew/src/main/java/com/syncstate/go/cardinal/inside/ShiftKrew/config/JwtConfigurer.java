package com.syncstate.go.cardinal.inside.ShiftKrew.config;


import com.syncstate.go.cardinal.inside.ShiftKrew.filters.JwtTokenFilter;
import com.syncstate.go.cardinal.inside.ShiftKrew.providers.TokenProvider;
import com.syncstate.go.cardinal.inside.ShiftKrew.services.UserService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private TokenProvider jwtTokenProvider;
    private UserService userService;
    public JwtConfigurer(TokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }
    @Override
    public void configure(HttpSecurity http) {
        JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenProvider, userService);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
