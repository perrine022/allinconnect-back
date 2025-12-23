package com.allinconnect.allinconnectback2.dto;


public class AuthResponse {
    private String token;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String email;

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, email);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
