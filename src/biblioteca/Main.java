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

        // Mostrar salas
        System.out.println("\n--- SALAS DISPONIBLES ---");

        for (Sala sala : repositorioSalas.obtenerDisponibles()) {
            System.out.println(sala);
        }

        // Prueba 1: Strategy por capacidad
        System.out.println("\n--- PRUEBA 1: SELECCION POR CAPACIDAD ---");

        Optional<Sala> salaCapacidad =
                new SeleccionPorCapacidad().seleccionar(
                        repositorioSalas.obtenerDisponibles(), 4);

        System.out.println("Sala seleccionada: "
                + salaCapacidad.orElse(null));

        // Prueba 2: Strategy sala preferida
        System.out.println("\n--- PRUEBA 2: SALA PREFERIDA ---");

        Optional<Sala> salaPreferida =
                new SeleccionSalaPreferida("C").seleccionar(
                        repositorioSalas.obtenerDisponibles(), 4);

        System.out.println("Sala seleccionada: "
                + salaPreferida.orElse(null));

        // Prueba 3: Sala insuficiente
        System.out.println("\n--- PRUEBA 3: CAPACIDAD INSUFICIENTE ---");

        Optional<Sala> salaInsuficiente =
                new SeleccionSalaPreferida("B").seleccionar(
                        repositorioSalas.obtenerDisponibles(), 7);

        System.out.println("Resultado esperado: vacio");
        System.out.println("Resultado obtenido: " + salaInsuficiente);

        // Prueba 4: Builder con datos incompletos
        System.out.println("\n--- PRUEBA 4: BUILDER INVALIDO ---");

        try {
            new ReservaBuilder().build();
            System.out.println("ERROR: se aceptaron datos incompletos.");
        } catch (IllegalStateException e) {
            System.out.println("Validacion correcta: " + e.getMessage());
        }

        // Prueba 5: Crear reserva con Facade
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

            // Prueba 6: Intentar confirmar nuevamente
            System.out.println("\n--- PRUEBA 6: CONFIRMAR OTRA VEZ ---");

            try {
                reserva.confirmar();
                System.out.println("ERROR: se permitio confirmar dos veces.");
            } catch (IllegalStateException e) {
                System.out.println("Validacion correcta: " + e.getMessage());
            }

            // Prueba 7: Cancelar reserva
            System.out.println("\n--- PRUEBA 7: CANCELAR ---");

            reserva.cancelar();

            System.out.println("Estado actual: "
                    + reserva.getNombreEstado());

            // Prueba 8: Intentar finalizar una cancelada
            System.out.println("\n--- PRUEBA 8: FINALIZAR CANCELADA ---");

            try {
                reserva.finalizar();
                System.out.println("ERROR: se permitio finalizar.");
            } catch (IllegalStateException e) {
                System.out.println("Validacion correcta: " + e.getMessage());
            }
        }

        // Prueba 9: Crear segunda reserva
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

            // Prueba 10: Finalizar reserva confirmada
            System.out.println("\n--- PRUEBA 10: FINALIZAR ---");

            reserva2.finalizar();

            System.out.println("Estado final: "
                    + reserva2.getNombreEstado());
        }

        // Prueba 11: Revisar reservas guardadas
        System.out.println("\n--- PRUEBA 11: REPOSITORIO ---");

        System.out.println("Total de reservas guardadas: "
                + repositorioReservas.obtenerTodas().size());

        for (Reserva reserva : repositorioReservas.obtenerTodas()) {
            System.out.println(reserva);
        }

        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }
}