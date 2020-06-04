package com.example.AuthorizationServer.utility;

import com.example.AuthorizationServer.bo.dto.UserDTO;

import java.util.Comparator;

/**
 * @author Jonas Fredén-Lundvall (jonlundv@kth.se)
 *
 * Comparator used for sorting users by username.
 */
public enum UserEntityDTOComparator implements Comparator<UserDTO> {

    INSTANCE;

    @Override
    public int compare(UserDTO o1, UserDTO o2) {
        return o1.getUsername().compareTo(o2.getUsername());
    }

}