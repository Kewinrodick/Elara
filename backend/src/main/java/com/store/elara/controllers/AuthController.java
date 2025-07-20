package com.store.elara.controllers;

import com.store.elara.dtos.AuthRequest;
import com.store.elara.dtos.CommonResponse;
import com.store.elara.services.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody AuthRequest authRequest) {
        CommonResponse commonResponse = userService.signin(authRequest);
        return ResponseEntity.status(commonResponse.getCode()).body(commonResponse);
    }
}
