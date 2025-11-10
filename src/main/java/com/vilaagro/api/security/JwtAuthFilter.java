package com.vilaagro.api.security;

import com.vilaagro.api.service.CustomUserDetailsService;
import com.vilaagro.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta todas as requisições HTTP para validar tokens de acesso
 * Procura por tokens em cookies HttpOnly seguros e valida sua autenticidade
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Pula a validação para endpoints públicos
        if (isPublicEndpoint(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrai o token do cookie
            final String jwt = extractTokenFromCookie(request, "accessToken");

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extrai o email do token
            final String userEmail = jwtService.extractUsername(jwt);

            // Se o token contém um email válido e não há autenticação no contexto atual
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Verifica se é um Access Token
                if (!jwtService.isAccessToken(jwt)) {
                    log.warn("Token fornecido não é um Access Token válido");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Carrega os detalhes do usuário
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Valida o token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Cria o token de autenticação do Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Adiciona detalhes da requisição
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Usuário autenticado com sucesso: {}", userEmail);
                } else {
                    log.warn("Token JWT inválido para o usuário: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Erro durante a autenticação JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do cookie especificado
     */
    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Verifica se o endpoint é público (não requer autenticação)
     */
    private boolean isPublicEndpoint(String path) {
        // Endpoints que requerem autenticação mesmo estando em /api/auth/
        if (path.equals("/api/auth/profile") || 
            path.equals("/api/auth/password") ||
            path.equals("/api/auth/me")) {
            return false;
        }
        
        return path.startsWith("/api/auth/") ||
               path.equals("/error") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }
}
