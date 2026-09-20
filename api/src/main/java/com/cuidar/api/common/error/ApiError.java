package com.cuidar.api.common.error;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * RFC 7807 problem+json response body.
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code type}     - URI identifying the problem type (relative or absolute).</li>
 *   <li>{@code title}    - short, human-readable summary.</li>
 *   <li>{@code status}   - HTTP status code, mirrored for clients that can't read headers.</li>
 *   <li>{@code detail}   - longer, human-readable explanation specific to this occurrence.</li>
 *   <li>{@code instance} - URI reference identifying the specific occurrence (request path).</li>
 *   <li>{@code timestamp}- server-side timestamp.</li>
 *   <li>{@code errors}   - optional list of field-level errors (Bean Validation).</li>
 * </ul>
 */
public record ApiError(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        OffsetDateTime timestamp,
        List<FieldError> errors
) {

    public static ApiError of(int status, String title, String detail, String instance) {
        return new ApiError(
                "about:blank",
                title,
                status,
                detail,
                instance,
                OffsetDateTime.now(),
                null
        );
    }

    public static ApiError of(int status, String title, String detail, String instance, List<FieldError> errors) {
        return new ApiError(
                "about:blank",
                title,
                status,
                detail,
                instance,
                OffsetDateTime.now(),
                errors
        );
    }

    public record FieldError(String field, String message) {
    }
}