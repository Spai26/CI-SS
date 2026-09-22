package com.cuidar.api.familias;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.familias.api.dto.ActualizarFamiliaRequest;
import com.cuidar.api.familias.api.dto.AdultoMayorRequest;
import com.cuidar.api.familias.domain.AdultoMayor;
import com.cuidar.api.familias.domain.Familia;
import com.cuidar.api.familias.domain.GeneroAdulto;
import com.cuidar.api.familias.service.FamiliaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class FamiliaServiceIntegrationTest {

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
    private FamiliaService familiaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        if (testUser == null) {
            Usuario u = new Usuario(null, "test_familia@test.com", "hash", "Test", "User", null, Usuario.ESTADO_ACTIVO, true, null);
            testUser = usuarioRepository.insert(u);
        }
    }

    @Test
    void canGetAndUpdateFamilia() {
        Familia f = familiaService.getFamiliaByUsuarioId(testUser.id());
        assertThat(f.direccion()).isNull();

        ActualizarFamiliaRequest req = new ActualizarFamiliaRequest("Calle Falsa 123", "{\"experiencia\": 5}");
        Familia actualizada = familiaService.actualizarFamilia(testUser.id(), req);

        assertThat(actualizada.direccion()).isEqualTo("Calle Falsa 123");
        assertThat(actualizada.preferenciasBusqueda()).contains("experiencia");
    }

    @Test
    void canAddAndListAdultosMayores() {
        AdultoMayorRequest req = new AdultoMayorRequest(
                "Juan", "Perez", LocalDate.of(1950, 1, 1), GeneroAdulto.MASCULINO, "Hipertension", "Necesita medicacion"
        );
        AdultoMayor insertado = familiaService.agregarAdultoMayor(testUser.id(), req);
        assertThat(insertado.id()).isNotNull();
        assertThat(insertado.nombre()).isEqualTo("Juan");

        List<AdultoMayor> lista = familiaService.listarAdultosMayores(testUser.id());
        assertThat(lista).hasSizeGreaterThan(0);
        assertThat(lista.get(0).condicionSalud()).isEqualTo("Hipertension");
    }
}
