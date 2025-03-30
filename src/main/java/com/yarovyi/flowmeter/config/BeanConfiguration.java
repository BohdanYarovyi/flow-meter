package com.yarovyi.flowmeter.config;

import com.yarovyi.flowmeter.domain.account.Role;
import com.yarovyi.flowmeter.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class BeanConfiguration {

    @Bean
    public Role defaultRole(RoleRepository roleRepository) {
        return roleRepository.findRoleByName("USER").orElseGet(() -> {
            Role role = new Role(null, "USER");
            return roleRepository.save(role);
        });
    }

}
