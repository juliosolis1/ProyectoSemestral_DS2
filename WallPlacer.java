/*
Universidad Tecnológica de Panamá
Facultad de Ingeniería en Sistemas Computacionales
Licenciatura en Desarrollo y Gestión de Software

Asignatura - Desarrollo de Software II

Proyecto Semestral - Quoridor

Facilitador: Paulo Picota

Integrantes:
Anyeline Araúz | 8-1040-2428
Diego Hernández | 20-70-8359
Alanis Martez | 8-1019-2389
Analía Rodríguez | 8-1037-1667
Julio Solís | 8-1011-1457

Grupo: 1GS117

Fecha de entrega: 01 de diciembre de 2025
II Semestre | I Año
*/

public class WallPlacer {

    /* ===========================================================================
    Esta clase se encarga de colocar paredes (muros) en el tablero.
    
    Importante:
    - El usuario NO trabaja con índices de arreglo.
      Él ingresa:
            * Fila: 1..8
            * Columna: A..H (que en Quoridor.java se convierte a 1..8)
    
      - Aquí dentro convertimos esos valores a índices internos 0..7
        restando 1 a fila y columna.
    
      - Las paredes se representan así:
          * board.hEdges[row][col]  -> pared horizontal debajo de la celda (row, col)
          * board.vEdges[row][col]  -> pared vertical a la derecha  de la celda (row, col)
    
      - Una pared ocupa SIEMPRE 2 segmentos:
          * Horizontal: hEdges[row][col]     y hEdges[row][col + 1]
          * Vertical:   vEdges[row][col]     y vEdges[row + 1][col]
    
      - Regla anti-cruzamiento de paredes:
          No se permite que una pared horizontal y una vertical
          se crucen formando un signo "+" en la MISMA coordenada
          base (misma fila y misma columna inicial).
    =========================================================================== */

    // Referencia al tablero donde se colocarán las paredes
    private Board board;

    // Constructor: recibe el tablero y lo guarda
    public WallPlacer(Board b) {
        this.board = b;
    }

    // MÉTODO PARA COLOCAR UNA PARED HORIZONTAL
    /**
     Coloca una pared horizontal usando coordenadas “de usuario”.
     
     @param row fila ingresada por el jugador (1..8)
     @param col columna ingresada por el jugador (1..8, equivalente a A..H)
     @return true si la pared se colocó correctamente, false si hubo algún problema.
     **/
    public boolean placeHorizontalWall(int row, int col) {
        // Guardamos los valores originales solo como referencia (por si en el futuro se quieren usar en mensajes más detallados).
        int originalRow = row;
        int originalCol = col;

        // Convertimos a índices internos 0..7
        row--;
        col--;

        /* 1. Verificar que las coordenadas estén en rango.
        Las paredes horizontales se colocan ENTRE filas, por eso solo se permite fila 0..(tamaño - 2) y col 0..(tamaño - 2). */
        if (row < 0 || row >= Board.tamaño - 1 || col < 0 || col >= Board.tamaño - 1) {
            System.out.println("No se puede colocar la pared: coordenadas fuera de rango.");
            System.out.println("Recuerde: filas de 1 a 8 y columnas de A a H.");
            return false;
        }

        // 2. Verificar si ya existe una pared horizontal en cualquiera de los dos segmentos.
        if (board.hEdges[row][col] || board.hEdges[row][col + 1]) {
            System.out.println("No se puede colocar la pared: ya hay una pared horizontal en esa posición.");
            return false;
        }

        /* 3. Regla anti-cruzamiento de paredes
        
        En esta misma "coordenada base" (fila y columna inicial) una pared vertical ocuparía:
        vEdges[row][col] y vEdges[row + 1][col]
        
        Si AMBOS segmentos verticales son true, significa que ya existe una pared vertical completa en esa coordenada. Si ahora intentamos
        poner una horizontal aquí, formaríamos un "+", lo cual está prohibido. */
        if (board.vEdges[row][col] && board.vEdges[row + 1][col]) {
            System.out.println("No se puede colocar la pared horizontal en esa posición.");
            System.out.println("Ya existe una pared vertical en la misma coordenada y no se permiten cruces en '+'.");
            System.out.println("Intente colocar la pared en otra posición.");
            return false;
        }

        // 4. Si pasa todas las validaciones, colocamos la pared horizontal (ocupa dos segmentos).
        board.hEdges[row][col] = true;
        board.hEdges[row][col + 1] = true;

        return true;
    }

    // MÉTODO PARA COLOCAR UNA PARED VERTICAL
    /**
     Coloca una pared vertical usando coordenadas “de usuario”.
     @param row fila ingresada por el jugador (1..8)
     @param col columna ingresada por el jugador (1..8, equivalente a A..H)
     @return true si la pared se colocó correctamente, false si hubo algún problema.
     **/
    public boolean placeVerticalWall(int row, int col) {
        int originalRow = row;
        int originalCol = col;

        // Convertimos a índices internos 0..7
        row--;
        col--;

        /* 1. Verificar que las coordenadas estén en rango.
         Las paredes verticales se colocan ENTRE columnas, por eso solo se permite fila 0..(tamaño - 2) y col 0..(tamaño - 2).*/
        if (row < 0 || row >= Board.tamaño - 1 || col < 0 || col >= Board.tamaño - 1) {
            System.out.println("No se puede colocar la pared: coordenadas fuera de rango.");
            System.out.println("Recuerde: filas de 1 a 8 y columnas de A a H.");
            return false;
        }

        // 2. Verificar si ya existe una pared vertical en cualquiera de los dos segmentos.
        if (board.vEdges[row][col] || board.vEdges[row + 1][col]) {
            System.out.println("No se puede colocar la pared: ya hay una pared vertical en esa posición.");
            return false;
        }

        /* 3. Regla anti-cruzamiento de paredes
        
        En esta misma "coordenada base" (fila y columna inicial) una pared horizontal ocuparía:
        hEdges[row][col] y hEdges[row][col + 1]
        
        Si AMBOS segmentos horizontales son true, significa que ya existe una pared horizontal completa en esa coordenada. 
        Si ahora intentamos poner una vertical aquí, formaríamos un "+", lo cual está prohibido.*/
        if (board.hEdges[row][col] && board.hEdges[row][col + 1]) {
            System.out.println("No se puede colocar la pared vertical en esa posición.");
            System.out.println("Ya existe una pared horizontal en la misma coordenada y no se permiten cruces en '+'.");
            System.out.println("Intente colocar la pared en otra posición.");
            return false;
        }

        // 4. Si pasa todas las validaciones, colocamos la pared vertical (ocupa dos segmentos).
        board.vEdges[row][col] = true;
        board.vEdges[row + 1][col] = true;

        return true;
    }
}
