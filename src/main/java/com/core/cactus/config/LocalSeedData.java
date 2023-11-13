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

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Profile("local")
public class LocalSeedData implements CommandLineRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final ResourcePatternResolver resourceResolver;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
    }

    private void seedUsers() throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:/seed-data/users*.json");
        for (Resource resource : resources) {
            User user = unmarshal(resource, User.class);
            userRepository.save(user);
        }
    }

    private <T> T unmarshal(Resource resource, Class<T> clazz) throws IOException {
        return objectMapper.readValue(resource.getInputStream(), clazz);
    }
}
