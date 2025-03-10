package org.example.demo.view;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.ExecutionException;


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

        double rawStep = Math.pow(10, Math.floor(Math.log10(1 / graphController.getScale() * 100)));

        double minSpacing = 60;

        double step = rawStep;
        while ((step * graphController.getScale()) < minSpacing) {
            step *= 2;
        }

        gc.setStroke(Color.web("#DADADA"));
        gc.setLineWidth(0.5);


        for (double x = Math.floor(minX / step) * step; x <= maxX; x += step) {
            double screenX = x * graphController.getScale() + width / 2 + graphController.getOffsetX();
            gc.strokeLine(screenX, 0, screenX, height);

            if (x != 0) {
                String label;
                if (Math.abs(x) < 0.01 || Math.abs(x) > 10000) {
                    label = String.format("%.1e", x);
                } else {
                    label = String.format("%.1f", x);
                }
                gc.fillText(label, screenX + 2, height / 2 + graphController.getOffsetY() + 12);

            }
        }

        for (double y = Math.floor(minY / step) * step; y <= maxY; y += step) {
            double screenY = -y * graphController.getScale() + height / 2 + graphController.getOffsetY();
            gc.strokeLine(0, screenY, width, screenY);
            if (y != 0) {
                String label;
                if (Math.abs(y) < 0.01 || Math.abs(y) > 10000) {
                    label = String.format("%.1e", y);
                } else {
                    label = String.format("%.1f", y);
                }
                gc.fillText(label, width / 2 + graphController.getOffsetX() + 5, screenY - 2);            }
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

        List<List<Point>> points;
        try {
            points = graphController.calculatePoints();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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