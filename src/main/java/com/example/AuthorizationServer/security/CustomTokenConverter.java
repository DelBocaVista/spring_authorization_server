package com.example.AuthorizationServer.security;

import com.example.AuthorizationServer.bo.dto.OrganizationDTO;
import com.example.AuthorizationServer.security.CustomUserDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.*;

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

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication authentication = super.extractAuthentication(map);

        ArrayList<LinkedHashMap<String, String>> organizations = (ArrayList<LinkedHashMap<String, String>>) map.get("orgs");
        List<OrganizationDTO> orgs = new ArrayList<>();
        for (LinkedHashMap m: organizations) {
            Long id = Long.valueOf((Integer) m.get("id"));
            String name = (String) m.get("name");
            String path = (String) m.get("path");
            Boolean enabled = (Boolean) m.get("enabled");
            orgs.add(new OrganizationDTO(id, name, path, enabled));
        }

        String name = (String) map.get("user_name");
        ArrayList<String> authorities = (ArrayList<String>) map.get("authorities");
        List<GrantedAuthority> auths = new ArrayList<>();
        for (String s: authorities) {
            auths.add(new SimpleGrantedAuthority(s));
        }

        CustomUserDetails user = new CustomUserDetails(name, "", auths, orgs);
        authentication.setDetails(user);

        return authentication;
    }


}
