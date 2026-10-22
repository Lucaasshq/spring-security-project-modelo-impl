package br.com.modelo.modelo_backend.config;


import br.com.modelo.modelo_backend.entity.enums.Papel;
import br.com.modelo.modelo_backend.security.JwtAuthenticationFilter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    public static String[] SWAGGER = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http.
                // Desabilita CSRF já que a API utiliza Tokens Stateless (JWT)
                        csrf(csrf -> csrf.disable())
                // Define o gerenciamento de sessão para ser totalmente STATELESS (sem estado de sessão no servidor)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define as autorizações de rotas
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticacao e rotas publicas
                        .requestMatchers(SWAGGER) .permitAll()
                        .requestMatchers("/api/auth/**", "/api/cardapio/**", "/api/produtos**", "/api/pedidos/publico/**").permitAll()
                        // endpoints de acesso restrito por papel
                        .requestMatchers("/api/cozinha/**").hasAnyAuthority(Papel.ROLE_COZINHA_RESTAURANTE.name(), Papel.ROLE_GERENTE_RESTAURANTE.name(), Papel.ROLE_DONO_RESTAURANTE.name())
                        .requestMatchers("/api/gerencia/**").hasAnyAuthority(Papel.ROLE_GERENTE_RESTAURANTE.name(), Papel.ROLE_DONO_RESTAURANTE.name())
                        .requestMatchers("/api/admin/**").hasAnyAuthority(Papel.ROLE_GLOBAL_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    /**
     * Define o algoritmo seguro de criptografia de senhas (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o AuthenticationManager necessário para o serviço de autenticação e login.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
}

