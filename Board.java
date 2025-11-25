public class Board {

    // Tamaño del tablero 9x9
    public static final int tamaño = 9;
    public int numcol = 0;

    // Matriz de paredes horizontales
    // hEdges[row][col] = true significa "hay una pared horizontal debajo de esta celda"
    boolean[][] hEdges = new boolean[tamaño - 1][tamaño];

    // Matriz de paredes verticales
    // vEdges[row][col] = true significa "hay una pared vertical a la derecha de esta celda"
    boolean[][] vEdges = new boolean[tamaño][tamaño - 1];

    // Matriz del tablero visible con símbolos como '*'
    char[][] cells = new char[tamaño][tamaño];

    public Board() {

        // Inicializar todas las celdas con '*'
        for (int r = 0; r < tamaño; r++)
            for (int c = 0; c < tamaño; c++)
                cells[r][c] = '*';
    }

    // Imprime el tablero con paredes y celdas
public void printBoard() {
    System.out.println();
    System.out.println("    1   2   3   4   5   6   7   8   9");

    for (int r = 0; r < tamaño; r++) {

        // Print row number
        System.out.print((r + 1) + "   ");

        // Print cells + vertical walls
        for (int c = 0; c < tamaño; c++) {

            System.out.print(" " + cells[r][c] + " ");

            if (c < tamaño - 1) {
                // Correct vertical alignment
                System.out.print(vEdges[r][c] ? "|" : " ");
            }
        }
        System.out.println();

        // Print horizontal walls
        if (r < tamaño - 1) {

            // Left offset
            System.out.print("    ");

            for (int c = 0; c < tamaño; c++) {

                // Correct horizontal alignment: 3 chars, not 4
                System.out.print(hEdges[r][c] ? "---" : "   ");

                // Add spacing between columns **except last**
                if (c < tamaño - 1) System.out.print(" ");
            }
            System.out.println();
        }
    }
}
}