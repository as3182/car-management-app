package com.spyneai.carmanagement.service;

import com.spyneai.carmanagement.config.CustomUserDetailService;
import com.spyneai.carmanagement.dto.JwtResponseDTO;
import com.spyneai.carmanagement.dto.UserDTO;
import com.spyneai.carmanagement.dto.UserLoginDTO;
import com.spyneai.carmanagement.dto.UserRegisterDTO;
import com.spyneai.carmanagement.entity.User;
import com.spyneai.carmanagement.exceptions.InvalidCredentialsException;
import com.spyneai.carmanagement.exceptions.UserAlreadyExistsException;
import com.spyneai.carmanagement.repository.UserRepository;
import com.spyneai.carmanagement.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {

        if (userRepository.existsByUsername(userRegisterDTO.getUsername())) {
            throw new UserAlreadyExistsException("User with username '" + userRegisterDTO.getUsername() + "' already exists.");
        }

        User newUser  = new User();
        newUser.setName(userRegisterDTO.getName());
        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        newUser.setUsername(userRegisterDTO.getUsername());
        userRepository.save(newUser);
    }

    @Override
    public JwtResponseDTO authenticateUser(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElseThrow(() -> new RuntimeException("username does not exist"));

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        UserDetails userDetails = customUserDetailService.loadUserByUsername(userLoginDTO.getUsername());
        String token = jwtHelper.generateToken(userDetails);

        return JwtResponseDTO.builder()
                .jwtToken(token)
                .build();

    }

    @Override
    public UserDTO userDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("Error"));
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}
