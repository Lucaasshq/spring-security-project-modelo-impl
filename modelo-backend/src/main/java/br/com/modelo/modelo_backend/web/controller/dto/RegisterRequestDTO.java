package br.com.modelo.modelo_backend.web.controller.dto;


import br.com.modelo.modelo_backend.entity.Usuario;
import br.com.modelo.modelo_backend.entity.enums.Papel;
import jakarta.validation.constraints.*;

public record RegisterRequestDTO(

        @NotNull(message = "O nome completo é obrigatório")
        @Size(min = 3, max = 100, message = "O nome completo deve ter entre 3 e 100 caracteres")
        String nomeCompleto,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_]).*$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@#$%^&+=!_)"
        )
        String senha,

        @NotNull(message = "A confirmação de senha é obrigatória")
        String confirmaSenha

) {
    public Usuario toEntity(String senhaCriptografada ) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(this.nomeCompleto);
        usuario.setEmail(this.email);
        usuario.setSenha(senhaCriptografada);
        usuario.setPapel(Papel.ROLE_CLIENTE);
        return usuario;
    }
}