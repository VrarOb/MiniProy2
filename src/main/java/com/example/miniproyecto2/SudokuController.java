package com.example.miniproyecto2;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Controlador que gestiona la interacción entre la vista y el modelo.
 * Se encarga de inicializar la cuadrícula, manejar eventos de teclado y mouse,
 * iniciar nuevos juegos, validar movimientos y proporcionar sugerencias de ayuda.
 */
public class SudokuController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Button newGameButton;
    @FXML
    private Button helpButton;

    private SudokuModel model;
    private TextField[][] cells;

    /**
     * Método de inicialización llamado automáticamente al cargar el FXML.
     * Construye la cuadrícula de celdas y asocia los eventos.
     */
    public void initialize() {
        model = new SudokuModel();
        cells = new TextField[SudokuModel.SIZE][SudokuModel.SIZE];

        // Construir la cuadrícula 6x6.
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefWidth(50);
                cell.setPrefHeight(50);
                cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");
                final int r = row;
                final int c = col;
                // Manejar la entrada de teclado en cada celda.
                cell.setOnKeyReleased((KeyEvent event) -> {
                    String text = cell.getText();
                    if (text.isEmpty()) {
                        model.setCell(r, c, SudokuModel.EMPTY);
                        cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");
                    } else {
                        try {
                            int num = Integer.parseInt(text);
                            if (num < 1 || num > 6) {
                                showError("Solo se permiten números del 1 al 6");
                                cell.setText("");
                            } else {
                                if (model.isValidMove(r, c, num)) {
                                    model.setCell(r, c, num);
                                    // Restablecer estilo en caso de corrección.
                                    cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");
                                } else {
                                    showError("Movimiento inválido: el número se repite en la fila, columna o bloque.");
                                    cell.setStyle("-fx-border-color: red; -fx-font-size: 18; -fx-alignment: center;");
                                }
                            }
                        } catch (NumberFormatException e) {
                            showError("Entrada no válida. Ingrese un número del 1 al 6.");
                            cell.setText("");
                        }
                    }
                });
                cells[row][col] = cell;
                gridPane.add(cell, col, row);
            }
        }
        // Asociar los eventos de los botones.
        newGameButton.setOnAction(e -> startNewGame());
        helpButton.setOnAction(e -> provideHelp());
    }

    /**
     * Inicia un nuevo juego solicitando confirmación al usuario.
     * Si se confirma, reinicia el tablero y coloca dos números en cada bloque 2x3.
     */
    private void startNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nuevo Juego");
        alert.setHeaderText("Iniciar un nuevo juego de Sudoku");
        alert.setContentText("¿Estás seguro?");
        alert.showAndWait().ifPresent(response -> {
            // Reinicializar el modelo y la interfaz.
            model = new SudokuModel();
            for (int row = 0; row < SudokuModel.SIZE; row++) {
                for (int col = 0; col < SudokuModel.SIZE; col++) {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    cells[row][col].setStyle("-fx-font-size: 18; -fx-alignment: center;");
                }
            }
            initializeBoard();
        });
    }

    /**
     * Inicializa el tablero colocando dos números válidos en cada bloque 2x3.
     * Se utiliza un enfoque con intentos limitados para cada bloque y, de no lograrlo,
     * se reinicia la generación del tablero completo.
     */
    private void initializeBoard() {
        boolean success = false;
        int overallAttempts = 0;
        // Limitar el número de reintentos globales para evitar bucles infinitos.
        while (!success && overallAttempts < 1000) {
            overallAttempts++;
            // Reinicializar el modelo y limpiar la interfaz.
            model = new SudokuModel();
            resetCells();
            success = true;

            // Iterar sobre cada bloque de 2x3.
            for (int blockRow = 0; blockRow < SudokuModel.SIZE; blockRow += 2) {
                for (int blockCol = 0; blockCol < SudokuModel.SIZE; blockCol += 3) {
                    int count = 0;
                    int blockAttempts = 0;
                    // Intentar colocar 2 números en el bloque actual
                    while (count < 2 && blockAttempts < 100) {
                        blockAttempts++;
                        int r = blockRow + (int)(Math.random() * 2);
                        int c = blockCol + (int)(Math.random() * 3);
                        if (model.getCell(r, c) == SudokuModel.EMPTY) {
                            int num = 1 + (int)(Math.random() * 6);
                            if (model.isValidMove(r, c, num)) {
                                model.setCell(r, c, num);
                                cells[r][c].setText(String.valueOf(num));
                                cells[r][c].setEditable(false);
                                count++;
                            }
                        }
                    }
                    // Si después de 100 intentos no se pudo colocar 2 números en el bloque,
                    // se marca la generación como fallida y se reinicia todo el tablero.
                    if (count < 2) {
                        success = false;
                        break;
                    }
                }
                if (!success) break;
            }
            // Si se logró completar la generación, se sale del ciclo.
            if (success) break;
        }
        if (!success) {
            showError("No se pudo inicializar el tablero. Intente reiniciar el juego.");
        }
    }

    /**
     * Reinicia visualmente las celdas para la generación de un nuevo tablero.
     */
    private void resetCells() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setEditable(true);
                cells[row][col].setStyle("-fx-font-size: 18; -fx-alignment: center;");
            }
        }
    }

    /**
     * Proporciona una sugerencia de ayuda para una celda vacía.
     * Busca la primera celda vacía y sugiere el primer número válido.
     */
    private void provideHelp() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                if (model.getCell(row, col) == SudokuModel.EMPTY) {
                    for (int num = 1; num <= 6; num++) {
                        if (model.isValidMove(row, col, num)) {
                            // Resaltar la celda sugerida.
                            cells[row][col].setStyle("-fx-border-color: blue; -fx-font-size: 18; -fx-alignment: center;");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Ayuda");
                            alert.setHeaderText("Sugerencia para la celda (" + (row + 1) + "," + (col + 1) + ")");
                            alert.setContentText("Un posible número es: " + num);
                            alert.show();
                            return;
                        }
                    }
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ayuda");
        alert.setHeaderText(null);
        alert.setContentText("No hay celdas vacías para sugerir ayuda.");
        alert.show();
    }

    /**
     * Muestra una alerta de error con el mensaje especificado.
     *
     * @param message el mensaje de error a mostrar
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
