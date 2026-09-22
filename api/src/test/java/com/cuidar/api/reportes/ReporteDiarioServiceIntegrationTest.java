package com.cuidar.api.reportes;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.AdultoMayorRepository;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.reportes.api.dto.CrearReporteDiarioRequest;
import com.cuidar.api.reportes.domain.ReporteDiario;
import com.cuidar.api.reportes.service.ReporteDiarioService;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ReporteDiarioServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private ReporteDiarioService reporteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Autowired
    private FamiliaRepository familiaRepository;

    @Autowired
    private AdultoMayorRepository adultoMayorRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Usuario cuidadorUser;
    private Usuario familiaUser;
    private Reserva reservaConfirmada;

    @BeforeEach
    void setUp() {
        if (cuidadorUser == null) {
            cuidadorUser = usuarioRepository.insert(new Usuario(null, "cuid_reportes@test.com", "h", "C", "T", null, Usuario.ESTADO_ACTIVO, true, null));
            Cuidador cuidador = cuidadorRepository.insert(new Cuidador(null, cuidadorUser.id(), "P", 1, BigDecimal.TEN, "VERIFICADO", "PUBLICADO", null, null, null));

            familiaUser = usuarioRepository.insert(new Usuario(null, "fam_reportes@test.com", "h", "F", "T", null, Usuario.ESTADO_ACTIVO, true, null));
            Familia familia = familiaRepository.insert(new Familia(null, familiaUser.id(), "Dir", null, null));
            AdultoMayor adulto = adultoMayorRepository.insert(new AdultoMayor(null, familia.id(), "Abuelo", "Ap", LocalDate.of(1950, 1, 1), "Nada", null, null, null));

            reservaConfirmada = reservaRepository.insert(new Reserva(null, cuidador.id(), familia.id(), adulto.id(), OffsetDateTime.now(), OffsetDateTime.now().plusHours(2), BigDecimal.TEN, Reserva.ESTADO_CONFIRMADA, null, null, null));
        }
    }

    @Test
    void canCreateAndListReports() {
        CrearReporteDiarioRequest req = new CrearReporteDiarioRequest(
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "Jugamos cartas",
                "Todo bien",
                "Feliz"
        );

        // Cuidador crea reporte
        ReporteDiario reporte = reporteService.crearReporte(cuidadorUser.id(), reservaConfirmada.id(), req);
        assertThat(reporte.id()).isNotNull();

        // Familia lista reportes
        List<ReporteDiario> listFam = reporteService.listarReportes(familiaUser.id(), reservaConfirmada.id());
        assertThat(listFam).hasSize(1);
        assertThat(listFam.get(0).actividades()).isEqualTo("Jugamos cartas");
        
        // Cuidador lista reportes
        List<ReporteDiario> listCuid = reporteService.listarReportes(cuidadorUser.id(), reservaConfirmada.id());
        assertThat(listCuid).hasSize(1);
    }
}
