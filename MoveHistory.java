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

import java.util.ArrayList;
import java.util.List;

/*
 Clase responsable de llevar el historial de movimientos de la partida.
 
 La idea es no mezclar la lógica del juego con la lógica de "registro".
 Aquí solamente guardamos y mostramos movimientos.
 
 Cada jugador tiene su propia lista de movimientos:
 - movimientosBlanco -> lo que hace el Personaje Blanco
 - movimientosRojo   -> lo que hace el Personaje Rojo
 
 Al final de la partida, se imprime una tabla con el formato:
 
   Personaje Blanco       | Personaje Rojo
 1. INICIAL - 1E          | INICIAL - 9E
 2. M (S) - 2E            | M (W) - 8E
 3. P (H 2 E)             | EXIT
 
 Y debajo se muestra quién ganó o si nadie ganó.
 */

public class MoveHistory {

    // Lista de movimientos del Personaje Blanco
    private List<String> movimientosBlanco;

    // Lista de movimientos del Personaje Rojo
    private List<String> movimientosRojo;

    // Constructor: inicializa las listas vacías y registra la posición inicial.
    public MoveHistory() {
        // Inicializamos las listas que guardan los movimientos
        movimientosBlanco = new ArrayList<>();
        movimientosRojo   = new ArrayList<>();

        /* ===========================================================================
         POSICIONES INICIALES EXPLÍCITAS EN EL HISTORIAL

         Por requerimiento del profesor, antes de cualquier
         movimiento debemos dejar registrada la posición
         inicial de ambos jugadores:
         - Personaje Blanco (PB) -> casilla 1E
         - Personaje Rojo   (PR) -> casilla 9E
        
         El formato "INI - 1E" / "INI - 9E" se interpreta como:
         INI   -> Posición inicial
         1E/9E -> Casilla del tablero
        
         Esto hará que en el historial la primera fila (número 1)
         sea siempre la posición inicial de cada jugador.
        =========================================================================== */
        
        movimientosBlanco.add("INICIAL - 1E");
        movimientosRojo.add("INICIAL - 9E");
     }

    /**
     * Registra un movimiento para el jugador indicado.
     @param jugador               Jugador que realizó el movimiento.
     @param descripcionMovimiento Texto corto con el movimiento realizado.
     **/
    public void registrarMovimiento(Player jugador, String descripcionMovimiento) {
        if (jugador == null || descripcionMovimiento == null) {
            // Por seguridad, si algo viene nulo no hacemos nada.
            return;
        }

        /* Decidimos a qué lista va el movimiento según el símbolo del jugador.
           En este proyecto:
           - Personaje Blanco -> símbolo "PB"
           - Personaje Rojo   -> símbolo "PR" */

        String simbolo = jugador.getSymbol();

        if ("PB".equals(simbolo)) {
            movimientosBlanco.add(descripcionMovimiento);
        } else if ("PR".equals(simbolo)) {
            movimientosRojo.add(descripcionMovimiento);
        } else {
            // Si en el futuro hubiera otros símbolos, se podrían manejar aquí.
            System.out.println("Advertencia: símbolo de jugador desconocido al registrar movimiento.");
        }
    }

    /**
     Imprime el historial de movimientos al final de la partida.
     @param blanco  Referencia al jugador blanco (para usar su nombre en el encabezado).
     @param rojo    Referencia al jugador rojo  (para usar su nombre en el encabezado).
     @param ganador Jugador que ganó la partida, o null si nadie ganó.
     **/
    public void imprimirHistorial(Player blanco, Player rojo, Player ganador) {
        System.out.println();
        System.out.println("========== HISTORIAL DE MOVIMIENTOS ==========");

        // Encabezado con los nombres de cada jugador
        // Ej: "  Personaje Blanco       | Personaje Rojo"
        System.out.printf("  %-20s   | %-20s%n",
                blanco.getName(), rojo.getName());

        // Calculamos cuántas filas tendrá la tabla.
        // Se usa la cantidad máxima entre ambos para no perder ningún movimiento.
        int totalFilas = Math.max(movimientosBlanco.size(), movimientosRojo.size());

        // Recorremos cada "ronda" de movimientos
        for (int i = 0; i < totalFilas; i++) {
            String movBlanco = (i < movimientosBlanco.size())
                    ? movimientosBlanco.get(i)
                    : ""; // si no hay movimiento, se deja vacío

            String movRojo = (i < movimientosRojo.size())
                    ? movimientosRojo.get(i)
                    : "";

            // Formato:
            //  1. M (S) - 2E         | M (W) - 8E
            System.out.printf("%2d. %-20s | %-20s%n",
                    (i + 1), movBlanco, movRojo);
        }

        System.out.println("==============================================");

        // Antes de mostrar el resultado final verificamos si la partida fue interrumpida porque alguno de los jugadores escribió "EXIT".
        boolean partidaInterrumpida = false;

        // Revisamos el último movimiento registrado del jugador blanco, si existe y contiene la palabra "EXIT".
        if (!movimientosBlanco.isEmpty()) {
            String ultimoBlanco = movimientosBlanco.get(movimientosBlanco.size() - 1);
            if (ultimoBlanco != null && ultimoBlanco.toUpperCase().contains("EXIT")) {
                partidaInterrumpida = true;
            }
        }

        // Revisamos el último movimiento registrado del jugador rojo, si existe y contiene la palabra "EXIT".
        if (!movimientosRojo.isEmpty()) {
            String ultimoRojo = movimientosRojo.get(movimientosRojo.size() - 1);
            if (ultimoRojo != null && ultimoRojo.toUpperCase().contains("EXIT")) {
                partidaInterrumpida = true;
            }
        }

        // Mensaje final sobre el resultado de la partida
        if (ganador != null) {
            // Caso 1: alguien ganó la partida
            // Mostramos el nombre del ganador en mayúsculas para resaltarlo
            System.out.println(ganador.getName().toUpperCase() + " GANÓ LA PARTIDA");
        } else if (partidaInterrumpida) {
             // Caso 2: nadie ganó, pero alguien se retiró usando EXIT
            System.out.println("PARTIDA INTERRUMPIDA");
        } else {
            // Caso 3: nadie ganó y nadie se retiró explícitamente
            // (la partida terminó en empate)
            System.out.println("NADIE GANÓ LA PARTIDA");
        }

    }
}
