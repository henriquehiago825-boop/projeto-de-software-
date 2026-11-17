package br.com.instituicao.gestao.service;

import br.com.instituicao.gestao.dto.InformacaoRequest;
import br.com.instituicao.gestao.model.Informacao;
import br.com.instituicao.gestao.repository.InformacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class InformacaoService {
    private final InformacaoRepository repository;

    public InformacaoService(InformacaoRepository repository) { this.repository = repository; }

    public Informacao cadastrar(InformacaoRequest request) {
        Informacao item = new Informacao();
        aplicarDados(item, request);
        return repository.save(item);
    }

    public Informacao atualizar(Long id, InformacaoRequest request) {
        Informacao item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado."));
        aplicarDados(item, request);
        return repository.save(item);
    }

    private void aplicarDados(Informacao item, InformacaoRequest request) {
        item.setCategoria(request.categoria().trim());
        item.setTitulo(request.titulo().trim());
        item.setDescricao(request.descricao().trim());
        item.setDataReferencia(request.dataReferencia());
    }

    public List<Informacao> consultar(String termo, String categoria) {
        String busca = termo == null ? "" : termo.trim();
        String filtro = categoria == null ? "" : categoria.trim();
        boolean temCategoria = !filtro.isBlank();
        if (temCategoria && !busca.isBlank()) return repository.findByTituloContainingIgnoreCaseAndCategoriaIgnoreCaseOrderByDataReferenciaDesc(busca, filtro);
        if (temCategoria) return repository.findByCategoriaIgnoreCaseOrderByDataReferenciaDesc(filtro);
        if (!busca.isBlank()) return repository.findByTituloContainingIgnoreCaseOrderByDataReferenciaDesc(busca);
        return repository.findAllByOrderByDataReferenciaDesc();
    }
}
