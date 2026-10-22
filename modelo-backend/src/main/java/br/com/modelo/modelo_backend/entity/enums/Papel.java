package br.com.modelo.modelo_backend.entity.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Papel implements GrantedAuthority {
    ROLE_GLOBAL_ADMIN("Admin Global"),
    ROLE_DONO_RESTAURANTE("Dono do Restaurante"),
    ROLE_GERENTE_RESTAURANTE("Gerente"),
    ROLE_COZINHA_RESTAURANTE("Cozinha / KDS"),
    ROLE_FUNCIONARIO_RESTAURANTE("Funcionario Operacional"),
    ROLE_CLIENTE("Cliente");

    private final String descricao;

    Papel(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getAuthority() {
        return this.name(); // retorna o nome da constante do enum como autoridade
    }
}
