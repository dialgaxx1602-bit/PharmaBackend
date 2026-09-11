package pe.edu.upeu.PharmaBackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.PharmaBackend.dto.PedidoResponseDTO;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.service.service.VentaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaController.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Test
    @DisplayName("Paso 11.1: Búsqueda con todos los filtros retorna 200 y lista")
    void buscarConTodosLosFiltros_Retorna200() throws Exception {
        PedidoResponseDTO pedidoDTO = PedidoResponseDTO.builder()
                .id(1L)
                .fecha(LocalDateTime.of(2026, 9, 1, 10, 0))
                .clienteId(1L)
                .clienteNombre("Juan Perez")
                .estado("REGISTRADA")
                .total(new BigDecimal("150.00"))
                .detalles(Collections.emptyList())
                .build();

        Mockito.when(ventaService.buscar(eq(1L), eq("REGISTRADA"), any(LocalDateTime.class), any(LocalDateTime.class), eq("fecha"), eq("DESC")))
                .thenReturn(List.of(pedidoDTO));

        mockMvc.perform(get("/api/v1/ventas/buscar")
                        .param("clienteId", "1")
                        .param("estado", "REGISTRADA")
                        .param("desde", "2026-09-01T00:00:00")
                        .param("hasta", "2026-09-10T23:59:59")
                        .param("sortBy", "fecha")
                        .param("sortDir", "DESC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estado", is("REGISTRADA")))
                .andExpect(jsonPath("$[0].total", is(150.00)));
    }

    @Test
    @DisplayName("Paso 11.2: Búsqueda solo con rango de fechas retorna 200 y resultados")
    void buscarSoloConRangoFechas_Retorna200() throws Exception {
        PedidoResponseDTO pedidoDTO = PedidoResponseDTO.builder()
                .id(2L)
                .fecha(LocalDateTime.of(2026, 9, 5, 12, 30))
                .clienteId(2L)
                .clienteNombre("Maria Gomez")
                .estado("REGISTRADA")
                .total(new BigDecimal("80.00"))
                .detalles(Collections.emptyList())
                .build();

        Mockito.when(ventaService.buscar(isNull(), isNull(), any(LocalDateTime.class), any(LocalDateTime.class), eq("fecha"), eq("DESC")))
                .thenReturn(List.of(pedidoDTO));

        mockMvc.perform(get("/api/v1/ventas/buscar")
                        .param("desde", "2026-09-01T00:00:00")
                        .param("hasta", "2026-09-10T23:59:59")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    @DisplayName("Paso 11.3: Búsqueda sin coincidencias retorna 200 con arreglo vacío []")
    void buscarSinCoincidencias_Retorna200ArregloVacio() throws Exception {
        Mockito.when(ventaService.buscar(any(), any(), any(), any(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/ventas/buscar")
                        .param("clienteId", "999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Paso 11.4: Ordenamiento inválido lanza 409 mediante ReglaNegocioException")
    void buscarConOrdenamientoInvalido_Retorna409() throws Exception {
        Mockito.when(ventaService.buscar(any(), any(), any(), any(), eq("hack_column"), anyString()))
                .thenThrow(new ReglaNegocioException("Campo de ordenamiento no permitido: hack_column"));

        mockMvc.perform(get("/api/v1/ventas/buscar")
                        .param("sortBy", "hack_column")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado", is(409)))
                .andExpect(jsonPath("$.mensaje", is("Campo de ordenamiento no permitido: hack_column")));
    }
}
