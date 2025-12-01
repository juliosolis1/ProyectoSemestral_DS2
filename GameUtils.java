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

/*
  Clase de utilidades estáticas para el juego Quoridor.
 
  Aquí se concentran funciones de apoyo que no pertenecen directamente a un solo objeto, por ejemplo:
  
  - Validar si un movimiento de ficha es permitido según:
    bordes del tablero, paredes, posición del otro jugador y reglas (sin diagonales, sin atravesar muros, etc.).
  - Detectar situaciones especiales como el empate (cuando un jugador queda completamente bloqueado).
  - Imprimir el estado de los jugadores debajo del tablero (posiciones actuales y última jugada realizada).
  - Definir constantes de colores ANSI para resaltar texto en la consola.
 
  En pocas palabras: aquí están las “herramientas” comunes que usa el main.
 */

public class GameUtils {

    /*==========================================================
    Constantes de colores ANSI para resaltar texto en consola.
    Se usan principalmente para mostrar al Personaje Rojo (PR).
    ===========================================================*/
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED   = "\u001B[31m";

    // Métodos de utilidad relacionados con la jugabilidad del juego (movimientos de jugadores y estado mostrado debajo del tablero).

    /*
     Intenta mover al jugador "actual" una casilla en la dirección WASD indicada.
     - No permite salir del tablero.
     - No permite movimientos diagonales (solo W, A, S, D).
     - No permite cruzar paredes.
     - Permite que ambos jugadores compartan la misma casilla si el movimiento es válido;
       visualmente se mostrará la ficha del jugador que tiene el turno actual.

     Devuelve true si el movimiento se realizó, false si fue inválido.
    */

    public static boolean moverJugador(Board board, Player actual, Player otro, char input) {
        char dir = Character.toLowerCase(input);
        int r = actual.getRow();
        int c = actual.getCol();

        int newR = r;
        int newC = c;

        // Determinar la nueva posición SOLO con WASD
        switch (dir) {
            case 'w': // arriba
                newR = r - 1;
                break;
            case 's': // abajo
                newR = r + 1;
                break;
            case 'a': // izquierda
                newC = c - 1;
                break;
            case 'd': // derecha
                newC = c + 1;
                break;
            default:
                // Cualquier otra tecla sería diagonal o inválida
                return false;
        }

        // 1. Verificar que se mantiene dentro del tablero
        if (newR < 0 || newR >= Board.tamaño || newC < 0 || newC >= Board.tamaño) {
            System.out.println("Movimiento fuera del tablero.");
            return false;
        }

        /* 2. Verificar si hay una pared entre la celda actual y la nueva
        Recordar:
        - hEdges[row][col] = pared horizontal debajo de la celda (row, col)
        - vEdges[row][col] = pared vertical a la derecha de la celda (row, col)*/
        switch (dir) {
            case 'w':
                // Mover hacia arriba: mirar pared horizontal ENTRE (r-1,c) y (r,c)
                if (r == 0 || board.hEdges[r - 1][c]) {
                    System.out.println("Hay una pared bloqueando el paso hacia arriba.");
                    return false;
                }
                break;
            case 's':
                // Mover hacia abajo: pared horizontal debajo de la celda actual (r,c)
                if (r == Board.tamaño - 1 || board.hEdges[r][c]) {
                    System.out.println("Hay una pared bloqueando el paso hacia abajo.");
                    return false;
                }
                break;
            case 'a':
                // Izquierda: pared vertical entre (r,c-1) y (r,c) -> vEdges[r][c-1]
                if (c == 0 || board.vEdges[r][c - 1]) {
                    System.out.println("Hay una pared bloqueando el paso hacia la izquierda.");
                    return false;
                }
                break;
            case 'd':
                // Derecha: pared vertical a la derecha de la celda actual (r,c)
                if (c == Board.tamaño - 1 || board.vEdges[r][c]) {
                    System.out.println("Hay una pared bloqueando el paso hacia la derecha.");
                    return false;
                }
                break;
        }

        // 3. Si todo es válido, actualizar la posición del jugador
        actual.setRow(newR);
        actual.setCol(newC);
        return true;
    }

