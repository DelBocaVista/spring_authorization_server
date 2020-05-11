package com.example.AuthorizationServer.utility;

import com.example.AuthorizationServer.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 *
 */
public class UserDetailExtractor {

    /**
     * Extract custom user details from security context
     * @param context the security context
     * @return the custom user details
     */
    public static CustomUserDetails extract(SecurityContext context) {
        OAuth2AuthenticationDetails authentication = (OAuth2AuthenticationDetails) context.getAuthentication().getDetails();
        return (CustomUserDetails) authentication.getDecodedDetails();
    }
}
