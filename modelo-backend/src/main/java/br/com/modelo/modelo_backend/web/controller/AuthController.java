/*
 * Copyright (c) 2026 ChegaAI. Todos os direitos reservados.
 * Este software é proprietário da ChegaAI.
 * A cópia, distribuição ou uso não autorizado é estritamente proibido.
 */

package br.com.modelo.modelo_backend.web.controller;

import br.com.modelo.modelo_backend.entity.Usuario;
import br.com.modelo.modelo_backend.repository.UsuarioRepository;
import br.com.modelo.modelo_backend.security.JwtService;
import br.com.modelo.modelo_backend.web.controller.dto.LoginRequestDTO;
import br.com.modelo.modelo_backend.web.controller.dto.RegisterRequestDTO;
import br.com.modelo.modelo_backend.web.controller.dto.TokenResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;




    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(loginRequestDTO.email()).orElseThrow(
                () -> new UsernameNotFoundException("Usuário de email: " + loginRequestDTO.email()+ " não encontrado")
        );

        String token = jwtService.generateToken(usuario);

        return new TokenResponseDTO(token, "Bearer", usuario.getUsername(), usuario.getPapel().name());
    }


    @PostMapping("/register")
    public ResponseEntity<Void> RegistroCliente(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {

        if (usuarioRepository.existsByEmail(registerRequestDTO.email())) {
            throw new RuntimeException("O e-mail já está em uso");
        }

        if (!registerRequestDTO.senha().equals(registerRequestDTO.confirmaSenha())){
            throw new RuntimeException("As senhas não são iguais");
        }

        String senhaCriptografada = passwordEncoder.encode(registerRequestDTO.senha());

        Usuario usuario = registerRequestDTO.toEntity(senhaCriptografada);
        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
