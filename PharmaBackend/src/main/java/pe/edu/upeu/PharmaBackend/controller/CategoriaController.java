package pe.edu.upeu.PharmaBackend.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.PharmaBackend.dto.CategoriaDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.service.service.CategoriaService;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    private CategoriaDTO mapToDTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.isEstado())
                .creadoEn(categoria.getCreadoEn())
                .actualizadoEn(categoria.getActualizadoEn())
                .build();
    }
    private Categoria mapToEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }
        return categoria;
    }
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getCategorias() {
        List<CategoriaDTO> dtos = categoriaService.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> getCategoriaById(@PathVariable Long id) {
        return categoriaService.findById(id)
                .map(categoria -> ResponseEntity.ok(mapToDTO(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<CategoriaDTO> createCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria nueva = mapToEntity(categoriaDTO);
        Categoria guardada = categoriaService.create(nueva);
        return new ResponseEntity<>(mapToDTO(guardada), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria actualizada = categoriaService.update(id, mapToEntity(categoriaDTO));
        return ResponseEntity.ok(mapToDTO(actualizada));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
