package pe.edu.upeu.PharmaBackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import pe.edu.upeu.PharmaBackend.exception.ResourceNotFoundException;
import pe.edu.upeu.PharmaBackend.mapper.ProductoMapper;
import pe.edu.upeu.PharmaBackend.repository.CategoriaRepository;
import pe.edu.upeu.PharmaBackend.repository.ProductoRepository;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO requestDTO) {
        log.info("Iniciando registro de producto: '{}' con categoriaId: {}", requestDTO.getNombre(), requestDTO.getCategoriaId());
        
        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria no encontrada con ID: {}. Lanzando ResourceNotFoundException.", requestDTO.getCategoriaId());
                    return new ResourceNotFoundException("Categoria", "id", requestDTO.getCategoriaId());
                });

        Producto producto = productoMapper.toEntity(requestDTO, categoria);
        Producto guardado = productoRepository.save(producto);
        log.info("Producto registrado exitosamente con ID: {}, categoria: '{}'", guardado.getId(), categoria.getNombre());
        return productoMapper.toResponseDTO(guardado);
    }

    @Override
    public ProductoResponseDTO update(Long id, ProductoRequestDTO requestDTO) {
        log.info("Actualizando producto ID: {} con categoriaId: {}", id, requestDTO.getCategoriaId());
        
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Producto", "id", id);
                });

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria no encontrada con ID: {}", requestDTO.getCategoriaId());
                    return new ResourceNotFoundException("Categoria", "id", requestDTO.getCategoriaId());
                });

        productoExistente.setNombre(requestDTO.getNombre());
        productoExistente.setPrecio(requestDTO.getPrecio());
        productoExistente.setStock(requestDTO.getStock());
        productoExistente.setCategoria(categoria);

        Producto actualizado = productoRepository.save(productoExistente);
        log.info("Producto ID: {} actualizado exitosamente", actualizado.getId());
        return productoMapper.toResponseDTO(actualizado);
    }

    @Override
    public Optional<ProductoResponseDTO> findById(Long id) {
        log.info("Buscando producto por ID: {}", id);
        return productoRepository.findById(id)
                .map(productoMapper::toResponseDTO);
    }

    @Override
    public List<ProductoResponseDTO> findAll() {
        log.info("Obteniendo todos los productos");
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> findByNombre(String nombre) {
        log.info("Buscando productos por nombre similar a: '{}'", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (!productoRepository.existsById(id)) {
            log.warn("No se pudo eliminar: Producto no encontrado con ID: {}", id);
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productoRepository.deleteById(id);
        log.info("Producto ID: {} eliminado exitosamente", id);
    }
}
