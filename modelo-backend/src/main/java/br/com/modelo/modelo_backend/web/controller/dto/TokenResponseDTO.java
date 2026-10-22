package br.com.modelo.modelo_backend.web.controller.dto;

public record TokenResponseDTO(
        String token,
        String tipo,
        String email,
        String papel
) {
}
