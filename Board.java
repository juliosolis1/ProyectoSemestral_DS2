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
 Representa el tablero de juego de 9x9 casillas.

 Esta clase:
 - Guarda la matriz de casillas donde se mueven los jugadores.
 - Administra las paredes horizontales y verticales que se colocan.
 - Proporciona métodos para dibujar el tablero en la consola, incluyendo bordes y paredes más gruesas.
 - Ofrece operaciones de consulta para saber si una posición está dentro del tablero, si hay una pared, etc.

 Es la “vista” principal del juego, porque todo lo que se muestra gráficamente en la consola sale de aquí.
 */

public class Board {

    // Tamaño del tablero 9x9
    public static final int tamaño = 9;

    /*
    Matriz de paredes horizontales
    hEdges[row][col] = true significa "hay una pared horizontal debajo de esta celda"
    IMPORTANTE DESTACAR: se usa desde WallPlacer y Quoridor para validar movimientos.
     */

    boolean[][] hEdges = new boolean[tamaño - 1][tamaño];

    /*
    Matriz de paredes verticales
    vEdges[row][col] = true significa "hay una pared vertical a la derecha de esta celda"
    IMPORTANTE DESTACAR: se usa desde WallPlacer y Quoridor para validar movimientos.
    */
    
    boolean[][] vEdges = new boolean[tamaño][tamaño - 1];

    // Matriz del tablero visible con símbolos como "*", "PB", "PR", etc.
    // Usamos String para poder dibujar fichas de dos caracteres.
    private String[][] cells = new String[tamaño][tamaño];

    /*
     Constructor: inicializa todas las celdas con "*".
     Las paredes empiezan en false (sin muros).
     */
    public Board() {
        // Inicializar todas las celdas con "*"
        for (int r = 0; r < tamaño; r++) {
            for (int c = 0; c < tamaño; c++) {
                cells[r][c] = "*";
            }
        }
    }

