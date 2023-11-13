package com.core.cactus.config;

import com.core.cactus.user.User;
import com.core.cactus.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile("local")
public class LocalSeedData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourcePatternResolver resourceResolver;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Resource[] resources = resourceResolver.getResources("classpath:seed-data/user*.json");
        for (Resource resource : resources) {
            User user = objectMapper.readValue(resource.getInputStream(), User.class);
            userRepository.save(user);
        }
    }
}
