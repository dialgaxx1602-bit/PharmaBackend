package pe.edu.upeu.PharmaBackend.service.impl;
import org.springframework.stereotype.Service;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.exception.BusinessException;
import pe.edu.upeu.PharmaBackend.exception.ResourceNotFoundException;
import pe.edu.upeu.PharmaBackend.repository.CategoriaRepository;
import pe.edu.upeu.PharmaBackend.service.service.CategoriaService;
import java.util.List;
import java.util.Optional;
@Service
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
    @Override
    public Categoria create(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new BusinessException("El nombre de la categoría no puede estar vacío");
        }
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }
    @Override
    public Categoria update(Long id, Categoria categoria) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new BusinessException("El nombre de la categoría no puede estar vacío");
        }
        if (!existente.getNombre().equalsIgnoreCase(categoria.getNombre()) &&
            categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new BusinessException("Ya existe otra categoría con el nombre: " + categoria.getNombre());
        }
        existente.setNombre(categoria.getNombre());
        existente.setDescripcion(categoria.getDescripcion());
        existente.setEstado(categoria.isEstado());
        return categoriaRepository.save(existente);
    }
    @Override
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }
    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }
    @Override
    public List<Categoria> findByNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }
    @Override
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", "id", id);
        }
        categoriaRepository.deleteById(id);
    }
}