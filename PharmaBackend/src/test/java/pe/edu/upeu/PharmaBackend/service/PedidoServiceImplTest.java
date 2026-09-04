package pe.edu.upeu.PharmaBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.PharmaBackend.dto.DetallePedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.entity.Cliente;
import pe.edu.upeu.PharmaBackend.entity.Pedido;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.BusinessException;
import pe.edu.upeu.PharmaBackend.exception.ResourceNotFoundException;
import pe.edu.upeu.PharmaBackend.exception.StockInsuficienteException;
import pe.edu.upeu.PharmaBackend.mapper.PedidoMapper;
import pe.edu.upeu.PharmaBackend.repository.ClienteRepository;
import pe.edu.upeu.PharmaBackend.repository.PedidoRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.impl.PedidoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Spy
    private PedidoMapper pedidoMapper = new PedidoMapper();

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@example.com")
                .estado(true)
                .build();

        Categoria categoria = Categoria.builder()
                .id(1L)
                .nombre("Analgesicos")
                .build();

        producto = Producto.builder()
                .id(10L)
                .nombre("Paracetamol 500mg")
                .precio(new BigDecimal("5.50"))
                .stock(20)
                .categoria(categoria)
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("Debe registrar un pedido exitosamente ejecutando los 12 pasos y reduciendo existencias")
    void registrarPedido_Exitoso() {

        DetallePedidoRequestDTO detalleReq = DetallePedidoRequestDTO.builder()
                .productoId(10L)
                .cantidad(2)
                .build();

        PedidoRequestDTO requestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .detalles(List.of(detalleReq))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(100L);
            p.setFecha(LocalDateTime.now());
            return p;
        });

        PedidoResponseDTO responseDTO = pedidoService.registrarPedido(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(100L, responseDTO.getId());
        assertEquals(1L, responseDTO.getClienteId());
        assertEquals("PENDIENTE", responseDTO.getEstado());
        assertEquals(new BigDecimal("11.00"), responseDTO.getTotal());
        assertEquals(1, responseDTO.getDetalles().size());
        assertEquals(18, producto.getStock());

        verify(clienteRepository).findById(1L);
        verify(productoRepository).findById(10L);
        verify(productoRepository).save(producto);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el cliente no existe")
    void registrarPedido_ClienteNoEncontrado() {
        PedidoRequestDTO requestDTO = PedidoRequestDTO.builder()
                .clienteId(99L)
                .detalles(List.of(DetallePedidoRequestDTO.builder().productoId(10L).cantidad(1).build()))
                .build();

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.registrarPedido(requestDTO));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar BusinessException si los detalles estan vacios")
    void registrarPedido_DetallesVacios() {
        PedidoRequestDTO requestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .detalles(Collections.emptyList())
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class, () -> pedidoService.registrarPedido(requestDTO));
    }

    @Test
    @DisplayName("Debe lanzar BusinessException si el stock es insuficiente")
    void registrarPedido_StockInsuficiente() {
        DetallePedidoRequestDTO detalleReq = DetallePedidoRequestDTO.builder()
                .productoId(10L)
                .cantidad(50)
                .build();

        PedidoRequestDTO requestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .detalles(List.of(detalleReq))
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        StockInsuficienteException ex = assertThrows(StockInsuficienteException.class, () -> pedidoService.registrarPedido(requestDTO));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        assertEquals(20, producto.getStock());
    }
}
