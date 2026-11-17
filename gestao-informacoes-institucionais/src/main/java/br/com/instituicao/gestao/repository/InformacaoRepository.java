package br.com.instituicao.gestao.repository;

import br.com.instituicao.gestao.model.Informacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InformacaoRepository extends JpaRepository<Informacao, Long> {
    List<Informacao> findByTituloContainingIgnoreCaseAndCategoriaIgnoreCaseOrderByDataReferenciaDesc(String titulo, String categoria);
    List<Informacao> findByTituloContainingIgnoreCaseOrderByDataReferenciaDesc(String titulo);
    List<Informacao> findByCategoriaIgnoreCaseOrderByDataReferenciaDesc(String categoria);
    List<Informacao> findAllByOrderByDataReferenciaDesc();
}
