package com.knowYourfan.backEnd.Controllers;

import com.knowYourfan.backEnd.Entities.User;
import com.knowYourfan.backEnd.Repositories.UserRepository;
import com.knowYourfan.backEnd.Security.JwtService;
import com.knowYourfan.backEnd.Services.TwitterAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/twitter")
public class TwitterAccountController {
    private final TwitterAccountService twitterAccountService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> getTwitterAuthUrl(@RequestParam Long userId) {
        URI twitterOAuthUrl = twitterAccountService.getOAuthRedirectURL(userId);
        return ResponseEntity.ok(Map.of("url", twitterOAuthUrl.toString()));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<?> twitterCallback(@RequestParam String oauth_token,
                                             @RequestParam String oauth_verifier) {
        try {
            Long userId = twitterAccountService.fetchAndSaveTwitterAccount(oauth_token, oauth_verifier);
            User u = userRepository.findById(userId).orElseThrow();
            String jwt = jwtService.generateToken(u);

            URI redirect = URI.create(System.getenv("FRONTEND_URL") + "?tab=socials&jwt=" + jwt);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(redirect);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception ex) {
            URI redirect = URI.create(System.getenv("FRONTEND_URL") + "?error=" + ex.getMessage());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(redirect);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }
}
