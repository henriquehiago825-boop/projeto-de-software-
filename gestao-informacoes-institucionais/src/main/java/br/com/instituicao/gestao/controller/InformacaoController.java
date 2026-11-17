package br.com.instituicao.gestao.controller;

import br.com.instituicao.gestao.dto.InformacaoRequest;
import br.com.instituicao.gestao.model.Informacao;
import br.com.instituicao.gestao.service.InformacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/informacoes")
public class InformacaoController {
    private final InformacaoService service;
    public InformacaoController(InformacaoService service) { this.service = service; }

    @GetMapping
    public List<Informacao> listar(@RequestParam(required = false) String busca,
                                   @RequestParam(required = false) String categoria) {
        return service.consultar(busca, categoria);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Informacao cadastrar(@Valid @RequestBody InformacaoRequest request) {
        return service.cadastrar(request);
    }

    @PutMapping("/{id}")
    public Informacao atualizar(@PathVariable Long id, @Valid @RequestBody InformacaoRequest request) {
        return service.atualizar(id, request);
    }
}
