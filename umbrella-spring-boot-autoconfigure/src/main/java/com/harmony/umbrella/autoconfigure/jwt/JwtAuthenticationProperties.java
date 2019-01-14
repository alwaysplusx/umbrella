package com.harmony.umbrella.autoconfigure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author wuxii
 */
@ConfigurationProperties(prefix = "harmony.jwt")
public class JwtAuthenticationProperties {

    private String signature;
    private Duration expiresIn = Duration.ofSeconds(7200);
    private boolean strictMode;
    private String issuer;

    private Form form;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Duration getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Duration expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public static class Form {

        private String usernameParameter;
        private String passwordParameter;
        private String loginPage;

        private String loginProcessingUrl;
        private String failureForwardUrl;
        private String successForwardUrl;

        private String responseType;

        private String responseTokenName = "token";

        private String passwordEncoderType = "bcrypt";

        public String getUsernameParameter() {
            return usernameParameter;
        }

        public void setUsernameParameter(String usernameParameter) {
            this.usernameParameter = usernameParameter;
        }

        public String getPasswordParameter() {
            return passwordParameter;
        }

        public void setPasswordParameter(String passwordParameter) {
            this.passwordParameter = passwordParameter;
        }

        public String getLoginPage() {
            return loginPage;
        }

        public void setLoginPage(String loginPage) {
            this.loginPage = loginPage;
        }

        public String getLoginProcessingUrl() {
            return loginProcessingUrl;
        }

        public void setLoginProcessingUrl(String loginProcessingUrl) {
            this.loginProcessingUrl = loginProcessingUrl;
        }

        public String getFailureForwardUrl() {
            return failureForwardUrl;
        }

        public void setFailureForwardUrl(String failureForwardUrl) {
            this.failureForwardUrl = failureForwardUrl;
        }

        public String getSuccessForwardUrl() {
            return successForwardUrl;
        }

        public void setSuccessForwardUrl(String successForwardUrl) {
            this.successForwardUrl = successForwardUrl;
        }

        public String getResponseType() {
            return responseType;
        }

        public void setResponseType(String responseType) {
            this.responseType = responseType;
        }

        public String getResponseTokenName() {
            return responseTokenName;
        }

        public void setResponseTokenName(String responseTokenName) {
            this.responseTokenName = responseTokenName;
        }

        public String getPasswordEncoderType() {
            return passwordEncoderType;
        }

        public void setPasswordEncoderType(String passwordEncoderType) {
            this.passwordEncoderType = passwordEncoderType;
        }

    }

}
