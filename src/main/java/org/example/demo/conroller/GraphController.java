package org.example.demo.conroller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import lombok.Getter;

import org.example.demo.model.Point;

import org.example.demo.view.GraphView;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GraphController {
    private final GraphView graphView;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<Integer, String> formulas = new TreeMap<>();
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

    public List<List<Point>> calculatePoints() throws InterruptedException, ExecutionException {
        double step = (getMaxX() - getMinX()) / (graphView.getCanvas().getWidth());
        List<CalculateFunction> functions = new ArrayList<>();
        System.out.println(step);
        double width = graphView.getCanvas().getWidth();
        double height = graphView.getCanvas().getHeight();
        for (Map.Entry<Integer, String> entry : formulas.entrySet()) {
            CalculateFunction calculateFunction = new CalculateFunction(
                    entry.getValue(), step, getMinX(),
                    getMaxX(), scale, width, height, offsetX, offsetY);
            functions.add(calculateFunction);
        }
        List<Future<List<Point>>> points = executor.invokeAll(functions);
        List<List<Point>> result = new ArrayList<>();
        for (Future<List<Point>> future : points) {
            result.add(future.get());

        }
        return result;
    }

}
