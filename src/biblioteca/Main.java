
package biblioteca;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== SISTEMA DE RESERVAS DE BIBLIOTECA ===");

        // Crear estudiante
        Estudiante estudiante = new Estudiante("1001", "Ana");

        // Crear repositorios
        RepositorioSalasMemoria repositorioSalas =
                new RepositorioSalasMemoria();

        RepositorioReservasMemoria repositorioReservas =
                new RepositorioReservasMemoria();

        // Crear observadores
        List<ObservadorReserva> observadores = List.of(
                new NotificadorEstudiante(),
                new RegistroEventos()
        );

        // Crear fachada
        ReservaFacade facade = new ReservaFacade(
                repositorioSalas,
                repositorioReservas,
                observadores
        );

        // Mostrar salas disponibles
        System.out.println("\n--- SALAS DISPONIBLES ---");

        for (Sala sala : repositorioSalas.obtenerDisponibles()) {
            System.out.println(sala);
        }

        // PRUEBA 1: Seleccion por capacidad
        System.out.println("\n--- PRUEBA 1: SELECCION POR CAPACIDAD ---");

        Optional<Sala> salaCapacidad =
                new SeleccionPorCapacidad().seleccionar(
                        repositorioSalas.obtenerDisponibles(), 4);

        System.out.println("Sala seleccionada: "
                + salaCapacidad.orElse(null));

        // PRUEBA 2: Sala preferida
        System.out.println("\n--- PRUEBA 2: SALA PREFERIDA ---");

        Optional<Sala> salaPreferida =
                new SeleccionSalaPreferida("C").seleccionar(
                        repositorioSalas.obtenerDisponibles(), 4);

        System.out.println("Sala seleccionada: "
                + salaPreferida.orElse(null));

        // PRUEBA 3: Capacidad insuficiente
        System.out.println("\n--- PRUEBA 3: CAPACIDAD INSUFICIENTE ---");

        Optional<Sala> salaInsuficiente =
                new SeleccionSalaPreferida("B").seleccionar(
                        repositorioSalas.obtenerDisponibles(), 7);

        System.out.println("Resultado esperado: vacio");
        System.out.println("Resultado obtenido: " + salaInsuficiente);

        // PRUEBA 4: Builder invalido
        System.out.println("\n--- PRUEBA 4: BUILDER INVALIDO ---");

        try {
            new ReservaBuilder().build();
            System.out.println("ERROR: se aceptaron datos incompletos.");
        } catch (IllegalStateException e) {
            System.out.println("Validacion correcta: " + e.getMessage());
        }

        // PRUEBA 5: Crear reserva con Facade
        System.out.println("\n--- PRUEBA 5: CREAR RESERVA ---");

        Optional<Reserva> resultado = facade.realizarReserva(
                estudiante,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                2,
                4,
                new SeleccionPorCapacidad()
        );

        if (resultado.isPresent()) {

            Reserva reserva = resultado.get();

            System.out.println("Reserva creada: " + reserva);
            System.out.println("Estado actual: "
                    + reserva.getNombreEstado());

            // PRUEBA 6: Confirmar otra vez
            System.out.println("\n--- PRUEBA 6: CONFIRMAR OTRA VEZ ---");

            try {
                reserva.confirmar();
                System.out.println("ERROR: se permitio confirmar dos veces.");
            } catch (IllegalStateException e) {
                System.out.println("Validacion correcta: " + e.getMessage());
            }

            // PRUEBA 7: Cancelar reserva
            System.out.println("\n--- PRUEBA 7: CANCELAR ---");

            reserva.cancelar();

            System.out.println("Estado actual: "
                    + reserva.getNombreEstado());

            // PRUEBA 8: Finalizar una cancelada
            System.out.println("\n--- PRUEBA 8: FINALIZAR CANCELADA ---");

            try {
                reserva.finalizar();
                System.out.println("ERROR: se permitio finalizar.");
            } catch (IllegalStateException e) {
                System.out.println("Validacion correcta: " + e.getMessage());
            }
        }

        // PRUEBA 9: Segunda reserva
        System.out.println("\n--- PRUEBA 9: SEGUNDA RESERVA ---");

        Optional<Reserva> segunda = facade.realizarReserva(
                estudiante,
                LocalDate.now().plusDays(2),
                LocalTime.of(14, 0),
                1,
                2,
                new SeleccionSalaPreferida("A")
        );

        if (segunda.isPresent()) {

            Reserva reserva2 = segunda.get();

            System.out.println("Reserva creada: " + reserva2);

            // PRUEBA 10: Finalizar reserva confirmada
            System.out.println("\n--- PRUEBA 10: FINALIZAR ---");

            reserva2.finalizar();

            System.out.println("Estado final: "
                    + reserva2.getNombreEstado());
        }

        // PRUEBA 11: Revisar repositorio
        System.out.println("\n--- PRUEBA 11: REPOSITORIO ---");

        System.out.println("Total de reservas guardadas: "
                + repositorioReservas.obtenerTodas().size());

        for (Reserva reserva : repositorioReservas.obtenerTodas()) {
            System.out.println(reserva);
        }

        // P02: Construccion sin estudiante
        System.out.println("\n--- P02: SIN ESTUDIANTE ---");

        try {
            new ReservaBuilder()
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(LocalTime.of(9, 0))
                    .duracionHoras(1)
                    .cantidadPersonas(2)
                    .sala(repositorioSalas.obtenerDisponibles().get(0))
                    .build();

            System.out.println("P02 FALLIDA");

        } catch (IllegalStateException e) {
            System.out.println("P02 APROBADA: " + e.getMessage());
        }

        // P09: Confirmar una reserva cancelada
        System.out.println("\n--- P09: CONFIRMAR CANCELADA ---");

        if (resultado.isPresent()) {

            Reserva reservaCancelada = resultado.get();

            try {
                reservaCancelada.confirmar();
                System.out.println("P09 FALLIDA");

            } catch (IllegalStateException e) {
                System.out.println("P09 APROBADA: " + e.getMessage());
                System.out.println("Estado: "
                        + reservaCancelada.getNombreEstado());
            }

        } else {
            System.out.println("P09 NO EJECUTADA: no se creo la reserva.");
        }

        // P10: Solicitud para 10 personas
        System.out.println("\n--- P10: DIEZ PERSONAS ---");

        int antes = repositorioReservas.obtenerTodas().size();

        Optional<Reserva> prueba10 = facade.realizarReserva(
                estudiante,
                LocalDate.now().plusDays(3),
                LocalTime.of(10, 0),
                2,
                10,
                new SeleccionPorCapacidad()
        );

        int despues = repositorioReservas.obtenerTodas().size();

        System.out.println("Reservas antes: " + antes);
        System.out.println("Reservas despues: " + despues);

        if (prueba10.isEmpty() && antes == despues) {
            System.out.println("P10 APROBADA: no se creo la reserva.");
        } else {
            System.out.println("P10 FALLIDA");
        }

        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }
}
