package com.cuidar.api.resenas;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.repository.AdultoMayorRepository;
import com.cuidar.api.familias.repository.FamiliaRepository;
import com.cuidar.api.resenas.api.dto.CrearResenaRequest;
import com.cuidar.api.resenas.domain.Resena;
import com.cuidar.api.resenas.service.ResenaService;
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
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ResenaServiceIntegrationTest {

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
    private ResenaService resenaService;

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

    private Usuario familiaUser;
    private Cuidador cuidador;
    private Reserva reservaPasada1;
    private Reserva reservaPasada2;

    @BeforeEach
    void setUp() {
        if (familiaUser == null) {
            Usuario cUser = usuarioRepository.insert(new Usuario(null, "cuid_res@test.com", "h", "C", "T", null, Usuario.ESTADO_ACTIVO, true, null));
            cuidador = cuidadorRepository.insert(new Cuidador(null, cUser.id(), "P", 1, BigDecimal.TEN, "VERIFICADO", "PUBLICADO", null, null, null));

            familiaUser = usuarioRepository.insert(new Usuario(null, "fam_res@test.com", "h", "F", "T", null, Usuario.ESTADO_ACTIVO, true, null));
<<<<<<< HEAD
            Familia familia = familiaRepository.insert(new Familia(null, familiaUser.id(), "Dir", null, null));
            AdultoMayor adulto = adultoMayorRepository.insert(new AdultoMayor(null, familia.id(), "Abuelo", "Ap", LocalDate.of(1950, 1, 1), "Nada", null, null, null));

            reservaPasada1 = reservaRepository.insert(new Reserva(null, cuidador.id(), familia.id(), adulto.id(), OffsetDateTime.now().minusDays(2), OffsetDateTime.now().minusDays(2).plusHours(2), BigDecimal.TEN, Reserva.ESTADO_CONFIRMADA, null, null, null));
            reservaPasada2 = reservaRepository.insert(new Reserva(null, cuidador.id(), familia.id(), adulto.id(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().minusDays(1).plusHours(2), BigDecimal.TEN, Reserva.ESTADO_CONFIRMADA, null, null, null));
=======
            Familia familia = familiaRepository.insert(new Familia(null, familiaUser.id(), "Dir", null, null, null));
            AdultoMayor adulto = adultoMayorRepository.insert(new AdultoMayor(null, familia.id(), "Abuelo", "Ap", LocalDate.of(1950, 1, 1), null, "Nada", null, null, null));

            reservaPasada1 = reservaRepository.insert(new Reserva(null, familia.id(), cuidador.id(), adulto.id(), LocalDate.now().minusDays(2), LocalDate.now().minusDays(2), null, BigDecimal.TEN, BigDecimal.TEN, Reserva.ESTADO_FINALIZADA, null, null, null));
            reservaPasada2 = reservaRepository.insert(new Reserva(null, familia.id(), cuidador.id(), adulto.id(), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), null, BigDecimal.TEN, BigDecimal.TEN, Reserva.ESTADO_FINALIZADA, null, null, null));
>>>>>>> 186f126 (feat(cuidadores): implement backend modules + frontend + dockerize)
        }
    }

    @Test
    void canCreateReviewAndAutoUpdateAverage() {
        // Primera reseña: 5 estrellas
        Resena res1 = resenaService.crearResenaDeFamiliaACuidador(familiaUser.id(), reservaPasada1.id(), new CrearResenaRequest((short) 5, "Excelente"));
        assertThat(res1.id()).isNotNull();

        Cuidador actualizado1 = cuidadorRepository.findById(cuidador.id()).orElseThrow();
<<<<<<< HEAD
        assertThat(actualizado1.promedioCalificacion()).isEqualByComparingTo("5.0");
=======
        assertThat(actualizado1.calificacionPromedio()).isEqualByComparingTo("5.0");
>>>>>>> 186f126 (feat(cuidadores): implement backend modules + frontend + dockerize)

        // Segunda reseña: 3 estrellas
        Resena res2 = resenaService.crearResenaDeFamiliaACuidador(familiaUser.id(), reservaPasada2.id(), new CrearResenaRequest((short) 3, "Regular"));
        
        Cuidador actualizado2 = cuidadorRepository.findById(cuidador.id()).orElseThrow();
        // (5 + 3) / 2 = 4
<<<<<<< HEAD
        assertThat(actualizado2.promedioCalificacion()).isEqualByComparingTo("4.0");
=======
        assertThat(actualizado2.calificacionPromedio()).isEqualByComparingTo("4.0");
>>>>>>> 186f126 (feat(cuidadores): implement backend modules + frontend + dockerize)
    }
}
