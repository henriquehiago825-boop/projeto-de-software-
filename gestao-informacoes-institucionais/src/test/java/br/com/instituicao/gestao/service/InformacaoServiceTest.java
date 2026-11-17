import br.com.instituicao.gestao.service.InformacaoService;
import br.com.instituicao.gestao.dto.InformacaoRequest;
import br.com.instituicao.gestao.model.Informacao;
import br.com.instituicao.gestao.repository.InformacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InformacaoServiceTest {

    @Mock
    InformacaoRepository repository;

    @InjectMocks
    InformacaoService service;

    @Test
    void deveCadastrarInformacaoRemovendoEspacosExcedentes() {
        InformacaoRequest request = new InformacaoRequest(
                " Comunicado ", " Reunião ", " Aviso geral ",
                LocalDate.of(2026, 11, 16));

        when(repository.save(any(Informacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Informacao salvo = service.cadastrar(request);

        assertEquals("Comunicado", salvo.getCategoria());
        assertEquals("Reunião", salvo.getTitulo());
        assertEquals("Aviso geral", salvo.getDescricao());
        verify(repository).save(any(Informacao.class));
    }

    @Test
    void deveAtualizarInformacaoExistente() {
        Long id = 1L;
        Informacao existente = new Informacao();

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Informacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InformacaoRequest request = new InformacaoRequest(
                " Evento ", " Palestra ", " Nova descrição ",
                LocalDate.of(2026, 11, 17));

        Informacao atualizado = service.atualizar(id, request);

        assertEquals("Evento", atualizado.getCategoria());
        assertEquals("Palestra", atualizado.getTitulo());
        assertEquals("Nova descrição", atualizado.getDescricao());
        verify(repository).findById(id);
        verify(repository).save(existente);
    }

    @Test
    void deveRetornarErroAoAtualizarRegistroInexistente() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.atualizar(id, new InformacaoRequest(
                        "Comunicado", "Aviso", "Descrição",
                        LocalDate.of(2026, 11, 17)))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).save(any(Informacao.class));
    }

    @Test
    void deveConsultarPorTitulo() {
        when(repository.findByTituloContainingIgnoreCaseOrderByDataReferenciaDesc("reunião"))
                .thenReturn(List.of(new Informacao()));

        List<Informacao> resultado = service.consultar(" reunião ", null);

        assertEquals(1, resultado.size());
        verify(repository)
                .findByTituloContainingIgnoreCaseOrderByDataReferenciaDesc("reunião");
    }

    @Test
    void deveConsultarPorCategoria() {
        when(repository.findByCategoriaIgnoreCaseOrderByDataReferenciaDesc("evento"))
                .thenReturn(List.of(new Informacao()));

        List<Informacao> resultado = service.consultar(null, " evento ");

        assertEquals(1, resultado.size());
        verify(repository)
                .findByCategoriaIgnoreCaseOrderByDataReferenciaDesc("evento");
    }

    @Test
    void deveConsultarTodosQuandoNaoExistemFiltros() {
        when(repository.findAllByOrderByDataReferenciaDesc())
                .thenReturn(List.of(new Informacao(), new Informacao()));

        List<Informacao> resultado = service.consultar(null, null);

        assertEquals(2, resultado.size());
        verify(repository).findAllByOrderByDataReferenciaDesc();
    }
}