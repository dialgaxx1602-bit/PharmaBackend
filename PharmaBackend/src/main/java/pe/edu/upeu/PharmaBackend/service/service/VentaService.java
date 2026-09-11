package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {

    List<PedidoResponseDTO> buscar(Long clienteId,
                                   String estado,
                                   LocalDateTime desde,
                                   LocalDateTime hasta,
                                   String sortBy,
                                   String sortDir);
}
