package com.cuidar.api.common.web;

/**
 * Centralised API path constants.
 *
 * <p>All controllers MUST mount their routes under {@link #V1} (i.e. {@code /api/v1/...}).</p>
 *
 * <p>Used by both controllers (e.g. {@code @RequestMapping(ApiPaths.V1 + "/auth")})
 * and security configuration (matchers for {@code /api/**}).</p>
 */
public final class ApiPaths {

    public static final String API = "/api";
    public static final String V1 = API + "/v1";

    public static final String AUTH = V1 + "/auth";
    public static final String CUIDADORES = V1 + "/cuidadores";
    public static final String NOTIFICACIONES = V1 + "/notificaciones";
    public static final String FAMILIAS = V1 + "/familias";

    private ApiPaths() {
        // utility class
    }
}