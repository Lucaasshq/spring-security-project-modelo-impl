package br.com.modelo.modelo_backend.entity.abstracts;/*
 * Copyright (c) 2026 ChegaAI. Todos os direitos reservados.
 * Este software é proprietário da ChegaAI.
 * A cópia, distribuição ou uso não autorizado é estritamente proibido.
 */


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;


@MappedSuperclass // indica que a tabela é herdada e não é uma entidade
/**
 * Classe base para todas as entidades do sistema.
 *
 * @SQLRestriction("data_exclusao IS NULL") garante que todas as consultas (SELECT)
 * ignorem automaticamente os registros marcados como excluídos logicamente (soft delete),
 * mantendo o histórico intacto no banco de dados sem os exibir nas listagens comuns.
 */
@SQLRestriction("data_exclusao IS NULL")
@Getter
@Setter
public abstract class IdModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_criacao")
    @NotNull
    private LocalDateTime dataCriacao;

    @Column(name = "ativo")
    @NotNull
    private boolean ativo = true;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;

    @PrePersist
    public void prePersist(){
        this.dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IdModel idModel = (IdModel) o;
        return Objects.equals(id, idModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
