package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.ProductoResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ProductoResponseDTO create(ProductoRequestDTO requestDTO);
    ProductoResponseDTO update(Long id, ProductoRequestDTO requestDTO);
    Optional<ProductoResponseDTO> findById(Long id);
    List<ProductoResponseDTO> findAll();
    List<ProductoResponseDTO> findByNombre(String nombre);
    void delete(Long id);
}
