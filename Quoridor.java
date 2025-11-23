import java.util.Scanner;

public class Quoridor {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Se crea el tablero del juego (contiene celdas y paredes)
        Board board = new Board();

        // Objeto encargado de colocar paredes en el tablero
        WallPlacer placer = new WallPlacer(board);

        System.out.println("=== QUORIDOR ===");

        // Mostrar el tablero por primera vez
        board.printBoard();

        int cantwall = 10; // Cantidad de paredes disponibles

        while (true) {
            System.out.println("\nColocar pared:");
            System.out.println("Formato: H fila col  /  V fila col");
            System.out.println("Ejemplos: H 3 4    V 5 2");
            System.out.print("Introduzca las coordenadas de la pared: ");

            try {

                // Leer tipo de pared (H o V)
                char type = sc.next().toUpperCase().charAt(0);

                // Leer coordenadas fila y columna
                int row = sc.nextInt();
                int col = sc.nextInt();

                boolean ok = false; // Se usará para saber si la colocación fue válida

                // Verificar si aún quedan paredes disponibles
                if (cantwall > 0) {

                    // Pared horizontal
                    if (type == 'H') {
                        ok = placer.placeHorizontalWall(row, col);

                    // Pared vertical
                    } else if (type == 'V') {
                        ok = placer.placeVerticalWall(row, col);

                    // Entrada inválida
                    } else {
                        System.out.println("❌ Tipo inválido. Use H o V.");
                        continue; // Regresa al inicio del ciclo
                    }

                    // Verificar si el método pudo colocar la pared
                    if (!ok) {
                        System.out.println("❌ No se puede colocar la pared ahí.");
                    } else {
                        System.out.println("✔ Pared colocada.");
                        cantwall--; // Restar una pared usada
                        System.out.println("Paredes restantes: " + cantwall);
                    }

                } else {
                    System.out.println("❌ Paredes insuficientes");
                }

            } catch (Exception e) {
                // En caso de que el usuario escriba algo inválido
                System.out.println(e.getMessage());
                System.out.println("⚠ Error en la lectura. Intente de nuevo.");
                sc.nextLine(); // Limpia el buffer
            }

            // Mostrar el tablero siempre después de intentar colocar una pared
            board.printBoard();
        }
    }
}
