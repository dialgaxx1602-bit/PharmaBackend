package pe.edu.upeu.PharmaBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.PharmaBackend.dto.CategoriaResumenDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.exception.ResourceNotFoundException;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateProductoConAsociacionValida() throws Exception {
        ProductoRequestDTO requestDTO = ProductoRequestDTO.builder()
                .nombre("Paracetamol 500mg")
                .precio(new BigDecimal("5.50"))
                .stock(100)
                .categoriaId(1L)
                .build();

        CategoriaResumenDTO catResumen = CategoriaResumenDTO.builder()
                .id(1L)
                .nombre("Analgésicos")
                .build();

        ProductoResponseDTO responseDTO = ProductoResponseDTO.builder()
                .id(10L)
                .nombre("Paracetamol 500mg")
                .precio(new BigDecimal("5.50"))
                .stock(100)
                .categoria(catResumen)
                .build();

        Mockito.when(productoService.create(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombre", is("Paracetamol 500mg")))
                .andExpect(jsonPath("$.precio", is(5.50)))
                .andExpect(jsonPath("$.stock", is(100)))
                .andExpect(jsonPath("$.categoria.id", is(1)))
                .andExpect(jsonPath("$.categoria.nombre", is("Analgésicos")));
    }

    @Test
    public void testGetAllProductos() throws Exception {
        ProductoResponseDTO responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Ibuprofeno")
                .precio(new BigDecimal("8.00"))
                .stock(50)
                .categoria(CategoriaResumenDTO.builder().id(1L).nombre("Analgésicos").build())
                .build();

        Mockito.when(productoService.findAll()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Ibuprofeno")));
    }

    @Test
    public void testGetProductoById() throws Exception {
        ProductoResponseDTO responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Ibuprofeno")
                .precio(new BigDecimal("8.00"))
                .stock(50)
                .build();

        Mockito.when(productoService.findById(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Ibuprofeno")));
    }

    @Test
    public void testUpdateProducto() throws Exception {
        ProductoRequestDTO requestDTO = ProductoRequestDTO.builder()
                .nombre("Paracetamol Forte")
                .precio(new BigDecimal("6.50"))
                .stock(80)
                .categoriaId(1L)
                .build();

        ProductoResponseDTO responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Paracetamol Forte")
                .precio(new BigDecimal("6.50"))
                .stock(80)
                .build();

        Mockito.when(productoService.update(eq(1L), any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Paracetamol Forte")));
    }

    @Test
    public void testDeleteProducto() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productoService, Mockito.times(1)).delete(1L);
    }

    @Test
    public void testCreateProductoConCategoriaInexistente_RespuestaControlada() throws Exception {
        ProductoRequestDTO requestDTO = ProductoRequestDTO.builder()
                .nombre("Producto de prueba")
                .precio(new BigDecimal("25.00"))
                .stock(10)
                .categoriaId(999999L)
                .build();

        Mockito.when(productoService.create(any(ProductoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Categoria", "id", 999999L));

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado", is(404)))
                .andExpect(jsonPath("$.error", is("Recurso no encontrado")))
                .andExpect(jsonPath("$.mensaje", is("Categoria no encontrado con id : '999999'")));
    }

    @Test
    public void testValidacionDtoInvalido() throws Exception {
        ProductoRequestDTO dtoInvalido = new ProductoRequestDTO();

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.estado", is(422)))
                .andExpect(jsonPath("$.error", is("Error de validacion")));
    }
}
