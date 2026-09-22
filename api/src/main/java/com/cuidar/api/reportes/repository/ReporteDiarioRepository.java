package com.cuidar.api.reportes.repository;

import com.cuidar.api.reportes.domain.ReporteDiario;

import java.util.List;

public interface ReporteDiarioRepository {
    ReporteDiario insert(ReporteDiario reporte);
    List<ReporteDiario> findByReservaId(Long reservaId);
}
