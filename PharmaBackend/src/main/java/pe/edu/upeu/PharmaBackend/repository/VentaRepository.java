package pe.edu.upeu.PharmaBackend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;
import pe.edu.upeu.PharmaBackend.entity.Pedido;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto prod LEFT JOIN FETCH p.cliente c " +
           "WHERE (:clienteId IS NULL OR p.cliente.id = :clienteId) " +
           "AND (:estado IS NULL OR UPPER(p.estado) = UPPER(:estado)) " +
           "AND (CAST(:desde AS java.time.LocalDateTime) IS NULL OR p.fecha >= :desde) " +
           "AND (CAST(:hasta AS java.time.LocalDateTime) IS NULL OR p.fecha <= :hasta)")
    List<Pedido> buscar(@Param("clienteId") Long clienteId,
                        @Param("estado") String estado,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta,
                        Sort sort);

    @Query("SELECT new pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO(" +
           "cat.id, " +
           "cat.nombre, " +
           "COALESCE(SUM(d.cantidad), 0L), " +
           "COALESCE(SUM(d.subtotal), 0.0)) " +
           "FROM DetallePedido d " +
           "JOIN d.pedido p " +
           "JOIN d.producto prod " +
           "JOIN prod.categoria cat " +
           "WHERE UPPER(p.estado) = 'REGISTRADA' " +
           "AND (CAST(:desde AS java.time.LocalDateTime) IS NULL OR p.fecha >= :desde) " +
           "AND (CAST(:hasta AS java.time.LocalDateTime) IS NULL OR p.fecha <= :hasta) " +
           "GROUP BY cat.id, cat.nombre " +
           "ORDER BY SUM(d.subtotal) DESC")
    List<VentaPorCategoriaDTO> reporteVentasPorCategoria(@Param("desde") LocalDateTime desde,
                                                         @Param("hasta") LocalDateTime hasta);

    @Query("SELECT new pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO(" +
           "prod.id, " +
           "prod.nombre, " +
           "COALESCE(SUM(d.cantidad), 0L), " +
           "COALESCE(SUM(d.subtotal), 0.0)) " +
           "FROM DetallePedido d " +
           "JOIN d.pedido p " +
           "JOIN d.producto prod " +
           "WHERE UPPER(p.estado) = 'REGISTRADA' " +
           "AND (CAST(:desde AS java.time.LocalDateTime) IS NULL OR p.fecha >= :desde) " +
           "AND (CAST(:hasta AS java.time.LocalDateTime) IS NULL OR p.fecha <= :hasta) " +
           "GROUP BY prod.id, prod.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoMasVendidoDTO> reporteProductosMasVendidos(@Param("desde") LocalDateTime desde,
                                                           @Param("hasta") LocalDateTime hasta);
}
