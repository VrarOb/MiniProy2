
package com.example.miniproyecto2;


/**
 * Modelo del juego Sudoku 6x6.
 * Mantiene la estructura de datos del tablero y provee métodos para validar
 * movimientos según las reglas del juego (cada fila, columna y bloque 2x3 sin repeticiones).
 */
public class SudokuModel {

    public static final int SIZE = 6;
    public static final int EMPTY = 0;

    private int[][] board;

    /**
     * Constructor que inicializa el tablero vacío.
     */
    public SudokuModel() {
        board = new int[SIZE][SIZE];
        // Inicialmente, todas las celdas están vacías (valor 0).
    }

    /**
     * Valida si el movimiento (colocar un número en una celda) es válido.
     *
     * @param row    la fila de la celda
     * @param col    la columna de la celda
     * @param number el número a colocar (de 1 a 6)
     * @return true si el movimiento es válido, false en caso contrario
     */
    public boolean isValidMove(int row, int col, int number) {
        // Validar en la fila.
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == number) {
                return false;
            }
        }
        // Validar en la columna.
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == number) {
                return false;
            }
        }
        // Validar en el bloque 2x3.
        int blockRow = (row / 2) * 2;
        int blockCol = (col / 3) * 3;
        for (int r = blockRow; r < blockRow + 2; r++) {
            for (int c = blockCol; c < blockCol + 3; c++) {
                if (board[r][c] == number) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Coloca un número en la celda especificada.
     *
     * @param row    la fila de la celda
     * @param col    la columna de la celda
     * @param number el número a colocar
     */
    public void setCell(int row, int col, int number) {
        board[row][col] = number;
    }

    /**
     * Obtiene el valor de la celda especificada.
     *
     * @param row la fila de la celda
     * @param col la columna de la celda
     * @return el valor contenido en la celda (0 si está vacía)
     */
    public int getCell(int row, int col) {
        return board[row][col];
    }
}

