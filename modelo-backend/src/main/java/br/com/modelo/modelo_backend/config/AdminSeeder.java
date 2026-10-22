/*
 * Copyright (c) 2026 ChegaAI. Todos os direitos reservados.
 * Este software é proprietário da ChegaAI.
 * A cópia, distribuição ou uso não autorizado é estritamente proibido.
 */

package br.com.modelo.modelo_backend.config;


import br.com.chegaai.chegaai_backend.entity.enums.Papel;
import br.com.modelo.modelo_backend.entity.Usuario;
import br.com.modelo.modelo_backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner criarAdminPadrao(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se já existe algum ADMIN global cadastrado
            boolean existeAdmin = usuarioRepository.findAll().stream()
                    .anyMatch(u -> u.getPapel() == Papel.ROLE_GLOBAL_ADMIN);

            if (!existeAdmin) {
                Usuario adminMaster = new Usuario();
                adminMaster.setNomeCompleto("Admin Global");
                adminMaster.setEmail("admin@chegaai.com");
                // Senha padrão segura gerada pelo encoder
                adminMaster.setSenha(passwordEncoder.encode("Admin@123456"));
                adminMaster.setPapel(Papel.ROLE_GLOBAL_ADMIN);

                usuarioRepository.save(adminMaster);
                System.out.println(">> [SECURITY] Usuário ADMIN Global padrão criado com sucesso: admin@chegaai.com / Admin@123456");
            }
        };
    }
}
