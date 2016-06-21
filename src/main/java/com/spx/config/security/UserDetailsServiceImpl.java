package com.spx.config.security;

import com.spx.dao.UserDao;
import com.spx.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Created by timofb on 21-Jun-16.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userDao.getUserByLogin(username).get(0);
        if(user == null) {
            new UsernameNotFoundException(String.format("Username %s not found", username));
        }
        String authority = "ADMIN";
        return new UserDetailsImpl(user, Collections.singleton(new SimpleGrantedAuthority(authority)));
    }
}

