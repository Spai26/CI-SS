package com.cuidar.api.reservas;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.reservas.api.dto.CambiarEstadoReservaRequest;
import com.cuidar.api.reservas.api.dto.CrearReservaRequest;
import com.cuidar.api.reservas.domain.Reserva;
import com.cuidar.api.reservas.service.ReservaService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ReservaServiceIntegrationTest {

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
    private ReservaService reservaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FamiliaRepository familiaRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    private Usuario familiaUser;
    private Usuario cuidadorUser;
    private Familia familia;
    private Cuidador cuidador;

    @BeforeEach
    void setUp() {
        if (familiaUser == null) {
            Usuario u1 = new Usuario(null, "familia_reserva@test.com", "hash", "Fam", "Test", null, Usuario.ESTADO_ACTIVO, true, null);
            familiaUser = usuarioRepository.insert(u1);
            familia = familiaRepository.insert(new Familia(null, familiaUser.id(), "Dir", null, null, null));
            
            Usuario u2 = new Usuario(null, "cuidador_reserva@test.com", "hash", "Cuid", "Test", null, Usuario.ESTADO_ACTIVO, true, null);
            cuidadorUser = usuarioRepository.insert(u2);
            cuidador = cuidadorRepository.insert(new Cuidador(null, cuidadorUser.id(), "Pres", 5, new BigDecimal("20.00"), Cuidador.ESTADO_VERIFICACION_VERIFICADO, Cuidador.ESTADO_PUBLICACION_PUBLICADO, null, null, null));
        }
    }

    @Test
    void canCreateAndChangeReservaState() {
        CrearReservaRequest req = new CrearReservaRequest(
                cuidador.id(),
                1L, // Dummy adulto mayor id
                LocalDate.now(),
                null,
                "POR_HORAS",
                new BigDecimal("5.00")
        );

        Reserva reserva = reservaService.crearReserva(familiaUser.id(), req);
        assertThat(reserva.id()).isNotNull();
        assertThat(reserva.estado()).isEqualTo(Reserva.ESTADO_PENDIENTE);
        assertThat(reserva.montoTotal()).isEqualByComparingTo(new BigDecimal("100.00")); // 5 horas * 20.00 tarifa

        CambiarEstadoReservaRequest changeReq = new CambiarEstadoReservaRequest(Reserva.ESTADO_CONFIRMADA, null);
        Reserva confirmada = reservaService.cambiarEstado(reserva.id(), changeReq);
        assertThat(confirmada.estado()).isEqualTo(Reserva.ESTADO_CONFIRMADA);

        List<Reserva> reservasFamilia = reservaService.listarPorFamilia(familiaUser.id());
        assertThat(reservasFamilia).hasSize(1);
    }
}