     /*
     Recorre el tablero desde la posición actual de un jugador para comprobar
     si EXISTE al menos un camino de casillas válidas hasta una fila objetivo.
     
     La búsqueda:
      - Solo se mueve en las 4 direcciones básicas (W, A, S, D), sin diagonales.
      - Respeta exactamente las mismas paredes (hEdges y vEdges) que usa el método moverJugador.
      - No tiene en cuenta turnos ni quién está en la casilla destino; solo analiza si una ruta geométrica es posible.
     
     Ejemplos de uso:
      - Para el Personaje Blanco (PB), la meta es la última fila: Board.tamaño - 1.
      - Para el Personaje Rojo   (PR), la meta es la primera fila: 0.
     
     Si no se encuentra ninguna ruta, significa que el jugador está encerrado
     en una "jaula" de paredes y/o bordes sin posibilidad de llegar a su meta.
     */
    public static boolean existeCaminoHastaFilaObjetivo(Board board, Player jugador, int filaObjetivo) {

        int filas = Board.tamaño;
        int columnas = Board.tamaño;

        // Matriz para marcar qué casillas ya han sido visitadas en la búsqueda.
        boolean[][] visitado = new boolean[filas][columnas];

        // Fila y columna de inicio (posición actual del jugador).
        int inicioFila = jugador.getRow();
        int inicioColumna = jugador.getCol();

        // Cola para realizar una búsqueda en anchura (BFS).
        java.util.ArrayDeque<int[]> cola = new java.util.ArrayDeque<int[]>();
        cola.add(new int[]{inicioFila, inicioColumna});
        visitado[inicioFila][inicioColumna] = true;

        while (!cola.isEmpty()) {
            int[] actual = cola.removeFirst();
            int r = actual[0];
            int c = actual[1];

            // ¿Hemos llegado a alguna casilla de la fila objetivo?
            if (r == filaObjetivo) {
                return true;
            }

            // Intentamos expandir a las cuatro direcciones (W, A, S, D),
            // replicando la misma lógica de paredes usada en moverJugador.

            // ARRIBA (W): de (r,c) a (r-1,c)
            if (r > 0 && !visitado[r - 1][c]) {
                // Hay una pared horizontal ENTRE (r-1,c) y (r,c) en hEdges[r-1][c].
                if (!board.hEdges[r - 1][c]) {
                    visitado[r - 1][c] = true;
                    cola.add(new int[]{r - 1, c});
                }
            }

            // ABAJO (S): de (r,c) a (r+1,c)
            if (r < filas - 1 && !visitado[r + 1][c]) {
                // Hay una pared horizontal ENTRE (r,c) y (r+1,c) en hEdges[r][c].
                if (!board.hEdges[r][c]) {
                    visitado[r + 1][c] = true;
                    cola.add(new int[]{r + 1, c});
                }
            }

            // IZQUIERDA (A): de (r,c) a (r,c-1)
            if (c > 0 && !visitado[r][c - 1]) {
                // Hay una pared vertical ENTRE (r,c-1) y (r,c) en vEdges[r][c-1].
                if (!board.vEdges[r][c - 1]) {
                    visitado[r][c - 1] = true;
                    cola.add(new int[]{r, c - 1});
                }
            }

            // DERECHA (D): de (r,c) a (r,c+1)
            if (c < columnas - 1 && !visitado[r][c + 1]) {
                // Hay una pared vertical ENTRE (r,c) y (r,c+1) en vEdges[r][c].
                if (!board.vEdges[r][c]) {
                    visitado[r][c + 1] = true;
                    cola.add(new int[]{r, c + 1});
                }
            }
        }

        // Si agotamos la búsqueda sin llegar a la fila objetivo, no hay camino posible.
        return false;
    }

    /*
     Determina si la partida debe terminar en EMPATE porque uno o ambos jugadores
     han quedado encerrados en una región del tablero sin camino a su fila objetivo.
     
     Regla de decisión:
      - PB (Personaje Blanco) debe poder llegar a la última fila (Board.tamaño-1).
      - PR (Personaje Rojo) debe poder llegar a la primera fila (0).
     
     Si al menos uno de los dos jugadores NO tiene camino, la función devuelve true,
     indicando que la partida debe darse por terminada en empate.
    
     Esto cubre lógicamente escenarios como:
      - Ambos atrapados en la misma "jaula" de casillas rodeadas de paredes.
      - Un jugador pegado a un borde sin salidas válidas hacia adelante.
      - Barreras de muros que dividen el tablero en zonas incomunicadas respecto a la meta.
     */

    public static boolean hayEmpatePorEncierro(Board board, Player blanco, Player rojo) {

        int filaObjetivoBlanco = Board.tamaño - 1; // Meta del jugador blanco (parte inferior).
        int filaObjetivoRojo   = 0;                // Meta del jugador rojo   (parte superior).

        boolean blancoTieneCamino = existeCaminoHastaFilaObjetivo(board, blanco, filaObjetivoBlanco);
        boolean rojoTieneCamino   = existeCaminoHastaFilaObjetivo(board, rojo,   filaObjetivoRojo);

        // Si ambos tienen al menos un camino, la partida continúa.
        // Si uno o los dos han quedado sin camino, la partida termina en empate.
        return !blancoTieneCamino || !rojoTieneCamino;
    }

    /*
     Muestra debajo del tablero el estado actual de las posiciones de los dos jugadores y, en una línea separada, la última acción realizada
     (movimiento o colocación de una pared).
     
     Ejemplo de salida:
     Personaje Blanco (PB) ahora está en la posición 2E
     Personaje Rojo (PR) ahora está en la posición 9E
     
     Personaje Rojo (PR) colocó una pared vertical en la posición 5F
     */
    public static void imprimirEstadoJugadores(Player blanco, Player rojo, String descripcionUltimaAccion) {

        // Convertimos las coordenadas internas (0..8) a las que ve el usuario (1..9, A..I).
        int filaBlanco = blanco.getRow() + 1;
        char colBlanco = (char) ('A' + blanco.getCol());

        int filaRojo = rojo.getRow() + 1;
        char colRojo = (char) ('A' + rojo.getCol());

        // Primera línea: posición del personaje blanco.
        System.out.println("Personaje Blanco (PB) ahora está en la posición " + filaBlanco + colBlanco);

        // Segunda línea: posición del personaje rojo (mostrado en color rojo).
        String etiquetaRojo = "Personaje Rojo (PR)";
        // Aplicamos color rojo solo al texto del personaje, usando ANSI.
        etiquetaRojo = ANSI_RED + etiquetaRojo + ANSI_RESET;
        System.out.println(etiquetaRojo + " ahora está en la posición " + filaRojo + colRojo);
        // Línea en blanco para separar visualmente las posiciones de la última jugada.
        System.out.println();

        // Línea en blanco para separar visualmente las posiciones de la última jugada.
        System.out.println();

        // Tercera línea: última jugada realizada en la partida.
        if (descripcionUltimaAccion != null && !descripcionUltimaAccion.isEmpty()) {
          System.out.println(descripcionUltimaAccion);
        }
    }
}