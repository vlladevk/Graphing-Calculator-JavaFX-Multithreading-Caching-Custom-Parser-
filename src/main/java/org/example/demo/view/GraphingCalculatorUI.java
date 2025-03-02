package org.example.demo.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.demo.conroller.FormulaController;

public class GraphingCalculatorUI extends Application {
    @Override
    public void start(Stage stage) {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #ffffff;");

        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #181818; -fx-border-width: 0 0 0 1; -fx-border-color: #3A3A3A;");

        GraphView graphView = new GraphView(stage);
        FormulaController controller = new FormulaController(graphView);
        root.getChildren().addAll(graphView, rightPanel);

        FormulaPanel formulaPanel = new FormulaPanel(controller);
        rightPanel.getChildren().add(formulaPanel);
        VBox.setVgrow(formulaPanel, Priority.ALWAYS);


        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Graphing Calculator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
