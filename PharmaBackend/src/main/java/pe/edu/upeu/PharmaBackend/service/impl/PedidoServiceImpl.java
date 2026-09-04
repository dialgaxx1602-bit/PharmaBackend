package pe.edu.upeu.PharmaBackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.PharmaBackend.dto.DetallePedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Cliente;
import pe.edu.upeu.PharmaBackend.entity.DetallePedido;
import pe.edu.upeu.PharmaBackend.entity.Pedido;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.BusinessException;
import pe.edu.upeu.PharmaBackend.exception.ResourceNotFoundException;
import pe.edu.upeu.PharmaBackend.exception.StockInsuficienteException;
import pe.edu.upeu.PharmaBackend.mapper.PedidoMapper;
import pe.edu.upeu.PharmaBackend.repository.ClienteRepository;
import pe.edu.upeu.PharmaBackend.repository.PedidoRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.service.PedidoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             ClienteRepository clienteRepository,
                             ProductoRepository productoRepository,
                             PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    @Transactional
    public PedidoResponseDTO registrarPedido(PedidoRequestDTO requestDTO) {
        log.info("Iniciando registro de pedido para el clienteId: {}", requestDTO.getClienteId());

        Cliente cliente = clienteRepository.findById(requestDTO.getClienteId())
                .orElseThrow(() -> {
                    log.warn("Cliente no encontrado con ID: {}", requestDTO.getClienteId());
                    return new ResourceNotFoundException("Cliente", "id", requestDTO.getClienteId());
                });

        if (requestDTO.getDetalles() == null || requestDTO.getDetalles().isEmpty()) {
            log.warn("Intento de registrar pedido sin detalles para clienteId: {}", requestDTO.getClienteId());
            throw new BusinessException("El pedido debe contener al menos un detalle");
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .fecha(LocalDateTime.now())
                .detalles(new ArrayList<>())
                .build();

        BigDecimal totalPedido = BigDecimal.ZERO;

        for (DetallePedidoRequestDTO detalleReq : requestDTO.getDetalles()) {

            Producto producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> {
                        log.warn("Producto no encontrado con ID: {}", detalleReq.getProductoId());
                        return new ResourceNotFoundException("Producto", "id", detalleReq.getProductoId());
                    });

            if (detalleReq.getCantidad() == null || detalleReq.getCantidad() <= 0) {
                log.warn("Cantidad invalida ({}) para producto ID: {}", detalleReq.getCantidad(), producto.getId());
                throw new BusinessException("La cantidad debe ser mayor a 0 para el producto: " + producto.getNombre());
            }

            if (producto.getStock() < detalleReq.getCantidad()) {
                log.warn("Stock insuficiente para el producto ID: {}. Disponible: {}, Solicitado: {}",
                        producto.getId(), producto.getStock(), detalleReq.getCantidad());
                throw new StockInsuficienteException(producto.getNombre(), producto.getStock(), detalleReq.getCantidad());
            }

            BigDecimal precioUnitario = producto.getPrecio();

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleReq.getCantidad()));

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(detalleReq.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build();

            pedido.addDetalle(detalle);

            producto.setStock(producto.getStock() - detalleReq.getCantidad());
            productoRepository.save(producto);

            totalPedido = totalPedido.add(subtotal);
        }

        pedido.setTotal(totalPedido);

        pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido registrado exitosamente con ID: {}, total: {}", guardado.getId(), guardado.getTotal());

        return pedidoMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PedidoResponseDTO> findById(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAll() {
        log.info("Obteniendo todos los pedidos");
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findByClienteId(Long clienteId) {
        log.info("Buscando pedidos por cliente ID: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
