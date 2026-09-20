package com.cuidar.api.auth.repository;

import com.cuidar.api.auth.domain.Usuario;

import java.util.Optional;

/**
 * DAO contract for {@link Usuario} persistence.
 *
 * <p>The interface lives in {@code repository}; the jOOQ-backed implementation
 * lives in the same package and translates between {@link Usuario} and the
 * jOOQ-generated {@code UsuarioRecord}.</p>
 */
public interface UsuarioRepository {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(Long id);

    /**
     * Persists a new {@link Usuario} and returns the stored representation
     * (with id and fechaRegistro populated, when the DB supports it).
     */
    Usuario insert(Usuario usuario);

    /**
     * Updates an existing {@link Usuario} (matched by id) and returns the stored
     * representation.
     */
    Usuario update(Usuario usuario);
}