package br.com.instituicao.gestao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage() == null
                        ? "Dados inválidos."
                        : e.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" "));

        if (mensagem.isBlank()) {
            mensagem = "Dados inválidos.";
        }

        return ResponseEntity.badRequest().body(Map.of("mensagem", mensagem));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException ex) {
        String mensagem = ex.getStatusCode().value() == 404
                ? "Registro não encontrado."
                : "Não foi possível concluir a operação.";

        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("mensagem", mensagem));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> erro(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensagem",
                        "Não foi possível concluir a operação. Tente novamente."));
    }
}