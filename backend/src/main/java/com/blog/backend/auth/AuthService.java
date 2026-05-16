package com.blog.backend.auth;

import com.blog.backend.auth.dto.AuthResponse;
import com.blog.backend.auth.dto.CurrentUserVO;
import com.blog.backend.auth.dto.LoginRequest;
import com.blog.backend.auth.dto.RefreshRequest;
import com.blog.backend.auth.dto.RegisterRequest;
import com.blog.backend.common.exception.BusinessException;
import com.blog.backend.domain.entity.User;
import com.blog.backend.domain.entity.UserProfile;
import com.blog.backend.domain.enums.UserRole;
import com.blog.backend.repository.UserProfileRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.AppUserPrincipal;
import com.blog.backend.security.JwtService;
import com.blog.backend.security.SecurityUtils;
import com.blog.backend.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setDisplayName(request.displayName());
        profileRepository.save(profile);
        user.setProfile(profile);
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (!user.isEnabled()) {
            throw new BusinessException(403, "账号已被禁用");
        }
        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (!refreshTokenStore.contains(request.refreshToken())) {
            throw new BusinessException(401, "刷新令牌已失效");
        }
        Long userId = jwtService.extractUserId(request.refreshToken());
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (!user.isEnabled()) {
            refreshTokenStore.delete(request.refreshToken());
            throw new BusinessException(403, "账号已被禁用");
        }
        refreshTokenStore.delete(request.refreshToken());
        return issueTokens(user);
    }

    public void logout(String refreshToken) {
        refreshTokenStore.delete(refreshToken);
    }

    public CurrentUserVO me() {
        AppUserPrincipal principal = SecurityUtils.requireCurrentUser();
        return new CurrentUserVO(principal.getId(), principal.getUsername(), principal.getDisplayName(), principal.getRole());
    }

    private AuthResponse issueTokens(User user) {
        AppUserPrincipal principal = new AppUserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);
        refreshTokenStore.save(refreshToken, user.getId(), 7L * 24 * 3600);
        return new AuthResponse(accessToken, refreshToken, new CurrentUserVO(
                principal.getId(),
                principal.getUsername(),
                principal.getDisplayName(),
                principal.getRole()
        ));
    }
}
