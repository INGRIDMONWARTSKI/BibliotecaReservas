package biblioteca;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Sala> salas = List.of(
            new Sala("A", "Sala A", 2),
            new Sala("B", "Sala B", 4),
            new Sala("C", "Sala C", 6),
            new Sala("D", "Sala D", 8)
        );

        System.out.println("Salas de la biblioteca:");
        salas.forEach(System.out::println);
        EstrategiaSeleccionSala porCapacidad = new SeleccionPorCapacidad();

System.out.println("Para 4 personas: "
        + porCapacidad.seleccionar(salas, 4).orElse(null));

EstrategiaSeleccionSala preferida = new SeleccionSalaPreferida("C");

System.out.println("Preferida C: "
        + preferida.seleccionar(salas, 4).orElse(null));

EstrategiaSeleccionSala preferidaNoAdecuada =
        new SeleccionSalaPreferida("B");

System.out.println("Preferida B para 7: "
        + preferidaNoAdecuada.seleccionar(salas, 7).orElse(null));
    }
}