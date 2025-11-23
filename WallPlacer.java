public class WallPlacer {

    // Referencia al tablero donde se colocarán las paredes
    Board board;

    // Constructor: recibe el tablero y lo guarda
    public WallPlacer(Board b) {
        this.board = b;
    }

    // ----------------------------------------------------------
    // MÉTODO PARA COLOCAR UNA PARED HORIZONTAL
    // ----------------------------------------------------------
    public boolean placeHorizontalWall(int row, int col) {

        // 1. Verificar que la fila esté dentro de los límites válidos.
        // Las paredes horizontales se colocan ENTRE filas, por eso solo se permite hasta tamaño - 2.
        if (row < 0 || row >= Board.tamaño - 1) return false;

        // 2. Verificar que la columna esté en rango.
        // Como la pared horizontal ocupa 2 columnas, col solo puede llegar hasta tamaño - 2.
        if (col < 0 || col >= Board.tamaño - 1) return false;

        // 3. Verificar si ya existe una pared horizontal en cualquiera de los dos segmentos.
        // Como la pared cubre 2 espacios, ambas celdas deben estar libres.
        if (board.hEdges[row][col] || board.hEdges[row][col + 1]) return false;

        // 4. Colocar la pared horizontal: marca 2 posiciones como true
        board.hEdges[row][col] = true;
        board.hEdges[row][col + 1] = true;

        // 5. La colocación fue exitosa
        return true;
    }

    // ----------------------------------------------------------
    // MÉTODO PARA COLOCAR UNA PARED VERTICAL
    // ----------------------------------------------------------
    public boolean placeVerticalWall(int row, int col) {

        // 1. Verificar que la fila esté en el rango correcto.
        // Como la pared vertical ocupa 2 filas, solo se permite hasta tamaño - 2.
        if (row < 0 || row >= Board.tamaño - 1) return false;

        // 2. Verificar que la columna esté dentro del rango válido.
        if (col < 0 || col >= Board.tamaño - 1) return false;

        // 3. Verificar si ya hay pared vertical en la posición actual o en la siguiente.
        // La pared vertical también ocupa 2 segmentos.
        if (board.vEdges[row][col] || board.vEdges[row + 1][col]) return false;

        // 4. Colocar ambos segmentos verticales
        board.vEdges[row][col] = true;
        board.vEdges[row + 1][col] = true;

        // 5. Colocación exitosa
        return true;
    }
}
