package com.example.miniproyecto2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;

public class SudokuController {

    @FXML private GridPane gridPane;
    @FXML private Button newGameButton;
    @FXML private Button helpButton;
    @FXML private Button instructionsButton;
    @FXML private Label fivesLabel;

    private SudokuModel model;
    private TextField[][] cells;
    private int[][] solution;

    public void initialize() {
        gridPane.getStylesheets().add(getClass().getResource("sudoku.css").toExternalForm());

        model = new SudokuModel();
        cells = new TextField[SudokuModel.SIZE][SudokuModel.SIZE];
        model.generatePuzzle();
        model.countFives();
        solution = model.getSolution();

        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                TextField cell = createCell(row, col);
                cells[row][col] = cell;
                gridPane.add(cell, col, row);
            }
        }

        gridPane.getStyleClass().add("grid-pane");
        updateViewFromModel();

        newGameButton.setOnAction(e -> startNewGame());
        helpButton.setOnAction(e -> provideHelp());
        instructionsButton.setOnAction(e -> instructions());
    }

    private TextField createCell(int row, int col) {
        TextField cell = new TextField();
        cell.setPrefWidth(50);
        cell.setPrefHeight(50);
        cell.getStyleClass().add("cell");

        // Añadir bordes para bloques 2x3
        if (col % 3 == 2 && row % 2 == 1) cell.getStyleClass().add("block-corner");
        else if (col % 3 == 2) cell.getStyleClass().add("block-right");
        else if (row % 2 == 1) cell.getStyleClass().add("block-bottom");

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[1-6]?")) {
                return change;
            }
            return null;
        });
        cell.setTextFormatter(formatter);

        final int r = row;
        final int c = col;
        cell.setOnKeyReleased((KeyEvent event) -> handleCellInput(cell, r, c));

        return cell;
    }

    private void handleCellInput(TextField cell, int row, int col) {
        String text = cell.getText();
        if (text.isEmpty()) {
            model.setCell(row, col, SudokuModel.EMPTY);
            cell.getStyleClass().removeAll("cell-error", "cell-correct");
        } else {
            try {
                int num = Integer.parseInt(text);
                if (solution[row][col] != num) {
                    showError("Número incorrecto para la celda (" + (row+1) + "," + (col+1) + ").");
                    cell.getStyleClass().remove("cell-correct");
                    cell.getStyleClass().add("cell-error");
                } else {
                    model.setCell(row, col, num);
                    cell.setEditable(false);
                    cell.getStyleClass().remove("cell-error");
                    cell.getStyleClass().add("cell-correct");

                    model.setCell(row,col, num);{
                        if (num == 5) model.fives ++;
                        int numbers = (model.countFives() - model.fives) ;
                        fivesLabel.setText("Cincos iniciales: " + numbers
                                + " | Cincos ingresados: " + model.fives);
                    }

                    // Cambiar esta línea ↓
                    if (isRowComplete(row) || isColComplete(col) || isBlockComplete(row, col)) {
                        applyPulseEffect(row, col); // <-- Usar coordenadas en lugar de la celda
                    }
                    checkPuzzleComplete();
                }


            } catch (NumberFormatException e) {
                showError("Entrada no válida. Ingrese un número del 1 al 6.");
                cell.setText("");
            }
        }
    }

    private boolean isRowComplete(int row) {
        for (int c = 0; c < SudokuModel.SIZE; c++) {
            if (model.getCell(row, c) == SudokuModel.EMPTY) return false;
        }
        return true;
    }

    private boolean isColComplete(int col) {
        for (int r = 0; r < SudokuModel.SIZE; r++) {
            if (model.getCell(r, col) == SudokuModel.EMPTY) return false;
        }
        return true;
    }

    private boolean isBlockComplete(int row, int col) {
        int blockRow = (row / 2) * 2;
        int blockCol = (col / 3) * 3;

        for (int r = blockRow; r < blockRow + 2; r++) {
            for (int c = blockCol; c < blockCol + 3; c++) {
                if (model.getCell(r, c) == SudokuModel.EMPTY) return false;
            }
        }
        return true;
    }

    private void applyPulseEffect(int row, int col) {
        List<TextField> sectionCells = new ArrayList<>();

        if (isRowComplete(row)) {
            for (int c = 0; c < SudokuModel.SIZE; c++) {
                sectionCells.add(cells[row][c]);
            }
        } else if (isColComplete(col)) {
            for (int r = 0; r < SudokuModel.SIZE; r++) {
                sectionCells.add(cells[r][col]);
            }
        } else if (isBlockComplete(row, col)) {
            int blockRow = (row / 2) * 2;
            int blockCol = (col / 3) * 3;
            for (int r = blockRow; r < blockRow + 2; r++) {
                for (int c = blockCol; c < blockCol + 3; c++) {
                    sectionCells.add(cells[r][c]);
                }
            }
        }

        // Aplicar animación a todas las celdas de la sección
        Timeline timeline = new Timeline();
        for (TextField cell : sectionCells) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(0.1), e -> cell.getStyleClass().add("pulse-animation")),
                    new KeyFrame(Duration.seconds(0.3), e -> cell.getStyleClass().remove("pulse-animation")),
                    new KeyFrame(Duration.seconds(0.5), e -> cell.getStyleClass().add("pulse-animation")),
                    new KeyFrame(Duration.seconds(0.7), e -> cell.getStyleClass().remove("pulse-animation"))
            );
        }
        timeline.play();
    }

    private void startNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nuevo Juego");
        alert.setHeaderText("Iniciar un nuevo juego de Sudoku");
        alert.setContentText("¿Estás seguro?");
        alert.showAndWait().ifPresent(response -> {
            model = new SudokuModel();
            model.generatePuzzle();
            model.countFives();
            solution = model.getSolution();
            updateViewFromModel();
        });
    }

    private void updateViewFromModel() {

        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                TextField cell = cells[row][col];
                int val = model.getCell(row, col);

                cell.getStyleClass().removeAll("cell-correct", "cell-error");

                if (val != SudokuModel.EMPTY) {
                    cell.setText(String.valueOf(val));
                    cell.setEditable(false);
                    if (solution[row][col] == val) {
                        cell.getStyleClass().add("cell-correct");
                    }
                } else {
                    cell.setText("");
                    cell.setEditable(true);
                }
            }
        }
    }

    private void checkPuzzleComplete() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                if (model.getCell(row, col) == SudokuModel.EMPTY) return;
            }
        }
        showAlert(Alert.AlertType.INFORMATION, "¡Ganaste!", "¡Has completado el Sudoku correctamente!");
    }

    private void provideHelp() {
        for (int row = 0; row < SudokuModel.SIZE; row++) {
            for (int col = 0; col < SudokuModel.SIZE; col++) {
                if (model.getCell(row, col) == SudokuModel.EMPTY) {
                    showAlert(Alert.AlertType.INFORMATION, "Ayuda",
                            "Sugerencia para la celda (" + (row+1) + "," + (col+1) + ")\nEl número correcto es: " + solution[row][col]);
                    return;
                }
            }
        }
        showAlert(Alert.AlertType.INFORMATION, "Ayuda", "No hay celdas vacías para sugerir ayuda.");
    }


    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void instructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instrucciones");
        alert.setHeaderText("Cómo jugar al Sudoku");
        alert.setContentText(
                "Reglas del juego:\n" +
                        "1. Cada fila debe contener números del 1 al 6 sin repetirse.\n" +
                        "2. Cada columna debe contener números del 1 al 6 sin repetirse.\n" +
                        "3. Cada bloque 2x3 debe contener números del 1 al 6 sin repetirse.\n\n" +
                        "Utiliza la lógica para deducir el número correcto en cada celda.\n" +
                        "¡Buena suerte!"
        );
        alert.showAndWait();
    }
}




