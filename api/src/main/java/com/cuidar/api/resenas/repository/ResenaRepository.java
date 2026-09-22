package com.cuidar.api.resenas.repository;

import com.cuidar.api.resenas.domain.Resena;

import java.math.BigDecimal;
import java.util.List;

public interface ResenaRepository {
    Resena insert(Resena resena);
    List<Resena> findByDestinatarioId(Long destinatarioId);
    BigDecimal calcularPromedioPorDestinatario(Long destinatarioId);
}
