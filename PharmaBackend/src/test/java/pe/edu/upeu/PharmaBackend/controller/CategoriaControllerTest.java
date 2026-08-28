package pe.edu.upeu.PharmaBackend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.PharmaBackend.dto.CategoriaDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.service.service.CategoriaService;
import java.util.Arrays;
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoriaService categoriaService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void testGetCategorias() throws Exception {
        Categoria cat1 = new Categoria();
        cat1.setId(1L);
        cat1.setNombre("Cat1");
        cat1.setEstado(true);
        Mockito.when(categoriaService.findAll()).thenReturn(Arrays.asList(cat1));
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Cat1")));
    }
    @Test
    public void testCreateCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Nueva");
        dto.setEstado(true);
        Categoria guardada = new Categoria();
        guardada.setId(1L);
        guardada.setNombre("Nueva");
        guardada.setEstado(true);
        Mockito.when(categoriaService.create(any(Categoria.class))).thenReturn(guardada);
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Nueva")));
    }
    @Test
    public void testUpdateCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Actualizada");
        dto.setEstado(true);
        Categoria actualizada = new Categoria();
        actualizada.setId(1L);
        actualizada.setNombre("Actualizada");
        actualizada.setEstado(true);
        Mockito.when(categoriaService.update(eq(1L), any(Categoria.class))).thenReturn(actualizada);
        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Actualizada")));
    }
    @Test
    public void testDeleteCategoria() throws Exception {
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(categoriaService, Mockito.times(1)).delete(1L);
    }
    @Test
    public void testValidacionDto() throws Exception {
        CategoriaDTO dto = new CategoriaDTO(); 
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }
}
