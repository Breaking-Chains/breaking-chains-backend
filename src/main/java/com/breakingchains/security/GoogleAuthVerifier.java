package com.breakingchains.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleAuthVerifier {

    @Value("${google.client-id:}")
    private String clientId;

    @Getter
    public static class GoogleUserPayload {
        private final String googleId;
        private final String email;
        private final String name;
        private final String pictureUrl;

        public GoogleUserPayload(String googleId, String email, String name, String pictureUrl) {
            this.googleId = googleId;
            this.email = email;
            this.name = name;
            this.pictureUrl = pictureUrl;
        }
    }

    public GoogleUserPayload verify(String idTokenString) throws Exception {
        GoogleIdTokenVerifier.Builder builder = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        );

        if (clientId != null && !clientId.isBlank() && !"your-google-client-id".equals(clientId)) {
            builder.setAudience(Collections.singletonList(clientId));
        }

        GoogleIdTokenVerifier verifier = builder.build();
        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            return new GoogleUserPayload(userId, email, name, pictureUrl);
        }

        return null;
    }
}
