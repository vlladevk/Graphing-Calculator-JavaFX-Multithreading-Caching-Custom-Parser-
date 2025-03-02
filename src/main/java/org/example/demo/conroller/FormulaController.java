package org.example.demo.conroller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.demo.view.GraphView;


public class FormulaController {
    private final GraphView graphView;
    private int formulaIndex = 0;

    public FormulaController(GraphView graphView) {
        this.graphView = graphView;
    }

    public void addFormulaField(VBox formulaFieldsContainer) {
        graphView.drawGraph();
        HBox formulaFieldWithDelete = new HBox(10);
        formulaFieldWithDelete.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(formulaFieldWithDelete, Priority.ALWAYS);
        TextField formulaField = new TextField();
        formulaField.setPromptText("f(x) = ");
        formulaField.setStyle("-fx-font-size: 18px; -fx-padding: 5px 10px 10px 5px; -fx-background-color: #2A2A2A; -fx-text-fill: white;");
        formulaField.setBackground(new Background(new BackgroundFill(Color.web("#2A2A2A"), null, Insets.EMPTY)));
        formulaField.setMinWidth(180);
        formulaField.setPrefWidth(180);
        Button deleteButton = new Button("X");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 16px;");
        final int currentIndex = formulaIndex++;

        deleteButton.setOnAction(e -> {
            graphView.remove(currentIndex);
            formulaFieldsContainer.getChildren().remove(formulaFieldWithDelete);
            graphView.drawGraph();
        });

        formulaField.textProperty().addListener((observable, oldValue, newValue) -> {
            graphView.setGraph(currentIndex, newValue);
        });

        formulaField.setOnAction(event -> {
            graphView.drawGraph();
        });

        StackPane deleteButtonContainer = new StackPane(deleteButton);
        formulaFieldWithDelete.getChildren().addAll(formulaField, deleteButtonContainer);

        formulaFieldsContainer.getChildren().add(formulaFieldWithDelete);
    }
}
