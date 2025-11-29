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

import java.util.Scanner;

public class Quoridor {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Crear tablero y helper para paredes
        Board board = new Board();
        WallPlacer placer = new WallPlacer(board);

        // Crear personajes: PB empieza en la fila superior, en el centrol; PR empieza en la fila inferior, en el centro.
        Player blanco = new Player("Personaje Blanco", "PB", 0, Board.tamaño / 2, 10);
        Player rojo   = new Player("Personaje Rojo",   "PR", Board.tamaño - 1, Board.tamaño / 2, 10);

        System.out.println("              === QUORIDOR ===");
        System.out.println("Controles: M para mover, P para pared, EXIT para salir.");

        MoveHistory historial = new MoveHistory();

        boolean turnoBlanco = true;
        boolean partidaActiva = true;
        Player ganador = null;
        
        // Descripción de la última acción relevante (movimiento o pared) que mostraremos debajo del tablero en cada turno.
        String descripcionUltimaAccion = "";


        while (partidaActiva) {
            Player actual = turnoBlanco ? blanco : rojo;
            Player otro   = turnoBlanco ? rojo   : blanco;
            
            // Mostrar tablero con las posiciones actuales
            board.printBoard(blanco, rojo);

            /* Mostrar debajo del tablero un resumen en texto plano
            con las posiciones actuales de ambos jugadores y,
            si aplica, la última acción realizada. */
            GameUtils.imprimirEstadoJugadores(blanco, rojo, descripcionUltimaAccion);

             System.out.println();
             System.out.println("----------------------------------------");
             // Armamos el mensaje de turno.
             // Si el jugador actual es el Personaje Rojo utilizamos los códigos ANSI para mostrar el símbolo [PR] en color rojo.
             String simboloActual = actual.getSymbol();
             String etiquetaTurno;
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


            String accion = sc.next().toUpperCase();

            if (accion.equals("EXIT")) {

                // Registramos que este jugador decidió salir de la partida (EXIT)

                historial.registrarMovimiento(actual, "EXIT");

                System.out.println("PARTIDA INTERRUMPIDA POR EL USUARIO.");
                break;
            }

            try {
                    if (accion.equals("M")) {
                    // Movimiento con teclas WASD
                    System.out.print("Dirección (W = arriba, S = abajo, A = izquierda, D = derecha): ");
                    String dirStr = sc.next();
                    char dir = dirStr.charAt(0);

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
                    char colFinal = (char) ('A' + actual.getCol());   // 0->A, 1->B, ..., 8->I

                    String movimiento = "M (" + Character.toUpperCase(dir) + ") - "
                            + filaFinal + colFinal;

                    historial.registrarMovimiento(actual, movimiento);

                    /* Actualizamos la descripción de la última acción para que
                    el mensaje debajo del tablero indique la nueva posición
                    del jugador que se acaba de mover. */
                    descripcionUltimaAccion = actual.getName()
                    + " (" + actual.getSymbol() + ") ahora está en la posición "
                    + filaFinal + colFinal;



                    // Verificar condición de victoria:
                    // Blanco gana si llega a la última fila (abajo)
                    // Rojo gana si llega a la primera fila (arriba)
                    if (actual == blanco && actual.getRow() == Board.tamaño - 1) {
                        ganador = actual;
                        partidaActiva = false;
                    } else if (actual == rojo && actual.getRow() == 0) {
                        ganador = actual;
                        partidaActiva = false;
                    } else {
                        // Cambio de turno
                        turnoBlanco = !turnoBlanco;
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
                    char tipo = sc.next().toUpperCase().charAt(0);

                    // Fila: siempre numérica, de 1 a 8
                    System.out.print("Fila (1-8): ");
                    int fila = sc.nextInt();

                    // Columna: siempre letras, de A hacia H
                    System.out.print("Columna inicial (A-H): ");
                    char letraColumna = sc.next().toUpperCase().charAt(0);

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

                    // Ahora intentamos colocar la pared usando WallPlacer.
                    boolean ok = false;

                    if (tipo == 'H') {
                    // Pared horizontal: se dibuja entre la fila "fila" y la fila "fila+1", ocupando dos casillas en X.
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
                    /* Actualizamos la descripción de la última acción para que se muestre qué jugador colocó qué tipo de pared
                    y en qué coordenada (fila + columna). */
                    String textoTipoPared = (tipo == 'H') ? "horizontal" : "vertical";
                    descripcionUltimaAccion = actual.getName()
                    + " (" + actual.getSymbol() + ") colocó una pared "
                    + textoTipoPared + " en la posición "
                    + fila + letraColumna;


                    // Si se colocó correctamente, descontamos un muro y cambiamos el turno.
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

                    turnoBlanco = !turnoBlanco;

                } else {
                    System.out.println("Acción inválida. Use M, P o EXIT.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Error en la lectura. Intente de nuevo.");
                sc.nextLine(); // limpiar buffer
            }
        }

        // Mostrar tablero final
        board.printBoard(blanco, rojo);

        // Resumen final de posiciones y última acción realizado justo debajo del tablero final.
        GameUtils.imprimirEstadoJugadores(blanco, rojo, descripcionUltimaAccion);

        // Imprimir historial completo de movimientos y el resultado de la partida.
        historial.imprimirHistorial(blanco, rojo, ganador);

        sc.close();
    }
}
