package com.example.demo.registration;

import com.example.demo.config.SpringSecurityConfig;
import com.example.demo.dto.NewUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.InvalidLoginException;
import com.example.demo.exception.InvalidTokenException;
import com.example.demo.exception.OfficeNotFoundException;
import com.example.demo.exception.RoleNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.OfficeEntity;
import com.example.demo.model.RoleEntity;
import com.example.demo.model.UserEntity;
import com.example.demo.repository.OfficeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtAuthenticationResponse;
import com.example.demo.security.jwt.JwtService;
import com.example.demo.security.jwt.RefreshTokenRequest;
import com.example.demo.validation.UserValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OfficeRepository officeRepository;
    private final UserValidator userValidator;


    public AuthenticationService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                                 RoleRepository roleRepository, AuthenticationManager authenticationManager,
                                 JwtService jwtService, OfficeRepository officeRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.officeRepository = officeRepository;
        this.userValidator = userValidator;
    }

    public UserDto registerUser(NewUserDto newUserDto) {
        userValidator.isAvailableEmail(newUserDto.email());

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(newUserDto.email());
        userEntity.setFirstName(newUserDto.firstName());
        userEntity.setLastName(newUserDto.lastName());
        userEntity.setPhoneNumber(newUserDto.phoneNumber());

        String officeName = newUserDto.defaultOfficeName();
        OfficeEntity officeEntity = officeRepository.findByName(officeName)
                .orElseThrow(() -> new OfficeNotFoundException("Office not found"));

        userEntity.setDefaultOffice(officeEntity);
        userEntity.setPasswordHash(passwordEncoder.encode(newUserDto.password()));

        RoleEntity role = roleRepository.findByName(SpringSecurityConfig.USER)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        userEntity.addRole(role);

        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.mapUserEntityToDto(savedUser);
    }

    public JwtAuthenticationResponse login(LoginUser loginUser) {
        UserEntity user = getUserEntity(loginUser.email());
        if (!passwordEncoder.matches(loginUser.password(), user.getPasswordHash())) {
            throw new InvalidLoginException("User with given username and password doesn't exist");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.email(),
                        loginUser.password()));

        Map<String, Object> extraClaims = getExtraClaims(user);

        var jwt = jwtService.generateToken(extraClaims, loginUser.email());
        var refreshToken = jwtService.generateRefreshToken(loginUser.email());

        JwtAuthenticationResponse jwtAuthenticationResponse = getJwtAuthenticationResponse(jwt, refreshToken);

        return jwtAuthenticationResponse;
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String email = jwtService.getUserEmailFromToken(refreshTokenRequest.getToken());
        UserEntity user = getUserEntity(email);

        if (!jwtService.validateToken(refreshTokenRequest.getToken(), email)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        Map<String, Object> extraClaims = getExtraClaims(user);
        var jwt = jwtService.generateToken(extraClaims, email);

        JwtAuthenticationResponse jwtAuthenticationResponse = getJwtAuthenticationResponse(
                jwt, refreshTokenRequest.getToken());

        return jwtAuthenticationResponse;
    }

    private static JwtAuthenticationResponse getJwtAuthenticationResponse(String jwt, String refreshToken) {
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;
    }

    private UserEntity getUserEntity(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidLoginException("User with given username and password doesn't exist."));
    }

    private static Map<String, Object> getExtraClaims(UserEntity user) {
        Map<String, Object> extraClaims = new HashMap<>();
        Set<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
        extraClaims.put("userRoles", roleNames);
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("defaultOffice", user.getDefaultOffice().getName());
        return extraClaims;
    }

}
