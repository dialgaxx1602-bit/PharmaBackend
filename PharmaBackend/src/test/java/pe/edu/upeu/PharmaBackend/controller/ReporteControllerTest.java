package pe.edu.upeu.PharmaBackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO;
import pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO;
import pe.edu.upeu.PharmaBackend.service.service.ReporteService;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    @DisplayName("GET /api/v1/reportes/ventas-por-categoria retorna 200 y lista")
    void reporteVentasPorCategoria_Retorna200() throws Exception {
        VentaPorCategoriaDTO dto = new VentaPorCategoriaDTO(1L, "Antibioticos", 25L, new BigDecimal("450.00"));
        Mockito.when(reporteService.obtenerVentasPorCategoria(any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/reportes/ventas-por-categoria")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoriaId", is(1)))
                .andExpect(jsonPath("$[0].categoriaNombre", is("Antibioticos")))
                .andExpect(jsonPath("$[0].cantidadVendida", is(25)))
                .andExpect(jsonPath("$[0].totalFacturado", is(450.00)));
    }

    @Test
    @DisplayName("GET /api/v1/reportes/productos-mas-vendidos retorna 200 y lista")
    void reporteProductosMasVendidos_Retorna200() throws Exception {
        ProductoMasVendidoDTO dto = new ProductoMasVendidoDTO(10L, "Amoxicilina 500mg", 40L, new BigDecimal("600.00"));
        Mockito.when(reporteService.obtenerProductosMasVendidos(any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/reportes/productos-mas-vendidos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productoId", is(10)))
                .andExpect(jsonPath("$[0].productoNombre", is("Amoxicilina 500mg")))
                .andExpect(jsonPath("$[0].cantidadVendida", is(40)))
                .andExpect(jsonPath("$[0].totalFacturado", is(600.00)));
    }
}
