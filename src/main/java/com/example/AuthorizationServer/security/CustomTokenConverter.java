package com.example.AuthorizationServer.security;

import com.example.AuthorizationServer.security.CustomUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonas Lundvall (jonlundv@kth.se)
 *
 * Custom helper which acts as a TokenEnhancer for when tokens are granted.
 */
public class CustomTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("orgs", user.getOrganizations());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        accessToken = super.enhance(accessToken, authentication);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new HashMap<>());
        return accessToken;
    }
}
