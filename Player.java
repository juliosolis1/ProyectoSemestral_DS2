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
  Representa a un jugador dentro del juego Quoridor.
 
  Cada objeto Player almacena:
  - El nombre que se muestra en consola (por ejemplo, "Personaje Blanco" o "Personaje Rojo").
  - El símbolo que se dibuja en el tablero ("PB" o "PR").
  - La posición actual del jugador en el tablero (fila y columna).
  - La cantidad de muros disponibles que le quedan.
 
  Además, expone métodos para:
  - Consultar y actualizar su posición.
  - Descontar muros cuando el jugador coloca una pared.
 
  Es la clase que modela a los peones que los jugadores mueven en el tablero.
 */

public class Player {

    // Nombre que se mostrará en la consola ("Personaje Blanco", "Personaje Rojo", etc.)
    private String name;

    /* ===============================================================
    Símbolo que se dibuja en el tablero.
    AHORA:
    - "PB" para Personaje Blanco
    - "PR" para Personaje Rojo
    ==================================================================*/

    private String symbol;

    // Fila y columna actuales del jugador en el tablero (0..8).
    private int row;
    private int col;

    // Cantidad de muros que todavía puede colocar este jugador.
    private int wallsRemaining;

    /**
     Constructor del jugador.
     
     @param name            Nombre que se mostrará (ej: "Personaje Blanco").
     @param symbol          Símbolo a dibujar en el tablero ("PB" o "PR").
     @param row             Fila inicial del jugador (0..8).
     @param col             Columna inicial del jugador (A..H).
     @param wallsRemaining  Cantidad de muros disponibles al inicio.
     **/
    public Player(String name, String symbol, int row, int col, int wallsRemaining) {
        this.name = name;
        this.symbol = symbol;
        this.row = row;
        this.col = col;
        this.wallsRemaining = wallsRemaining;
    }

    // =======
    // Getters
    // =======

    public String getName() {
        return name;
    }

    /*
     Devuelve el símbolo que se usa para dibujar al jugador en el tablero.
     Ahora es un String ("PB" o "PR").
     */
    public String getSymbol() {
        return symbol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getWallsRemaining() {
        return wallsRemaining;
    }

    // =======
    // Setters
    // =======

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setWallsRemaining(int wallsRemaining) {
        this.wallsRemaining = wallsRemaining;
    }
}
