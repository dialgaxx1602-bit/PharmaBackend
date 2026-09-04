package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.PedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PedidoService {

    PedidoResponseDTO registrarPedido(PedidoRequestDTO requestDTO);

    Optional<PedidoResponseDTO> findById(Long id);

    List<PedidoResponseDTO> findAll();

    List<PedidoResponseDTO> findByClienteId(Long clienteId);
}
