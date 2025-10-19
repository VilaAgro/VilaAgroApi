package com.vilaagro.api.service;

import com.vilaagro.api.dto.LoginRequestDTO;
import com.vilaagro.api.dto.UserCreateDTO;
import com.vilaagro.api.dto.UserResponseDTO;
import com.vilaagro.api.exception.EmailAlreadyExistsException;
import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.UserRepository;
import com.vilaagro.api.service.CustomUserDetailsService.CustomUserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Serviço responsável pela lógica de autenticação
 * Gerencia registro, login, logout e refresh de tokens
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    /**
     * Registra um novo usuário e realiza login automático
     */
    public UserResponseDTO registerUser(UserCreateDTO createDTO, HttpServletResponse response) {
        // Verifica se o email já existe
        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new EmailAlreadyExistsException(createDTO.getEmail());
        }

        // Armazena a senha original para login posterior
        String originalPassword = createDTO.getPassword();

        // Hash da senha antes de criar o usuário
        String hashedPassword = passwordEncoder.encode(createDTO.getPassword());
        createDTO.setPassword(hashedPassword);

        // Cria o usuário
        UserResponseDTO user = userService.createUser(createDTO);

        // Realiza login automático após registro usando a senha original
        authenticateAndSetCookies(createDTO.getEmail(), user.getId().toString(),
                                user.getType().name(), response);

        return user;
    }

    /**
     * Autentica um usuário existente
     */
    public UserResponseDTO authenticateUser(LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            // Autentica usando o AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Obtém o usuário autenticado
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            // Gera tokens e define cookies
            authenticateAndSetCookies(user.getEmail(), user.getId().toString(),
                                    user.getType().name(), response);

            // Converte para DTO de resposta
            return userService.convertToResponseDTO(user);

        } catch (AuthenticationException e) {
            log.error("Falha na autenticação para email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Credenciais inválidas");
        }
    }

    /**
     * Realiza logout limpando os cookies
     */
    public void logoutUser(HttpServletResponse response) {
        // Remove o cookie do access token
        Cookie accessTokenCookie = createCookie("accessToken", "", 0, true, true);
        response.addCookie(accessTokenCookie);

        // Remove o cookie do refresh token
        Cookie refreshTokenCookie = createCookie("refreshToken", "", 0, true, true);
        response.addCookie(refreshTokenCookie);

        log.info("Logout realizado com sucesso");
    }

    /**
     * Renova o access token usando o refresh token
     */
    public void refreshAccessToken(HttpServletResponse response) {
        // Obtém o refresh token do cookie
        String refreshToken = extractTokenFromRequest("refreshToken");

        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token não encontrado");
        }

        try {
            // Valida se é um refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Token fornecido não é um refresh token");
            }

            // Extrai o email do refresh token
            String userEmail = jwtService.extractUsername(refreshToken);

            // Carrega os detalhes do usuário
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) userDetailsService.loadUserByUsername(userEmail);

            // Valida o refresh token
            if (!jwtService.isTokenValid(refreshToken, userPrincipal)) {
                throw new BadCredentialsException("Refresh token inválido");
            }

            // Gera novo access token
            String newAccessToken = jwtService.generateAccessToken(
                    userPrincipal,
                    userPrincipal.getUserId(),
                    userPrincipal.getUserType()
            );

            // Define o novo access token no cookie
            Cookie accessTokenCookie = createCookie("accessToken", newAccessToken,
                                                  (int) (accessTokenExpiration / 1000), true, true);
            response.addCookie(accessTokenCookie);

            log.info("Access token renovado com sucesso para usuário: {}", userEmail);

        } catch (Exception e) {
            log.error("Erro ao renovar access token: {}", e.getMessage());
            throw new BadCredentialsException("Erro ao renovar token");
        }
    }

    /**
     * Obtém o usuário atualmente autenticado
     */
    public UserResponseDTO getCurrentUser() {
        try {
            // Obtém a autenticação atual do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BadCredentialsException("Usuário não autenticado");
            }

            // Obtém o principal (usuário) da autenticação
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            return userService.convertToResponseDTO(user);

        } catch (Exception e) {
            log.error("Erro ao buscar usuário atual: {}", e.getMessage());
            throw new BadCredentialsException("Erro ao buscar usuário atual");
        }
    }

    /**
     * Método auxiliar para autenticar e definir cookies
     */
    private void authenticateAndSetCookies(String email, String userId, String userType, HttpServletResponse response) {
        try {
            // Carrega os detalhes do usuário para gerar os tokens
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) userDetailsService.loadUserByUsername(email);

            // Gera access token
            String accessToken = jwtService.generateAccessToken(userPrincipal, userId, userType);

            // Gera refresh token
            String refreshToken = jwtService.generateRefreshToken(userPrincipal);

            // Cria cookies seguros HttpOnly (Secure=false para desenvolvimento local)
            Cookie accessTokenCookie = createCookie("accessToken", accessToken,
                                                  (int) (accessTokenExpiration / 1000), true, false);
            Cookie refreshTokenCookie = createCookie("refreshToken", refreshToken,
                                                   (int) (refreshTokenExpiration / 1000), true, false);

            // Adiciona cookies na resposta
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            log.info("Tokens gerados e cookies definidos com sucesso para usuário: {}", email);

        } catch (Exception e) {
            log.error("Erro ao gerar tokens: {}", e.getMessage());
            throw new RuntimeException("Erro interno durante autenticação");
        }
    }

    /**
     * Cria um cookie seguro
     */
    private Cookie createCookie(String name, String value, int maxAge, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure); // false em desenvolvimento (HTTP), true em produção (HTTPS)
        cookie.setPath("/");
        // SameSite=Lax permite cookies em requisições cross-site GET (melhor para desenvolvimento)
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    /**
     * Extrai um token específico dos cookies da requisição atual
     */
    private String extractTokenFromRequest(String cookieName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
