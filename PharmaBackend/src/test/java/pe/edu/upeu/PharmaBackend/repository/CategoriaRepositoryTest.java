package pe.edu.upeu.PharmaBackend.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class CategoriaRepositoryTest {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Test
    public void testSaveAndFindByName() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Analgésicos");
        categoria.setDescripcion("Medicamentos para el dolor");
        categoria.setEstado(true);
        categoriaRepository.save(categoria);
        Optional<Categoria> found = categoriaRepository.findByNombre("Analgésicos");
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Analgésicos");
    }
}
