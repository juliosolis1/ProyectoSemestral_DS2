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

public class GameUtils {

    /*==========================================================
    Constantes de colores ANSI para resaltar texto en consola.
    Se usan principalmente para mostrar al Personaje Rojo (PR).
    ===========================================================*/
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED   = "\u001B[31m";

    // Métodos de utilidad relacionados con la jugabilidad del juego (movimientos de jugadores y estado mostrado debajo del tablero).

    /*
     Intenta mover al jugador 'actual' una casilla en la dirección WASD indicada.
     - No permite salir del tablero.
     - No permite movimientos diagonales (solo W, A, S, D).
     - No permite cruzar paredes.
     - No permite entrar en la casilla del otro jugador.
     
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

        // 3. No permitir ocupar la misma casilla que el otro jugador
        if (newR == otro.getRow() && newC == otro.getCol()) {
            System.out.println("No puedes entrar en la casilla del otro jugador.");
            return false;
        }

        // 4. Si todo es válido, actualizar la posición del jugador
        actual.setRow(newR);
        actual.setCol(newC);
        return true;
    }

    /*
     Muestra debajo del tablero el estado actual de las posiciones de los dos jugadores y, en una línea separada, la última acción realizada
     (movimiento o colocación de una pared).
     
     Ejemplo de salida:
     Personaje Blanco (PB) ahora está en la posición 2E
     Personaje Rojo (PR) ahora está en la posición 9E
     
     Personaje Rojo (PR) colocó una pared vertical en la posición 5F
     */
    public static void imprimirEstadoJugadores(Player blanco,
                                               Player rojo,
                                               String descripcionUltimaAccion) {

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
