package com.spyneai.carmanagement.service;

import com.spyneai.carmanagement.dto.JwtResponseDTO;
import com.spyneai.carmanagement.dto.UserDTO;
import com.spyneai.carmanagement.dto.UserLoginDTO;
import com.spyneai.carmanagement.dto.UserRegisterDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    public void registerUser(UserRegisterDTO userRegisterDTO);

    public JwtResponseDTO authenticateUser(@Valid UserLoginDTO userLoginDTO);

    public UserDTO userDetails(Long userId);
}
