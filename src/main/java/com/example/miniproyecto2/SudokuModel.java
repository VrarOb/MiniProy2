package com.example.miniproyecto2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Modelo del juego Sudoku 6x6.
 * Genera una solución completa mediante backtracking en orden aleatorio y crea
 * el puzzle visible dejando solo dos pistas por bloque 2x3.
 */
public class SudokuModel {

    public static final int SIZE = 6;
    public static final int EMPTY = 0;
    public int fives = 0;

    // Tablero visible (puzzle) y solución completa (oculta)
    private int[][] board;
    private int[][] solution;

    /**
     * Constructor: inicializa ambas matrices en vacío.
     */
    public SudokuModel() {
        board = new int[SIZE][SIZE];
        solution = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
                solution[i][j] = EMPTY;
            }
        }
    }

    /**
     * Retorna el valor de la celda (row, col) del puzzle.
     */
    public int getCell(int row, int col) {
        return board[row][col];
    }

    /**
     * Asigna un valor a la celda (row, col) del puzzle.
     */
    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }

    /**
     * Retorna la solución completa generada.
     */
    public int[][] getSolution() {
        return solution;
    }

    /**
     * Verifica si es válido colocar un número en (row, col) en la matriz pasada.
     * Se comprueban fila, columna y bloque 2x3.
     */
    private boolean isValidForSolution(int[][] board, int row, int col, int number) {
        // Fila
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == number) {
                return false;
            }
        }
        // Columna
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == number) {
                return false;
            }
        }
        // Bloque 2x3
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
     * Resuelve la matriz 'board' mediante backtracking.
     * Se prueba en orden aleatorio para obtener soluciones distintas.
     *
     * @param board matriz a resolver
     * @param row fila actual
     * @param col columna actual
     * @return true si se logra resolver, false en caso contrario
     */
    private boolean solveBoard(int[][] board, int row, int col) {
        if (row == SIZE) { // Se completaron todas las filas
            return true;
        }
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        if (board[row][col] != EMPTY) {
            return solveBoard(board, nextRow, nextCol);
        }

        // Generar un listado aleatorio de números del 1 al 6
        List<Integer> numbers = new ArrayList<>();
        for (int num = 1; num <= SIZE; num++) {
            numbers.add(num);
        }
        Collections.shuffle(numbers, new Random());

        for (int num : numbers) {
            if (isValidForSolution(board, row, col, num)) {
                board[row][col] = num;
                if (solveBoard(board, nextRow, nextCol)) {
                    return true;
                }
                board[row][col] = EMPTY;
            }
        }
        return false;
    }

    /**
     * Crea una copia de la matriz original.
     *
     * @param original matriz a copiar
     * @return copia de la matriz
     */
    private int[][] copyBoard(int[][] original) {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(original[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }

    /**
     * Genera el puzzle:
     * 1. Se resuelve la solución completa en 'solution' mediante backtracking (en orden aleatorio).
     * 2. Se copia la solución a 'board' y se borran, en cada bloque 2x3, todos los números
     *    excepto dos pistas.
     */
    public void generatePuzzle() {
        // Generar solución completa
        if (!solveBoard(solution, 0, 0)) {
            throw new RuntimeException("No se pudo generar una solución completa.");
        }
        // Copiar solución a puzzle
        board = copyBoard(solution);
        // En cada bloque 2x3, conservar solo 2 números y borrar el resto
        for (int blockRow = 0; blockRow < SIZE; blockRow += 2) {
            for (int blockCol = 0; blockCol < SIZE; blockCol += 3) {
                List<int[]> positions = new ArrayList<>();

                for (int r = blockRow; r < blockRow + 2; r++) {
                    for (int c = blockCol; c < blockCol + 3; c++) {
                        positions.add(new int[]{r, c});
                    }
                }

                Collections.shuffle(positions);

                int keptCount = 0;
                for (int[] pos : positions) {
                    int cellValue = board[pos[0]][pos[1]];
                    if (cellValue == 2) {
                        continue;
                    }
                    if (keptCount < 2) {
                        keptCount++;
                    } else {
                        board[pos[0]][pos[1]] = EMPTY;
                    }
                }
            }
        }
    }

    public int countFives() {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 5) {
                    count++;
                }
            }
        }
        return count;
    }

}
