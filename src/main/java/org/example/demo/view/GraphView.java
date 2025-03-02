package org.example.demo.view;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;


import lombok.Getter;
import org.example.demo.model.Point;
import org.example.demo.conroller.GraphController;

public class GraphView extends Pane {
    @Getter
    private final Canvas canvas;
    private final GraphController graphController;

    double minX;
    double maxX;
    double minY;
    double maxY;



    public GraphView(Stage stage) {
        this.canvas = new Canvas(550, 600);
        this.canvas.widthProperty().bind(stage.widthProperty().subtract(250));
        this.canvas.heightProperty().bind(stage.heightProperty());
        getChildren().add(canvas);
        graphController = new GraphController(this);

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> graphController.requestRedraw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> graphController.requestRedraw());

    }

    public void setGraph(int index, String value) {
        graphController.setGraph(index, value);
    }

    public void remove(int index) {
        graphController.removeGraph(index);
    }


    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        gc.clearRect(0, 0, width, height);

        minX = graphController.getMinX();
        maxX = graphController.getMaxX();
        minY = graphController.getMinY();
        maxY = graphController.getMaxY();

        double[] possibleSteps = {0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100};
        double step = 1;
        for (double s : possibleSteps) {
            if (graphController.getScale() * s >= 50) {
                step = s;
                break;
            }
        }

        gc.setStroke(Color.web("#DADADA"));
        gc.setLineWidth(0.5);

        for (double x = Math.floor(minX / step) * step; x <= maxX; x += step) {
            double screenX = x * graphController.getScale() + width / 2 + graphController.getOffsetX();
            gc.strokeLine(screenX, 0, screenX, height);

            if (x != 0) { // Убираем дублирующийся 0
                gc.fillText(String.format("%.1f", x), screenX + 2, height / 2 + graphController.getOffsetY() + 12);
            }
        }

        for (double y = Math.floor(minY / step) * step; y <= maxY; y += step) {
            double screenY = -y * graphController.getScale() + height / 2 + graphController.getOffsetY();
            gc.strokeLine(0, screenY, width, screenY);

            if (y != 0) {
                gc.fillText(String.format("%.1f", y), width / 2 + graphController.getOffsetX() + 5, screenY - 2);
            }
        }


        gc.setStroke(Color.web("#888888"));
        gc.setLineWidth(2);
        double centerX = width / 2 + graphController.getOffsetX();
        double centerY = height / 2 + graphController.getOffsetY();

        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);

        gc.setFill(Color.BLACK);
        gc.fillText("0", centerX + 5, centerY - 5);
    }


    public void drawGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);
        drawGrid();

        List<List<Point>> points = graphController.calculatePoints();
        gc.setLineWidth(2);

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.BROWN};
        int i = 0;

        for (List<Point> formulaPoints : points) {
            gc.setStroke(colors[i % colors.length]);
            gc.beginPath();
            boolean first = true;
            for (Point point : formulaPoints) {
                if (first) {
                    gc.moveTo(point.getX(), point.getY());
                    first = false;
                } else {
                    gc.lineTo(point.getX(), point.getY());
                }
            }
            gc.stroke();
            i++;
        }
    }

}