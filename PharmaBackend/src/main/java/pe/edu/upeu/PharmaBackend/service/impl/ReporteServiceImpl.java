package pe.edu.upeu.PharmaBackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.repository.VentaRepository;
import pe.edu.upeu.PharmaBackend.service.service.ReporteService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReporteServiceImpl implements ReporteService {

    private final VentaRepository ventaRepository;

    public ReporteServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPorCategoriaDTO> obtenerVentasPorCategoria(LocalDateTime desde, LocalDateTime hasta) {
        long inicio = System.currentTimeMillis();
        log.info("Iniciando consulta reporte ventas por categoria - desde: {}, hasta: {}", desde, hasta);

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            log.warn("Rango de fechas invalido en reporte ventas por categoria: 'desde' ({}) es posterior a 'hasta' ({})", desde, hasta);
            throw new ReglaNegocioException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        List<VentaPorCategoriaDTO> reporte = ventaRepository.reporteVentasPorCategoria(desde, hasta);

        long duracionMs = System.currentTimeMillis() - inicio;
        log.info("Reporte ventas por categoria completado - filas retornadas: {}, duracion: {} ms", reporte.size(), duracionMs);

        return reporte;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(LocalDateTime desde, LocalDateTime hasta) {
        long inicio = System.currentTimeMillis();
        log.info("Iniciando consulta reporte productos mas vendidos - desde: {}, hasta: {}", desde, hasta);

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            log.warn("Rango de fechas invalido en reporte productos mas vendidos: 'desde' ({}) es posterior a 'hasta' ({})", desde, hasta);
            throw new ReglaNegocioException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        List<ProductoMasVendidoDTO> reporte = ventaRepository.reporteProductosMasVendidos(desde, hasta);

        long duracionMs = System.currentTimeMillis() - inicio;
        log.info("Reporte productos mas vendidos completado - filas retornadas: {}, duracion: {} ms", reporte.size(), duracionMs);

        return reporte;
    }
}
