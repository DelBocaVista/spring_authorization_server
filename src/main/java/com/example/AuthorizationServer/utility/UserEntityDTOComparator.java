package com.example.AuthorizationServer.utility;

import com.example.AuthorizationServer.bo.dto.UserEntityDTO;

import java.util.Comparator;

public enum UserEntityDTOComparator implements Comparator<UserEntityDTO> {

    INSTANCE;

    @Override
    public int compare(UserEntityDTO o1, UserEntityDTO o2) {
        return o1.getUsername().compareTo(o2.getUsername());
    }

}