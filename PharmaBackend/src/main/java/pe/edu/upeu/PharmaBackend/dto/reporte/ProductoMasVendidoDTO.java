package pe.edu.upeu.PharmaBackend.dto.reporte;

import java.math.BigDecimal;

public record ProductoMasVendidoDTO(
        Long productoId,
        String productoNombre,
        Long cantidadVendida,
        BigDecimal totalFacturado
) {
}
