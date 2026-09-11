package pe.edu.upeu.PharmaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;
import pe.edu.upeu.PharmaBackend.service.service.ReporteService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Endpoints para reportes analíticos agregados")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas-por-categoria")
    @Operation(summary = "Reporte de ventas agrupadas por categoría",
               description = "Calcula la cantidad total de unidades vendidas y el monto facturado por cada categoría, filtrando solo ventas REGISTRADA y rango de fechas opcional.")
    public ResponseEntity<List<VentaPorCategoriaDTO>> getVentasPorCategoria(
            @Parameter(description = "Fecha inicial (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @Parameter(description = "Fecha final (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        List<VentaPorCategoriaDTO> resultado = reporteService.obtenerVentasPorCategoria(desde, hasta);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/productos-mas-vendidos")
    @Operation(summary = "Reporte de productos más vendidos",
               description = "Calcula la cantidad total de unidades y total facturado por producto, ordenado por cantidad descendente, filtrando solo ventas REGISTRADA y rango opcional.")
    public ResponseEntity<List<ProductoMasVendidoDTO>> getProductosMasVendidos(
            @Parameter(description = "Fecha inicial (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @Parameter(description = "Fecha final (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        List<ProductoMasVendidoDTO> resultado = reporteService.obtenerProductosMasVendidos(desde, hasta);
        return ResponseEntity.ok(resultado);
    }
}
