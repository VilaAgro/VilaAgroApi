package com.vilaagro.api.service;

import com.vilaagro.api.model.User;
import com.vilaagro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementação personalizada do UserDetailsService do Spring Security
 * Carrega os detalhes do usuário a partir do banco de dados
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carrega um usuário pelo email (username no contexto do Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        return new CustomUserPrincipal(user);
    }

    /**
     * Classe interna que implementa UserDetails para o usuário customizado
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Converte o UserType para uma authority do Spring Security
            return Collections.singletonList(
                new SimpleGrantedAuthority(user.getType().name())
            );
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            // Considera habilitado se não está com documentos rejeitados
            return user.getDocumentsStatus() != com.vilaagro.api.model.AccountStatus.DISAPPROVED;
        }

        /**
         * Método auxiliar para obter o usuário original
         */
        public User getUser() {
            return user;
        }

        /**
         * Método auxiliar para obter o ID do usuário
         */
        public String getUserId() {
            return user.getId().toString();
        }

        /**
         * Método auxiliar para obter o tipo do usuário
         */
        public String getUserType() {
            return user.getType().name();
        }
    }
}
