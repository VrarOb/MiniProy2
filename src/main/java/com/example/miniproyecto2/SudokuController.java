package com.example.miniproyecto2;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Controlador que enlaza la vista con el modelo.
 * Inicializa la cuadrícula, procesa la entrada del usuario y actualiza la vista.
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
    // La solución completa (oculta) para validar la entrada del usuario
    private int[][] solution;

    /**
     * Inicializa la vista: crea la cuadrícula, restringe la entrada y asigna eventos.
     */
    public void initialize() {
        // Se crea el modelo inicialmente para tener acceso a constantes y tamaño
        model = new SudokuModel();
        cells = new TextField[SudokuModel.SIZE][SudokuModel.SIZE];

        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefWidth(50);
                cell.setPrefHeight(50);
                cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");

                // Permitir solo un dígito (1-6)
                TextFormatter<String> formatter = new TextFormatter<>(change -> {
                    if (change.getControlNewText().matches("[1-6]?")) {
                        return change;
                    }
                    return null;
                });
                cell.setTextFormatter(formatter);

                final int r = row;
                final int c = col;
                cell.setOnKeyReleased((KeyEvent event) -> {
                    String text = cell.getText();
                    if (text.isEmpty()) {
                        model.setCell(r, c, SudokuModel.EMPTY);
                        cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");
                    } else {
                        try {
                            int num = Integer.parseInt(text);
                            // Validar comparando con la solución generada
                            if (solution[r][c] != num) {
                                showError("Número incorrecto para la celda (" + (r+1) + "," + (c+1) + ").");
                                cell.setStyle("-fx-border-color: red; -fx-font-size: 18; -fx-alignment: center;");
                            } else {
                                model.setCell(r, c, num);
                                cell.setEditable(false);
                                cell.setStyle("-fx-font-size: 18; -fx-alignment: center;");
                                checkPuzzleComplete();
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

        newGameButton.setOnAction(e -> startNewGame());
        helpButton.setOnAction(e -> provideHelp());
    }

    /**
     * Inicia un nuevo juego: crea un nuevo modelo para generar un tablero nuevo,
     * actualiza la solución y refresca la vista.
     */
    private void startNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nuevo Juego");
        alert.setHeaderText("Iniciar un nuevo juego de Sudoku");
        alert.setContentText("¿Estás seguro?");
        alert.showAndWait().ifPresent(response -> {
            // Re-inicializar el modelo genera una nueva solución y puzzle
            model = new SudokuModel();
            model.generatePuzzle();
            solution = model.getSolution();
            updateViewFromModel();
        });
    }

    /**
     * Actualiza la vista según el estado actual del puzzle.
     */
    private void updateViewFromModel() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                int val = model.getCell(row, col);
                if (val != SudokuModel.EMPTY) {
                    cells[row][col].setText(String.valueOf(val));
                    cells[row][col].setEditable(false);
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                }
                cells[row][col].setStyle("-fx-font-size: 18; -fx-alignment: center;");
            }
        }
    }

    /**
     * Verifica si el puzzle está completamente resuelto.
     * Si es así, muestra un mensaje "¡Has ganado!".
     */
    private void checkPuzzleComplete() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                if (model.getCell(row, col) == SudokuModel.EMPTY) {
                    return; // Al menos una celda está vacía
                }
            }
        }
        Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
        winAlert.setTitle("¡Ganaste!");
        winAlert.setHeaderText(null);
        winAlert.setContentText("¡Has completado el Sudoku correctamente!");
        winAlert.show();
    }

    /**
     * Proporciona ayuda mostrando el número correcto para la primera celda vacía.
     */
    private void provideHelp() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                if (model.getCell(row, col) == SudokuModel.EMPTY) {
                    int correctNumber = solution[row][col];
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ayuda");
                    alert.setHeaderText("Sugerencia para la celda (" + (row+1) + "," + (col+1) + ")");
                    alert.setContentText("El número correcto es: " + correctNumber);
                    alert.show();
                    return;
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
     * Muestra un mensaje de error.
     *
     * @param message el mensaje a mostrar.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}





