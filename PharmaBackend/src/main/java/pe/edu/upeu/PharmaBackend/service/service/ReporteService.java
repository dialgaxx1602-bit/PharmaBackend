package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteService {

    List<VentaPorCategoriaDTO> obtenerVentasPorCategoria(LocalDateTime desde, LocalDateTime hasta);

    List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(LocalDateTime desde, LocalDateTime hasta);
}
