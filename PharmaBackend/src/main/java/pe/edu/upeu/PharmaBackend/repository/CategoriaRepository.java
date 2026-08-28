package pe.edu.upeu.PharmaBackend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import java.util.List;
import java.util.Optional;
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    List<Categoria> findByEstado(boolean estado);
    boolean existsByNombreIgnoreCase(String nombre);
}
