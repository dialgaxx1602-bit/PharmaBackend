package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.PedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoService {

    List<PedidoResponseDTO> buscar(Long clienteId,
                                   String estado,
                                   LocalDateTime desde,
                                   LocalDateTime hasta,
                                   String sortBy,
                                   String sortDir);

    PedidoResponseDTO registrarPedido(PedidoRequestDTO requestDTO);

    Optional<PedidoResponseDTO> findById(Long id);

    List<PedidoResponseDTO> findAll();

    List<PedidoResponseDTO> findByClienteId(Long clienteId);

    PedidoResponseDTO anularPedido(Long id);

    PedidoResponseDTO actualizarEstado(Long id, String nuevoEstado);
}
