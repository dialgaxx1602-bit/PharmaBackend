package pe.edu.upeu.PharmaBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "El ID de cliente es obligatorio")
    @Positive(message = "El ID de cliente debe ser un número positivo")
    private Long clienteId;

    @NotNull(message = "La lista de detalles no puede ser nula")
    @NotEmpty(message = "La lista de detalles no puede estar vacía")
    @Valid
    private List<DetallePedidoRequestDTO> detalles;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public List<DetallePedidoRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoRequestDTO> detalles) { this.detalles = detalles; }
}
