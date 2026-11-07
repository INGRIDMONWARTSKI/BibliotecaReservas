package biblioteca;

public class RegistroEventos implements ObservadorReserva {

    @Override
    public void actualizar(Reserva reserva,
                           String estadoAnterior,
                           String estadoNuevo) {

        System.out.println(
            "Evento: reserva " + reserva.getCodigo()
            + " cambio de " + estadoAnterior
            + " a " + estadoNuevo);
    }
}