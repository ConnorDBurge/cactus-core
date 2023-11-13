package com.core.cactus.security;

import com.core.cactus.user.User;
import com.core.cactus.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            User newUser = User.builder()
                    .email(email)
                    .username(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            userRepository.save(newUser);
        }

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String targetUrl = savedRequest != null ? savedRequest.getRedirectUrl() : "/";
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