    /*
     Imprime el tablero con:
     - Coordenadas
     - Matriz con bordes finos
     - Paredes horizontales y verticales con bordes más gruesos
     - Posición de los dos personajes (blanco y rojo)
     
     Notas:
     - Se usan caracteres Unicode de líneas (┌ ┐ └ ┘ │ ─ ║ ═, etc.)
     - Si la consola muestra símbolos raros, guardar el archivo como UTF-8
       y usar una fuente compatible (por ejemplo en la terminal de VS Code).
     */
    public void printBoard(Player blanco, Player rojo, Player jugadorActual) {

        /* ==============================================================
        0. Caracteres para dibujar la matriz y las paredes
        ================================================================= */        

        // Color rojo para Personaje Rojo (PR)
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED   = "\u001B[31m";

        // Bordes delgados (matriz base)
        final char BORDE_V_DELGADO = '│';
        final String BORDE_H_DELGADO = "───";

        final char ESQUINA_SUP_IZQ = '┌';
        final char ESQUINA_SUP_DER = '┐';
        final char ESQUINA_INF_IZQ = '└';
        final char ESQUINA_INF_DER = '┘';

        final char UNION_SUP = '┬';
        final char UNION_INF = '┴';
        final char UNION_IZQ = '├';
        final char UNION_DER = '┤';
        final char UNION_CENTRO = '┼';

        // Bordes gruesos (muros)
        final char BORDE_V_GRUESO = '║';
        final String BORDE_H_GRUESO = "═══";

      
        // 1. Reiniciar la matriz visible con "*" 
        for (int r = 0; r < tamaño; r++) {
            for (int c = 0; c < tamaño; c++) {
                cells[r][c] = "*";
            }
        }

        /* 2. Colocar los personajes en la matriz visible (sobrescriben el "*" en sus posiciones correspondientes).
        Si ambos jugadores comparten la misma casilla, se dará prioridad visual al jugador que tiene el turno actual (jugadorActual). */

        // Primero colocamos al jugador que NO está en turno.
        Player jugadorSinTurno = (jugadorActual == blanco) ? rojo : blanco;

        // Colocamos al jugador sin turno en su casilla.
        cells[jugadorSinTurno.getRow()][jugadorSinTurno.getCol()] = jugadorSinTurno.getSymbol();

        // Ahora colocamos al jugador que SÍ está en turno. Si ambos comparten casilla,
        // esta asignación sobrescribirá a la anterior y será la ficha que vea el usuario.
        cells[jugadorActual.getRow()][jugadorActual.getCol()] = jugadorActual.getSymbol();

        /* ======================================================================
         2.1 Representación visual de las paredes disponibles de cada jugador

         En el juego físico de Quoridor, cada jugador tiene sus paredes
         “apartadas” a un lado del tablero. En la consola no podemos apilar
         fichas literalmente al costado de la matriz, pero estas líneas 
         muestran cuántos muros le quedan a cada jugador usando el carácter ║.
        ========================================================================= */

        System.out.println();

        // Línea de paredes para el Personaje Blanco
        System.out.print("    ");
        for (int i = 0; i < blanco.getWallsRemaining(); i++) {
            // Cada muro disponible se representa como un tramo vertical grueso.
            System.out.print(BORDE_V_GRUESO + "   ");
        }

        

        /*  
        3. Encabezado de columnas (A..I)
        
           En el tablero real tenemos 9 columnas de casillas:
                A  B  C  D  E  F  G  H  I
        
           La matriz interna usa índices 0..8, pero aquí los
           mostramos como letras para que el jugador vea
           coordenadas tipo "C5", "F7", etc.
        
           IMPORTANTE (alineación):
           Cada casilla visible se imprime como:
               │ * │
           Es decir, después del número de fila, hay:
               - un borde vertical '│'
               - un espacio, el símbolo de la celda y otro espacio " * "
        
           El carácter de la celda (el '*', 'B' o 'R') queda centrado
           cada 4 columnas de texto. Para que la letra A, B, C, ...
           quede JUSTO encima de ese símbolo, imprimimos exactamente
           4 caracteres por columna: "  A ".
        */

        System.out.println();
        System.out.print("    "); // Desplazamiento para alinear con los números de fila

        for (int c = 0; c < tamaño; c++) {
            // Convertimos el índice de columna (0..8) a letra (A..I).
            // 0 -> 'A', 1 -> 'B', ..., 8 -> 'I'
            char letraColumna = (char) ('A' + c);

            /*
             Imprimimos "  A ":
             - 2 espacios antes de la letra
             - la letra de la columna
             - 1 espacio después
            
             De esta forma, la letra queda centrada sobre el símbolo de la casilla que se imprime como " * " en cada fila.
            */
            System.out.print("  " + letraColumna + " ");
        }
        System.out.println();



        // 4. Borde superior del tablero (matriz finita)
        System.out.print("    ");               // Alineación con los números de columna
        System.out.print(ESQUINA_SUP_IZQ);        // Esquina superior izquierda

        for (int c = 0; c < tamaño; c++) {
            System.out.print(BORDE_H_DELGADO);    // Segmento horizontal fino de la celda
            if (c < tamaño - 1) {
                System.out.print(UNION_SUP);      // Unión entre columnas (parte de arriba)
            }
        }

        System.out.println(ESQUINA_SUP_DER);      // Esquina superior derecha

        /* 
         5. Filas del tablero
            Por cada fila imprimimos:
            a) Número de fila + celdas
            b) Línea separadora (con muros horizontales si aplica)
        */
        for (int r = 0; r < tamaño; r++) {

            // a) Número de fila alineado (siempre 1..9, pero lo dejamos general)
            if (r + 1 < 10) {
                // Para 1 dígito dejamos un espacio delante → " 1  "
                System.out.print(" " + (r + 1) + "  ");
            } else {
                // Por si en el futuro se cambia el tamaño del tablero
                System.out.print((r + 1) + "  ");
            }

            // Primer borde vertical de la fila (lado izquierdo del tablero)
            System.out.print(BORDE_V_DELGADO);

            // Recorremos todas las columnas de la fila r
               for (int c = 0; c < tamaño; c++) {

                // Contenido lógico de la celda (lo que hay en la matriz).
                // Puede ser "*", "PB", "PR", etc.
                String contenido = cells[r][c];

                // Por seguridad, si por alguna razón viene null lo tratamos como "*".
                if (contenido == null || contenido.isEmpty()) {
                     contenido = "*";
                }

                // Guardamos el contenido "lógico" original de la celda (sin relleno),
                // para poder saber si se trata del Personaje Rojo ("PR") antes de
                // agregar espacios y aplicar el color rojo.
                String contenidoOriginal = contenido;

               /*
                Queremos que cada celda ocupe EXACTAMENTE 3 caracteres de ancho
                (sin contar el borde vertical "│"), para mantener la cuadrícula alineada.
                
                Ejemplos:
                 - "*"  → " * "
                 - "PB" → "PB "
                 - "PR" → "PR "
                */  
              if (contenido.length() == 1) {
                  // Centramos símbolos de un solo carácter.
                   contenido = " " + contenido + " ";
               } else if (contenido.length() == 2) {
                  // Para símbolos de dos caracteres ("PB"/"PR") usamos 3 posiciones.
                    contenido = contenido + " ";
              } else if (contenido.length() > 3) {
                  // Si alguien mete algo más largo, recortamos a 3 para no romper el tablero.
                   contenido = contenido.substring(0, 3);
                }
 
                // Si el contenido lógico de la celda es "PR", coloreamos la celda en rojo.
                /* Los códigos ANSI no ocupan ancho visible, así que la cuadrícula se mantiene
                perfectamente alineada a pesar de agregarlos al texto. */
                String contenidoAImprimir = contenido;
                if ("PR".equals(contenidoOriginal)) {
                    contenidoAImprimir = ANSI_RED + contenido + ANSI_RESET;
                }

                // Imprimimos la celda ya formateada (exactamente 3 caracteres).
                System.out.print(contenidoAImprimir);


                // Entre celdas: o borde fino (matriz) o muro vertical grueso
                if (c < tamaño - 1) {
                    // Si vEdges[r][c] es true, hay un muro vertical entre (r,c) y (r,c+1)
                    char bordeEntreCeldas = vEdges[r][c] ? BORDE_V_GRUESO : BORDE_V_DELGADO;
                    System.out.print(bordeEntreCeldas);
                } else {
                    // Última celda: cerramos con el borde derecho fino del tablero
                    System.out.print(BORDE_V_DELGADO);
                }
            }

            // Terminamos la línea de celdas
            System.out.println();

            // b) Si no es la última fila, dibujamos la línea separadora (aquí es donde se ven los muros horizontales).
            if (r < tamaño - 1) {
                // Alineación con el header/números de columna
                System.out.print("    ");
                // Unión izquierda (similar a ├)
                System.out.print(UNION_IZQ);

                // Recorremos todas las columnas para dibujar segmentos horizontales
                for (int c = 0; c < tamaño; c++) {
                    // Si hay pared horizontal debajo de la celda (r,c), dibujamos un tramo grueso; si no, un tramo fino.
                    String segmento = hEdges[r][c] ? BORDE_H_GRUESO : BORDE_H_DELGADO;
                    System.out.print(segmento);

                    // Unión entre segmentos horizontales
                    if (c < tamaño - 1) {
                        System.out.print(UNION_CENTRO);
                    }
                }

                // Unión derecha (similar a ┤)
                System.out.println(UNION_DER);
            } else {
                // Si es la última fila, dibujamos la base del tablero
                System.out.print("    ");
                System.out.print(ESQUINA_INF_IZQ);

                for (int c = 0; c < tamaño; c++) {
                    System.out.print(BORDE_H_DELGADO);
                    if (c < tamaño - 1) {
                        System.out.print(UNION_INF);
                    }
                }

                System.out.println(ESQUINA_INF_DER);

            }
        }
        // Línea de paredes para el Personaje Rojo 
            System.out.println();
            System.out.print("    ");
            for (int i = 0; i < rojo.getWallsRemaining(); i++) {
                System.out.print(BORDE_V_GRUESO + "   ");
            }
            System.out.println();
    }
}
