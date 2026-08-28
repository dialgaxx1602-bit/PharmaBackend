package pe.edu.upeu.PharmaBackend.service.service;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import java.util.List;
import java.util.Optional;
public interface CategoriaService {
    Categoria create(Categoria categoria);
    Categoria update(Long id, Categoria categoria);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    List<Categoria> findByNombre(String nombre);
    void delete(Long id);
}