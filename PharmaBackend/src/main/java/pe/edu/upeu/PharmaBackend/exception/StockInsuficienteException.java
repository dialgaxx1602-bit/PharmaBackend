package pe.edu.upeu.PharmaBackend.exception;

public class StockInsuficienteException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String nombreProducto, Integer stockDisponible, Integer cantidadSolicitada) {
        super(String.format("Stock insuficiente para el producto '%s'. Disponible: %d, Solicitado: %d",
                nombreProducto, stockDisponible, cantidadSolicitada));
    }
}
