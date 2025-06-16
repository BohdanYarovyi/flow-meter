package com.yarovyi.flowmeter.config;

import com.yarovyi.flowmeter.entity.account.Role;
import com.yarovyi.flowmeter.repository.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.sql.Date;
import java.time.LocalDate;

@Configuration
public class BeanConfiguration {

    @Bean
    public Role defaultRole(RoleRepository roleRepository) {
        return roleRepository
                .findRoleByName("USER")
                .orElseGet(() -> {
                    Role role = new Role(null, "USER");
                    return roleRepository.save(role);
                });
    }


    @Bean
    public SimpleMailMessage simpleMailMessage() {
        return new SimpleMailMessage();
    }

}
