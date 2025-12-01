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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/*
 Clase principal del juego Quoridor.

 Responsabilidades:
 - Crear el tablero y los dos jugadores (Personaje Blanco y Personaje Rojo).
 - Controlar el ciclo de turnos hasta que alguien gane, haya empate o se escriba EXIT.
 - Leer desde teclado las acciones del usuario (mover ficha o colocar pared).
 - Coordinar el uso de las clases Board, WallPlacer, MoveHistory y GameUtils para ejecutar las reglas del juego.

 En resumen: aquí se encuentra el método main y toda la lógica de “orquestación” de la partida que se ve en la consola.
 */

public class Quoridor {

    public static void main(String[] args) {
        // BufferedReader se utiliza para leer texto desde la consola (teclado).
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Crear tablero y helper para paredes
        Board board = new Board();
        WallPlacer placer = new WallPlacer(board);

        // Crear personajes: PB empieza en la fila superior, en el centrol; PR empieza en la fila inferior, en el centro.
        Player blanco = new Player("Personaje Blanco", "PB", 0, Board.tamaño / 2, 10);
        Player rojo   = new Player("Personaje Rojo",   "PR", Board.tamaño - 1, Board.tamaño / 2, 10);

        System.out.println("              === QUORIDOR ===");
        System.out.println("-------------------------------------------");
        System.out.println("PB: Personaje Blanco (empieza arriba)");
        System.out.println("PR: Personaje Rojo (empieza abajo)");
        System.out.println("Cada jugador dispone de 10 muros.");
        System.out.println();
        System.out.println("Controles de movimiento:");
        System.out.println("  W: arriba");
        System.out.println("  S: abajo");
        System.out.println("  A: izquierda");
        System.out.println("  D: derecha");
        System.out.println("-------------------------------------------");
        System.out.println("Durante tu turno, puedes:");
        System.out.println("  M: mover tu personaje");
        System.out.println("  P: colocar una pared");
        System.out.println("EXIT: salir de la partida");
        System.out.println("-------------------------------------------");
        System.out.println();

        // Historial de movimientos
        MoveHistory historial = new MoveHistory();

        // Estado de la partida
        boolean partidaActiva = true;
        boolean turnoBlanco   = true;
        Player ganador = null;

        // Descripción de la última acción relevante (movimiento o pared) que mostraremos debajo del tablero en cada turno.
        String descripcionUltimaAccion = "";

        while (partidaActiva) {
            Player actual = turnoBlanco ? blanco : rojo;
            Player otro   = turnoBlanco ? rojo   : blanco;

            // Mostrar tablero con las posiciones actuales
            board.printBoard(blanco, rojo, actual);

            /*
              Debajo del tablero mostramos un resumen de las posiciones y la última jugada:
               - Primera línea: posición actual del Personaje Blanco
               - Segunda línea: posición actual del Personaje Rojo
               - Tercera línea: texto que describe la última jugada realizada
             
              La variable 'descripcionUltimaAccion' se va actualizando tras cada movimiento o colocación de pared 
              para que siempre muestre lo último que ocurrió.
             */
            GameUtils.imprimirEstadoJugadores(blanco, rojo, descripcionUltimaAccion);

            // Indicar de quién es el turno y cuántos muros le quedan.
            String simboloActual = actual.getSymbol();
            String etiquetaTurno;

            // Si es el Personaje Rojo, mostramos su símbolo en rojo usando códigos ANSI.
            if ("PR".equals(simboloActual)) {
                etiquetaTurno = "Turno de " + actual.getName()
                        + " [" + GameUtils.ANSI_RED + simboloActual + GameUtils.ANSI_RESET + "]";
            } else {
                etiquetaTurno = "Turno de " + actual.getName()
                        + " [" + simboloActual + "]";
            }

            System.out.println(etiquetaTurno);
            System.out.println("Muros restantes: " + actual.getWallsRemaining());
            System.out.print("¿Qué desea hacer? (M = mover, P = pared, EXIT = salir): ");
            System.out.println();

            try {
                // Leemos la acción principal del turno usando BufferedReader. El jugador puede escribir M, P o EXIT (en mayúscula o minúscula).
                String lineaAccion = br.readLine();
                if (lineaAccion == null) {
                    System.out.println("No se pudo leer la acción. Intente de nuevo.");
                    continue;
                }
                String accion = lineaAccion.trim().toUpperCase();

                if (accion.equals("EXIT")) {

                    // Registramos que este jugador decidió salir de la partida (EXIT).
                    historial.registrarMovimiento(actual, "EXIT");

                    System.out.println("PARTIDA INTERRUMPIDA POR EL USUARIO.");
                    break;
                }

                if (accion.equals("M")) {
                    // ------------------------------------------------
                    // OPCIÓN: MOVER AL JUGADOR (W, A, S, D)
                    // ------------------------------------------------
                    System.out.print("Dirección (W = arriba, S = abajo, A = izquierda, D = derecha): ");

                    // Leemos la dirección como una línea completa y tomamos el primer carácter.
                    String dirStr = br.readLine();
                    if (dirStr == null || dirStr.trim().isEmpty()) {
                        System.out.println("Dirección inválida. Intente de nuevo.");
                        continue;
                    }
                    char dir = Character.toUpperCase(dirStr.trim().charAt(0));

                    boolean movOk = GameUtils.moverJugador(board, actual, otro, dir);
                    if (!movOk) {
                        System.out.println("Movimiento inválido. Intente de nuevo.");
                        continue; // No se cambia el turno
                    }

                    /* ===========================================================================
                    Si el movimiento fue válido, lo registramos en el historial con el formato:
                    M (S) - 2E

                    Donde:
                    - M  -> acción de mover
                    - (S)-> dirección presionada por el jugador
                    - 2E -> posición final del jugador después del movimiento
                    =========================================================================== */

                    int filaFinal = actual.getRow() + 1;              // fila 0..8 -> 1..9
                    char colFinal = (char) ('A' + actual.getCol());   // col  0..8 -> A..I

                    String movimiento = "M (" + dir + ") - " + filaFinal + colFinal;
                    historial.registrarMovimiento(actual, movimiento);

                    /* Actualizamos la descripción de la última acción para que el mensaje debajo del tablero 
                    indique la nueva posición del jugador que se acaba de mover. */
                    descripcionUltimaAccion = actual.getName()
                            + " (" + actual.getSymbol() + ") ahora está en la posición "
                            + filaFinal + colFinal;

                    // Verificar primero la condición de victoria clásica:
                    // - Blanco gana si llega a la última fila (abajo).
                    // - Rojo   gana si llega a la primera fila (arriba).
                    if (actual == blanco && actual.getRow() == Board.tamaño - 1) {
                        ganador = actual;
                        partidaActiva = false;
                    } else if (actual == rojo && actual.getRow() == 0) {
                        ganador = actual;
                        partidaActiva = false;
                    } else {
                        /* ==========================================================
                         Si nadie ha ganado todavía, verificamos si la configuración
                         actual del tablero deja a alguno de los jugadores encerrado
                         en una región de casillas sin camino posible hacia su meta.
                         
                          - Para PB (blanco) la meta es la última fila (abajo).
                          - Para PR (rojo)   la meta es la primera fila (arriba).
                         
                         GameUtils.hayEmpatePorEncierro(...) devolverá true cuando
                         al menos uno de los dos ya no tenga NINGÚN camino válido
                         hasta su fila objetivo, aunque aún pueda moverse dentro
                         de una "jaula" pequeña de casillas.
                         ========================================================== */
                        boolean empatePorEncierro = GameUtils.hayEmpatePorEncierro(board, blanco, rojo);

                        if (empatePorEncierro) {
                            // No hay ganador, pero el tablero ha quedado bloqueado.
                            // Dejamos ganador = null para que MoveHistory imprima
                            // "NADIE GANÓ LA PARTIDA" al final.
                            partidaActiva = false;
                        } else {
                            // Ningún jugador está encerrado: se cambia el turno.
                            turnoBlanco = !turnoBlanco;
                        }
                    }

                } else if (accion.equals("P")) {

                    // ------------------------------------------------
                    // OPCIÓN: COLOCAR UNA PARED (HORIZONTAL O VERTICAL)
                    // ------------------------------------------------

                    // 1. Verificamos que todavía tenga muros disponibles.
                    if (actual.getWallsRemaining() <= 0) {
                        System.out.println("No te quedan muros para colocar.");
                        continue;
                    }

                    /* ================================================
                    FORMATO DE LAS COORDENADAS PARA PAREDES

                    El tablero tiene 9 columnas de casillas (A..I),
                    pero las paredes se colocan ENTRE casillas.

                    Por eso, para muros solo hay 8 opciones de columna:
                    A, B, C, D, E, F, G, H

                    Ejemplos de coordenadas:
                    - H 3 C  → pared horizontal en la fila 3, entre las columnas C y D.
                    - V 5 F  → pared vertical en la fila 5, entre las filas 5 y 6 sobre la columna F.

                    En el código pedimos los datos por separado:
                    1) Tipo de pared (H/V)
                    2) Fila (1..8)
                    3) Columna inicial (A..H)
                    =================================================== */

                    System.out.println("Colocar pared (Ejemplos: H 3 C  o  V 5 F)");

                    // Tipo de pared
                    System.out.print("Tipo de pared (H = horizontal, V = vertical): ");
                    String tipoStr = br.readLine();
                    if (tipoStr == null || tipoStr.trim().isEmpty()) {
                        System.out.println("Tipo inválido. Use H o V.");
                        continue;
                    }
                    char tipo = Character.toUpperCase(tipoStr.trim().charAt(0));

                    // Fila: siempre numérica, de 1 a 8
                    System.out.print("Fila (1-8): ");
                    int fila = Integer.parseInt(br.readLine().trim());

                    // Columna: siempre letras, de A hacia H
                    System.out.print("Columna inicial (A-H): ");
                    String colStr = br.readLine();
                    if (colStr == null || colStr.trim().isEmpty()) {
                        System.out.println("Columna inválida. Use letras de la A a la H.");
                        continue;
                    }
                    char letraColumna = Character.toUpperCase(colStr.trim().charAt(0));

                    // Validamos que la letra esté en el rango permitido.
                    if (letraColumna < 'A' || letraColumna > 'H') {
                        System.out.println("Columna inválida. Use letras de la A a la H.");
                        continue; // No intentamos colocar la pared
                    }

                    /*Convertimos la letra de columna a un número 1..8 para poder llamar a WallPlacer:
                    A → 1
                    B → 2
                    ...
                    H → 8
                    WallPlacer se encarga de transformar esto a los índices 0..7 internos (restando 1).*/

                    int col = (letraColumna - 'A') + 1;

                    // Intentamos colocar la pared llamando a WallPlacer, que valida que no haya choques con otras paredes ni con el borde.
                    boolean ok;
                    if (tipo == 'H') {
                        // Pared horizontal: se dibuja entre la fila "fila" y "fila+1" ocupando dos casillas en X.
                        ok = placer.placeHorizontalWall(fila, col);
                    } else if (tipo == 'V') {
                        // Pared vertical: se dibuja entre la columna "col" y la columna "col+1", ocupando dos casillas en Y.
                        ok = placer.placeVerticalWall(fila, col);
                    } else {
                        System.out.println("Tipo inválido. Use H o V.");
                        continue;
                    }

                    // Si no se pudo colocar (por choque o fuera de rango), mostramos un mensaje y no cambiamos de turno.
                    if (!ok) {
                        System.out.println("No se puede colocar la pared en esa posición.");
                        continue;
                    }

                    // Si llegamos aquí, la pared se colocó correctamente.
                    // Actualizamos la cantidad de muros restantes para el jugador actual.
                    actual.setWallsRemaining(actual.getWallsRemaining() - 1);
                    System.out.println("Pared colocada. Te quedan "
                            + actual.getWallsRemaining() + " muros.");

                    /* ==================================================
                    Registramos la colocación de la pared con el formato:

                    P (H 2 E)
                    P (V 5 F)

                    Donde:
                    - P    -> acción de pared
                    - H/V  -> tipo de pared (horizontal o vertical)
                    - 2    -> fila ingresada por el usuario (1..8)
                    - E    -> columna ingresada por el usuario (A..H)
                    ====================================================== */

                    String movimientoPared = "P (" + tipo + " " + fila + " " + letraColumna + ")";
                    historial.registrarMovimiento(actual, movimientoPared);

                    // Actualizamos la descripción de la última acción para indicar qué jugador colocó qué tipo de pared y en qué coordenada.
                    String textoTipoPared = (tipo == 'H') ? "horizontal" : "vertical";
                    descripcionUltimaAccion = actual.getName()
                            + " (" + actual.getSymbol() + ") colocó una pared "
                            + textoTipoPared + " en la posición "
                            + fila + letraColumna;

                    // Después de colocar una pared válida verificamos si la nueva
                    // configuración del tablero deja a alguno de los jugadores sin
                    // camino posible hacia su fila objetivo.
                    boolean empatePorEncierro = GameUtils.hayEmpatePorEncierro(board, blanco, rojo);

                    if (empatePorEncierro) {
                        // La partida queda bloqueada por paredes/bordes: empate inmediato.
                        // ganador permanece en null; MoveHistory mostrará
                        // "NADIE GANÓ LA PARTIDA" al final del historial.
                        partidaActiva = false;
                    } else {
                        // Si ninguno está encerrado, simplemente alternamos el turno.
                        turnoBlanco = !turnoBlanco;
                    }


                } else {
                    System.out.println("Acción inválida. Use M, P o EXIT.");
                    continue;
                }
            } catch (Exception e) {
                // Si ocurre cualquier error (por ejemplo, al convertir números), mostramos un mensaje genérico y repetimos el turno.
                System.out.println("Error en la lectura. Intente de nuevo.");
            }
        }

        // Mostrar tablero final
        board.printBoard(blanco, rojo, (ganador != null ? ganador : blanco));

        // Resumen final de posiciones y última acción realizado justo debajo del tablero final.
        GameUtils.imprimirEstadoJugadores(blanco, rojo, descripcionUltimaAccion);

        // Imprimir historial completo de movimientos y el resultado de la partida.
        historial.imprimirHistorial(blanco, rojo, ganador);

        // No es necesario cerrar explícitamente BufferedReader sobre System.in en este tipo de aplicación.
    }
}
