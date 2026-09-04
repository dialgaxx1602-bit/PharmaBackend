package pe.edu.upeu.PharmaBackend.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.PharmaBackend.dto.DetallePedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.DetallePedido;
import pe.edu.upeu.PharmaBackend.entity.Pedido;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        List<DetallePedidoResponseDTO> detallesDTO = pedido.getDetalles() == null ? Collections.emptyList() :
                pedido.getDetalles().stream()
                        .map(this::toDetalleResponseDTO)
                        .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getFecha())
                .clienteId(pedido.getCliente() != null ? pedido.getCliente().getId() : null)
                .clienteNombre(pedido.getCliente() != null ? pedido.getCliente().getNombre() : null)
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .detalles(detallesDTO)
                .build();
    }

    public DetallePedidoResponseDTO toDetalleResponseDTO(DetallePedido detalle) {
        if (detalle == null) {
            return null;
        }

        return DetallePedidoResponseDTO.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null)
                .productoNombre(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}
