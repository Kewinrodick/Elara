package com.store.elara.security;

import com.store.elara.entities.User;
import com.store.elara.entities.UserPrinciple;
import com.store.elara.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException{
        User user = userRepository.findUserByName(name);
        if(user == null){
            throw new UsernameNotFoundException(name);
        }

        return new UserPrinciple(user);
    }
}
