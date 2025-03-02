package org.example.demo.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Duration;
import lombok.Getter;
import org.example.demo.parser.*;

public class GraphView extends Pane {
    @Getter
    private final Canvas canvas;
    private final Map<Integer, String> formulas = new HashMap<>();

    private double scale = 30;
    private double offsetX = 0;
    private double offsetY = 0;
    double minX;
    double maxX;
    double minY;
    double maxY;


    private double lastMouseX;
    private double lastMouseY;
    private final Timeline redrawTimeline;

    public GraphView(Stage stage) {
        this.canvas = new Canvas(550, 600);
        this.canvas.widthProperty().bind(stage.widthProperty().subtract(250));
        this.canvas.heightProperty().bind(stage.heightProperty());
        getChildren().add(canvas);

        // Настройка таймера для отложенной перерисовки
        redrawTimeline = new Timeline(new KeyFrame(Duration.millis(5), e -> drawGraph()));
        redrawTimeline.setCycleCount(1);

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> requestRedraw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> requestRedraw());

        setupMouseControls();
    }

    private void setupMouseControls() {
        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            requestRedraw();
        });

        canvas.setOnMouseDragged(event -> {
            offsetX += (event.getX() - lastMouseX);
            offsetY += (event.getY() - lastMouseY);
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            requestRedraw();
        });

        canvas.setOnScroll((ScrollEvent event) -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale *= zoomFactor;
            requestRedraw();
        });
    }

    public void setGraph(int index, String value) {
        formulas.put(index, value);
    }

    public void remove(int index) {
        formulas.remove(index);
    }


    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        gc.clearRect(0, 0, width, height);

        minX = (-width / 2 - offsetX) / scale;
        maxX = (width / 2 - offsetX) / scale;
        minY = (-height / 2 + offsetY) / scale;
        maxY = (height / 2 + offsetY) / scale;

        double[] possibleSteps = {0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100};
        double step = 1;
        for (double s : possibleSteps) {
            if (scale * s >= 50) {
                step = s;
                break;
            }
        }

        gc.setStroke(Color.web("#DADADA"));
        gc.setLineWidth(0.5);

        for (double x = Math.floor(minX / step) * step; x <= maxX; x += step) {
            double screenX = x * scale + width / 2 + offsetX;
            gc.strokeLine(screenX, 0, screenX, height);

            if (x != 0) { // Убираем дублирующийся 0
                gc.fillText(String.format("%.1f", x), screenX + 2, height / 2 + offsetY + 12);
            }
        }

        for (double y = Math.floor(minY / step) * step; y <= maxY; y += step) {
            double screenY = -y * scale + height / 2 + offsetY;
            gc.strokeLine(0, screenY, width, screenY);

            if (y != 0) {
                gc.fillText(String.format("%.1f", y), width / 2 + offsetX + 5, screenY - 2);
            }
        }


        gc.setStroke(Color.web("#888888"));
        gc.setLineWidth(2);
        double centerX = width / 2 + offsetX;
        double centerY = height / 2 + offsetY;

        gc.strokeLine(0, centerY, width, centerY);
        gc.strokeLine(centerX, 0, centerX, height);

        gc.setFill(Color.BLACK);
        gc.fillText("0", centerX + 5, centerY - 5);
    }

    private void requestRedraw() {
        if (redrawTimeline != null && redrawTimeline.getStatus() != Timeline.Status.RUNNING) {
            redrawTimeline.play(); // Запуск отложенной перерисовки
        }
    }
    public void drawGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);
        drawGrid();

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.BROWN};
        gc.setLineWidth(2);

        int i = 0;
        for (Map.Entry<Integer, String> entry : formulas.entrySet()) {
            String expression = entry.getValue();
            LexemeParser lexemeParser = new LexemeParser();
            List<Lexeme> lexemes = lexemeParser.parse(expression);
            ExpressionParser expressionParser = new ExpressionParser(lexemes);
            Expression parsedExpression = expressionParser.parse();

            gc.setStroke(colors[i % colors.length]);
            gc.beginPath();

            boolean first = true;

            double step = (maxX - minX) / (width * 2) * 0.1;

            for (double x = minX; x <= maxX; x += step) {
                try {
                    double y = parsedExpression.evaluate(Map.of("x", x));

                    double screenX = x * scale + width / 2 + offsetX;
                    double screenY = -y * scale + height / 2 + offsetY;
                    if (first) {
                        gc.moveTo(screenX, screenY);
                        first = false;
                    } else {
                        gc.lineTo(screenX, screenY);
                    }
                } catch (Exception ignored) {

                }
            }

            gc.stroke();
            i++;
        }
    }

}