package pe.edu.upeu.PharmaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.service.service.VentaService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Endpoints para consultas y operaciones sobre ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ventas con filtros combinados y ordenamiento",
               description = "Permite filtrar por clienteId, estado, rango de fechas y ordenar por campos en lista blanca (id, fecha, total, estado)")
    public ResponseEntity<List<PedidoResponseDTO>> buscarVentas(
            @Parameter(description = "ID del cliente (opcional)")
            @RequestParam(required = false) Long clienteId,

            @Parameter(description = "Estado de la venta (opcional: REGISTRADA, ANULADA, PENDIENTE)")
            @RequestParam(required = false) String estado,

            @Parameter(description = "Fecha inicial (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,

            @Parameter(description = "Fecha final (formato ISO date-time, opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,

            @Parameter(description = "Campo de ordenamiento (id, fecha, total, estado; por defecto fecha)")
            @RequestParam(required = false, defaultValue = "fecha") String sortBy,

            @Parameter(description = "Dirección de ordenamiento (ASC, DESC; por defecto DESC)")
            @RequestParam(required = false, defaultValue = "DESC") String sortDir
    ) {
        List<PedidoResponseDTO> response = ventaService.buscar(clienteId, estado, desde, hasta, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}
