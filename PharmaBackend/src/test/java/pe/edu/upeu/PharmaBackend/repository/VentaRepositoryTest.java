package pe.edu.upeu.PharmaBackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;
import pe.edu.upeu.PharmaBackend.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(pe.edu.upeu.PharmaBackend.config.JpaAuditingConfig.class)
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private Cliente cliente1;
    private Cliente cliente2;
    private Categoria catAntibioticos;
    private Categoria catAnalgesicos;
    private Producto prodAmox;
    private Producto prodParac;

    @BeforeEach
    void setUp() {
        cliente1 = clienteRepository.save(Cliente.builder()
                .nombre("Carlos Mendoza")
                .email("carlos@example.com")
                .estado(true)
                .build());

        cliente2 = clienteRepository.save(Cliente.builder()
                .nombre("Ana Lopez")
                .email("ana@example.com")
                .estado(true)
                .build());

        catAntibioticos = categoriaRepository.save(Categoria.builder()
                .nombre("Antibioticos")
                .descripcion("Medicamentos antibacterianos")
                .estado(true)
                .build());

        catAnalgesicos = categoriaRepository.save(Categoria.builder()
                .nombre("Analgesicos")
                .descripcion("Medicamentos para el dolor")
                .estado(true)
                .build());

        prodAmox = productoRepository.save(Producto.builder()
                .nombre("Amoxicilina 500mg")
                .precio(new BigDecimal("15.00"))
                .stock(100)
                .categoria(catAntibioticos)
                .estado(true)
                .build());

        prodParac = productoRepository.save(Producto.builder()
                .nombre("Paracetamol 500mg")
                .precio(new BigDecimal("5.00"))
                .stock(200)
                .categoria(catAnalgesicos)
                .estado(true)
                .build());

        // Pedido 1: REGISTRADA, Cliente 1, Amoxicilina x2 (30.00)
        Pedido p1 = Pedido.builder()
                .cliente(cliente1)
                .fecha(LocalDateTime.of(2026, 9, 2, 10, 0))
                .estado(EstadoVenta.REGISTRADA.name())
                .total(new BigDecimal("30.00"))
                .build();
        p1.addDetalle(DetallePedido.builder()
                .producto(prodAmox)
                .cantidad(2)
                .precioUnitario(new BigDecimal("15.00"))
                .subtotal(new BigDecimal("30.00"))
                .build());
        ventaRepository.save(p1);

        // Pedido 2: REGISTRADA, Cliente 2, Paracetamol x4 (20.00)
        Pedido p2 = Pedido.builder()
                .cliente(cliente2)
                .fecha(LocalDateTime.of(2026, 9, 4, 14, 0))
                .estado(EstadoVenta.REGISTRADA.name())
                .total(new BigDecimal("20.00"))
                .build();
        p2.addDetalle(DetallePedido.builder()
                .producto(prodParac)
                .cantidad(4)
                .precioUnitario(new BigDecimal("5.00"))
                .subtotal(new BigDecimal("20.00"))
                .build());
        ventaRepository.save(p2);

        // Pedido 3: ANULADA (no debe sumarse a reportes)
        Pedido p3 = Pedido.builder()
                .cliente(cliente1)
                .fecha(LocalDateTime.of(2026, 9, 6, 11, 0))
                .estado(EstadoVenta.ANULADA.name())
                .total(new BigDecimal("150.00"))
                .build();
        p3.addDetalle(DetallePedido.builder()
                .producto(prodAmox)
                .cantidad(10)
                .precioUnitario(new BigDecimal("15.00"))
                .subtotal(new BigDecimal("150.00"))
                .build());
        ventaRepository.save(p3);
    }

    @Test
    @DisplayName("buscar(...) con filtros combinados admite nulos y aplica Sort")
    void testBuscarConFiltrosYSort() {
        // Filtrar por cliente1
        List<Pedido> resultadoCliente1 = ventaRepository.buscar(
                cliente1.getId(), null, null, null, Sort.by(Sort.Direction.DESC, "fecha")
        );
        assertEquals(2, resultadoCliente1.size());

        // Filtrar por estado REGISTRADA
        List<Pedido> resultadoRegistradas = ventaRepository.buscar(
                null, "REGISTRADA", null, null, Sort.by(Sort.Direction.ASC, "total")
        );
        assertEquals(2, resultadoRegistradas.size());
        assertEquals(new BigDecimal("20.00"), resultadoRegistradas.get(0).getTotal());

        // Filtrar por rango de fechas
        List<Pedido> resultadoFechas = ventaRepository.buscar(
                null, null,
                LocalDateTime.of(2026, 9, 3, 0, 0),
                LocalDateTime.of(2026, 9, 5, 23, 59),
                Sort.by(Sort.Direction.DESC, "fecha")
        );
        assertEquals(1, resultadoFechas.size());
        assertEquals(cliente2.getId(), resultadoFechas.get(0).getCliente().getId());
    }

    @Test
    @DisplayName("Paso 12: Comprobar que ventas ANULADAS no aparecen en los reportes")
    void testReportesExcluyenVentasAnuladas() {
        List<VentaPorCategoriaDTO> catReporte = ventaRepository.reporteVentasPorCategoria(null, null);
        assertNotNull(catReporte);
        assertEquals(2, catReporte.size());

        // Verificar que la venta ANULADA de Amoxicilina (10 unidades, 150.00) NO fue sumada
        VentaPorCategoriaDTO catAntib = catReporte.stream()
                .filter(c -> c.categoriaNombre().equals("Antibioticos"))
                .findFirst().orElseThrow();
        assertEquals(2L, catAntib.cantidadVendida());
        assertEquals(0, new BigDecimal("30.00").compareTo(catAntib.totalFacturado()));

        List<ProductoMasVendidoDTO> prodReporte = ventaRepository.reporteProductosMasVendidos(null, null);
        assertNotNull(prodReporte);
        assertEquals(2, prodReporte.size());

        ProductoMasVendidoDTO prodAmoxDTO = prodReporte.stream()
                .filter(p -> p.productoNombre().equals("Amoxicilina 500mg"))
                .findFirst().orElseThrow();
        assertEquals(2L, prodAmoxDTO.cantidadVendida());
        assertEquals(0, new BigDecimal("30.00").compareTo(prodAmoxDTO.totalFacturado()));
    }
}
