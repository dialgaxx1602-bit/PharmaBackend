package pe.edu.upeu.PharmaBackend.service.generic;
import java.util.Optional;
public interface CrudService <T, ID> {
    T create(T t);
    T update(T t);
    Optional<T> findById(ID id);
    void deleteById(ID id);
    Iterable<T> readAll();
}
