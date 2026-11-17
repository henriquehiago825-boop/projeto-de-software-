package br.com.instituicao.gestao.service;

import br.com.instituicao.gestao.dto.InformacaoRequest;
import br.com.instituicao.gestao.model.Informacao;
import br.com.instituicao.gestao.repository.InformacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InformacaoServiceTest {
    @Mock InformacaoRepository repository;
    @InjectMocks InformacaoService service;

    @Test
    void deveCadastrarInformacaoRemovendoEspacosExcedentes() {
        InformacaoRequest request = new InformacaoRequest(" Comunicado ", " Reunião ", " Aviso geral ", LocalDate.of(2026, 11, 16));
        when(repository.save(any(Informacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Informacao salvo = service.cadastrar(request);
        assertEquals("Comunicado", salvo.getCategoria());
        assertEquals("Reunião", salvo.getTitulo());
        assertEquals("Aviso geral", salvo.getDescricao());
        verify(repository).save(any(Informacao.class));
    }
}
