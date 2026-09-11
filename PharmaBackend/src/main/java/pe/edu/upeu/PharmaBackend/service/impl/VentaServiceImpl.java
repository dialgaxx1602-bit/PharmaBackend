package pe.edu.upeu.PharmaBackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.service.service.PedidoService;
import pe.edu.upeu.PharmaBackend.service.service.VentaService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VentaServiceImpl implements VentaService {

    private final PedidoService pedidoService;

    public VentaServiceImpl(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Override
    public List<PedidoResponseDTO> buscar(Long clienteId,
                                          String estado,
                                          LocalDateTime desde,
                                          LocalDateTime hasta,
                                          String sortBy,
                                          String sortDir) {
        return pedidoService.buscar(clienteId, estado, desde, hasta, sortBy, sortDir);
    }
}
