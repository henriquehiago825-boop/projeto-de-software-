package br.com.instituicao.gestao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record InformacaoRequest(
        @NotBlank(message = "A categoria é obrigatória.") @Size(max = 60, message = "A categoria deve ter até 60 caracteres.") String categoria,
        @NotBlank(message = "O título é obrigatório.") @Size(max = 120, message = "O título deve ter até 120 caracteres.") String titulo,
        @NotBlank(message = "A descrição é obrigatória.") @Size(max = 500, message = "A descrição deve ter até 500 caracteres.") String descricao,
        @NotNull(message = "A data é obrigatória.") LocalDate dataReferencia
) {}
