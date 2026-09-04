package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    ClienteDTO create(ClienteDTO clienteDTO);

    Optional<ClienteDTO> findById(Long id);

    List<ClienteDTO> findAll();

    ClienteDTO update(Long id, ClienteDTO clienteDTO);

    void delete(Long id);
}
