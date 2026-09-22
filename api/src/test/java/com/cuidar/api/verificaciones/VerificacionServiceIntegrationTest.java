package com.cuidar.api.verificaciones;

import com.cuidar.api.auth.domain.Usuario;
import com.cuidar.api.auth.repository.UsuarioRepository;
import com.cuidar.api.cuidadores.domain.Cuidador;
import com.cuidar.api.cuidadores.repository.CuidadorRepository;
import com.cuidar.api.verificaciones.api.dto.CrearSolicitudRequest;
import com.cuidar.api.verificaciones.api.dto.DocumentoVerificacionRequest;
import com.cuidar.api.verificaciones.api.dto.ResolverSolicitudRequest;
import com.cuidar.api.verificaciones.domain.DocumentoVerificacion;
import com.cuidar.api.verificaciones.domain.SolicitudVerificacion;
import com.cuidar.api.verificaciones.service.VerificacionService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class VerificacionServiceIntegrationTest {

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
    private VerificacionService verificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    private Usuario adminUser;
    private Usuario cuidadorUser;
    private Cuidador cuidador;

    @BeforeEach
    void setUp() {
        if (adminUser == null) {
            Usuario u1 = new Usuario(null, "admin_verif@test.com", "hash", "Admin", "Test", null, Usuario.ESTADO_ACTIVO, true, null);
            adminUser = usuarioRepository.insert(u1);
            
            Usuario u2 = new Usuario(null, "cuidador_verif@test.com", "hash", "Cuid", "Test", null, Usuario.ESTADO_ACTIVO, true, null);
            cuidadorUser = usuarioRepository.insert(u2);
            // Empieza en BORRADOR
<<<<<<< HEAD
            cuidador = cuidadorRepository.insert(new Cuidador(null, cuidadorUser.id(), "Pres", 5, new BigDecimal("20.00"), Cuidador.ESTADO_VERIFICACION_BORRADOR, Cuidador.ESTADO_PUBLICACION_BORRADOR, null, null, null));
=======
            cuidador = cuidadorRepository.insert(new Cuidador(null, cuidadorUser.id(), "Pres", 5, new BigDecimal("20.00"), Cuidador.ESTADO_VERIFICACION_NO_VERIFICADO, Cuidador.ESTADO_PUBLICACION_BORRADOR, null, null, null));
>>>>>>> 186f126 (feat(cuidadores): implement backend modules + frontend + dockerize)
        }
    }

    @Test
    void canCreateAndApproveVerification() {
        // Cuidador manda solicitud
        DocumentoVerificacionRequest docReq = new DocumentoVerificacionRequest(DocumentoVerificacion.TIPO_IDENTIDAD, "http://s3.com/dni.pdf");
        CrearSolicitudRequest req = new CrearSolicitudRequest(List.of(docReq));

        SolicitudVerificacion solicitud = verificacionService.crearSolicitud(cuidadorUser.id(), req);
        assertThat(solicitud.id()).isNotNull();
        assertThat(solicitud.estado()).isEqualTo(SolicitudVerificacion.ESTADO_PENDIENTE);
        assertThat(solicitud.documentos()).hasSize(1);

        // Admin aprueba
        ResolverSolicitudRequest resolucion = new ResolverSolicitudRequest(SolicitudVerificacion.ESTADO_APROBADA, "Todo en orden");
        SolicitudVerificacion aprobada = verificacionService.resolverSolicitud(adminUser.id(), solicitud.id(), resolucion);
        assertThat(aprobada.estado()).isEqualTo(SolicitudVerificacion.ESTADO_APROBADA);
        assertThat(aprobada.revisadaPor()).isEqualTo(adminUser.id());

        // Verificamos que el Cuidador cambió su estado de verificación mágicamente
        Cuidador actual = cuidadorRepository.findById(cuidador.id()).orElseThrow();
        assertThat(actual.estadoVerificacion()).isEqualTo(Cuidador.ESTADO_VERIFICACION_VERIFICADO);
    }
}
