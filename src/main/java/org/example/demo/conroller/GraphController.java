package org.example.demo.conroller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;

import org.example.demo.model.Point;
import org.example.demo.parser.Expression;
import org.example.demo.parser.ExpressionParser;
import org.example.demo.parser.Lexeme;
import org.example.demo.parser.LexemeParser;
import org.example.demo.view.GraphView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphController {
    private final GraphView graphView;

    private final Map<Integer, String> formulas = new HashMap<>();
    @Getter
    private double scale = 30;
    @Getter
    private double offsetX = 0;
    @Getter
    private double offsetY = 0;


    private double lastMouseX;
    private double lastMouseY;
    private final Timeline redrawTimeline;
    public GraphController(GraphView graphView) {
        this.graphView = graphView;
        redrawTimeline = new Timeline(new KeyFrame(Duration.millis(5), e -> graphView.drawGraph()));
        redrawTimeline.setCycleCount(1);
        setupMouseControls();
    }

    private void setupMouseControls() {
        graphView.getCanvas().setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            requestRedraw();
        });

        graphView.getCanvas().setOnMouseDragged(event -> {
            offsetX += (event.getX() - lastMouseX);
            offsetY += (event.getY() - lastMouseY);
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            requestRedraw();
        });

        graphView.getCanvas().setOnScroll((ScrollEvent event) -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale *= zoomFactor;
            requestRedraw();
        });
    }
    public void requestRedraw() {
        if (redrawTimeline != null && redrawTimeline.getStatus() != Timeline.Status.RUNNING) {
            redrawTimeline.play();
        }
    }

    public void setGraph(int index, String value) {
        formulas.put(index, value);
    }

    public void removeGraph(int index) {
        formulas.remove(index);
        requestRedraw();
    }

    public double getMinX() {
        double width = graphView.getCanvas().getWidth();
        return (-width / 2 - offsetX) / scale;
    }

    public double getMaxX() {
        double width = graphView.getCanvas().getWidth();
        return (width / 2 - offsetX) / scale;
    }

    public double getMinY() {
        double height = graphView.getCanvas().getHeight();
        return (-height / 2 + offsetY) / scale;
    }

    public double getMaxY() {
        double height = graphView.getCanvas().getHeight();
        return  (height / 2 + offsetY) / scale;
    }

    public List<List<Point>> calculatePoints() {
        List<List<Point>> points = new ArrayList<>();
        int i = 0;

        for (Map.Entry<Integer, String> entry : formulas.entrySet()) {
            String expression = entry.getValue();
            LexemeParser lexemeParser = new LexemeParser();
            List<Lexeme> lexemes = lexemeParser.parse(expression);
            ExpressionParser expressionParser = new ExpressionParser(lexemes);
            Expression parsedExpression;
            try {
                parsedExpression = expressionParser.parse();
            } catch (Exception e) {
                continue;
            }
            List<Point> formulaPoints = new ArrayList<>();
            double step = (getMaxX() - getMinX()) / (graphView.getCanvas().getWidth() * 2) * 0.1;

            for (double x = getMinX(); x <= getMaxX(); x += step) {
                try {
                    double y = parsedExpression.evaluate(Map.of("x", x));

                    double screenX = x * scale + graphView.getCanvas().getWidth() / 2 + offsetX;
                    double screenY = -y * scale + graphView.getCanvas().getHeight() / 2 + offsetY;

                    formulaPoints.add(new Point(screenX, screenY));
                } catch (RuntimeException ignored) {

                }

            }
            points.add(formulaPoints);
            i++;
        }
        return points;
    }

}
