package com.core.cactus.user.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/")
public class OAuth2Controller {

    @GetMapping("/secured")
    public ResponseEntity<String> secured() {
        return ResponseEntity.ok("This is secured");
    }
}
