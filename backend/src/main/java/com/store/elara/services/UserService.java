package com.store.elara.services;

import com.store.elara.dtos.*;
import com.store.elara.entities.User;
import com.store.elara.mappers.UserMapper;
import com.store.elara.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepositories;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;


    public UserDto add(RegisterUserRequest request) {

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        userRepositories.save(user);

        UserDto userDto = userMapper.toDto(user);

        return userDto;

    }

    public UserDto update(UpdateUserRequest updateUserRequest, Long id) {
        var user = userRepositories.findById(id).orElse(null);
        userMapper.update(updateUserRequest, user);
        userRepositories.save(user);

        UserDto userDto =  userMapper.toDto(user);

        return userDto;
    }

    public String changePassword(ChangePasswordRequest request, Long id) {
        var user = userRepositories.findById(id).orElse(null);

        // How to make spring security work as to check the relevent
        // log in credentials when its upto changing that user's password

        if(user != null){
            if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepositories.save(user);
                    return "success";
            }
            else{
                return "Wrong password";
            }
        }
        return null;

    }

    public String login(RegisterUserRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(request.getName());
        }
        return "Fail";
    }
}
