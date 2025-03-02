package org.example.demo.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.example.demo.conroller.FormulaController;

public class FormulaPanel extends VBox {
    public FormulaPanel(FormulaController controller) {
        controller.addFormulaField(this);
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #181818; -fx-border-width: 0;");

        setMinWidth(200);
        setMaxWidth(Double.MAX_VALUE);
        setFillWidth(true);
        setMaxHeight(Double.MAX_VALUE);


        VBox.setVgrow(this, Priority.ALWAYS);

        setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.web("#181818"), new javafx.scene.layout.CornerRadii(12), Insets.EMPTY)));

        VBox formulaFieldsContainer = new VBox(10);
        formulaFieldsContainer.setStyle("-fx-spacing: 10px;");
        formulaFieldsContainer.setMaxWidth(Double.MAX_VALUE);
        formulaFieldsContainer.setAlignment(Pos.CENTER);

        getChildren().add(formulaFieldsContainer);

        Button addFormulaButton = new Button("Add function");
        addFormulaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12px;");
        addFormulaButton.setOnAction(e -> {
            controller.addFormulaField(formulaFieldsContainer);
        });

        HBox buttonContainer = new HBox(addFormulaButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10));

        getChildren().add(buttonContainer);
    }
}
