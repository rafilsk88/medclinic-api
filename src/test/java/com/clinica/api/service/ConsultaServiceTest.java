package com.clinica.api.service;

import com.clinica.api.dto.ConsultaRequest;
import com.clinica.api.dto.ConsultaResponse;
import com.clinica.api.exception.ConflitoException;
import com.clinica.api.exception.RegraNegocioException;
import com.clinica.api.model.Consulta;
import com.clinica.api.model.Medico;
import com.clinica.api.model.Paciente;
import com.clinica.api.model.StatusConsulta;
import com.clinica.api.repository.ConsultaRepository;
import com.clinica.api.repository.MedicoRepository;
import com.clinica.api.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock ConsultaRepository consultaRepository;
    @Mock MedicoRepository medicoRepository;
    @Mock PacienteRepository pacienteRepository;
    @InjectMocks ConsultaService service;

    Medico medico;
    Paciente paciente;

    @BeforeEach
    void setUp() {
        medico = Medico.builder().id(1L).nome("Dra. Ana").crm("123456-SP").especialidade("Cardiologia").build();
        paciente = Paciente.builder().id(2L).nome("João").cpf("52998224725")
                .dataNascimento(LocalDate.of(1990, 1, 1)).build();
    }

    private Consulta consultaCom(StatusConsulta status) {
        return Consulta.builder().id(10L).medico(medico).paciente(paciente)
                .dataHora(LocalDateTime.now().plusDays(1)).status(status).build();
    }

    @Test
    void deveAgendarConsultaQuandoHorarioLivre() {
        var req = new ConsultaRequest(2L, 1L, LocalDateTime.now().plusDays(1), null);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(consultaRepository.existeConflito(anyLong(), any(), any(), any())).thenReturn(false);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(i -> i.getArgument(0));

        ConsultaResponse resp = service.agendar(req);

        assertEquals(StatusConsulta.AGENDADA, resp.status());
        assertEquals("Dra. Ana", resp.medicoNome());
    }

    @Test
    void deveRejeitarAgendamentoComConflitoDeHorario() {
        var req = new ConsultaRequest(2L, 1L, LocalDateTime.now().plusDays(1), null);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(consultaRepository.existeConflito(anyLong(), any(), any(), any())).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.agendar(req));
        verify(consultaRepository, never()).save(any());
    }

    @Test
    void deveRejeitarAgendamentoComMedicoInativo() {
        medico.setAtivo(false);
        var req = new ConsultaRequest(2L, 1L, LocalDateTime.now().plusDays(1), null);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        assertThrows(RegraNegocioException.class, () -> service.agendar(req));
    }

    @Test
    void deveConfirmarConsultaAgendada() {
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consultaCom(StatusConsulta.AGENDADA)));

        assertEquals(StatusConsulta.CONFIRMADA, service.confirmar(10L).status());
    }

    @Test
    void naoDeveIniciarAtendimentoSemConfirmacao() {
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consultaCom(StatusConsulta.AGENDADA)));

        assertThrows(RegraNegocioException.class, () -> service.iniciarAtendimento(10L));
    }

    @Test
    void deveCancelarComMotivo() {
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consultaCom(StatusConsulta.CONFIRMADA)));

        ConsultaResponse resp = service.cancelar(10L, "Paciente desistiu");

        assertEquals(StatusConsulta.CANCELADA, resp.status());
        assertEquals("Paciente desistiu", resp.motivoCancelamento());
    }

    @Test
    void naoDeveCancelarConsultaConcluida() {
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consultaCom(StatusConsulta.CONCLUIDA)));

        assertThrows(RegraNegocioException.class, () -> service.cancelar(10L, "x"));
    }
}
