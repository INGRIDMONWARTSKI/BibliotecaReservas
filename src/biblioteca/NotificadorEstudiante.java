package biblioteca;

public class NotificadorEstudiante implements ObservadorReserva {

    @Override
    public void actualizar(Reserva reserva,
                           String estadoAnterior,
                           String estadoNuevo) {

        System.out.println(
            "Notificacion para "
            + reserva.getEstudiante().getNombre()
            + ": la reserva " + reserva.getCodigo()
            + " cambio de " + estadoAnterior
            + " a " + estadoNuevo);
    }
}