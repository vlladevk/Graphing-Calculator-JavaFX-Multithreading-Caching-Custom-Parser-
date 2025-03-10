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
import java.util.stream.Collectors;

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

    private final double bufferFactor = 1.2;
    private final Map<Integer, List<Point>> cachedPoints = new HashMap<>();
    private final Map<Integer, Double> cachedSteps = new HashMap<>();

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
            double newScale = scale * zoomFactor;

            double mouseX = event.getX();
            double mouseY = event.getY();

            double graphX = (mouseX - graphView.getCanvas().getWidth() / 2 - offsetX) / scale;
            double graphY = (mouseY - graphView.getCanvas().getHeight() / 2 - offsetY) / scale;

            offsetX += graphX * (scale - newScale);
            offsetY += graphY * (scale - newScale);

            scale = newScale;
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
        cachedPoints.remove(index);
        cachedSteps.remove(index);
    }

    public void removeGraph(int index) {
        formulas.remove(index);
        cachedPoints.remove(index);
        cachedSteps.remove(index);
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

    private void calculateRawPoints() throws ExecutionException, InterruptedException {
        double minX = getMinX() * bufferFactor;
        double maxX = getMaxX() * bufferFactor;
        double step = (maxX - minX) / graphView.getCanvas().getWidth();

        List<CalculateFunction> functions = new ArrayList<>();
        List<Integer> missingIndexes = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : formulas.entrySet()) {
            int index = entry.getKey();

            if (!cachedPoints.containsKey(index) || cachedPoints.get(index).isEmpty()) {
                functions.add(new CalculateFunction(entry.getValue(), step, minX, maxX));
                missingIndexes.add(index);
            } else {
                List<Point> existingPoints = cachedPoints.get(index);
                double cachedMinX = existingPoints.getFirst().getX();
                double cachedMaxX = existingPoints.getLast().getX();
                double cachedStep = cachedSteps.getOrDefault(index, step);

                if (minX < cachedMinX || maxX > cachedMaxX || step < cachedStep) {
                    functions.add(new CalculateFunction(entry.getValue(), step, minX, maxX));
                    missingIndexes.add(index);
                }
            }
        }

        List<Future<List<Point>>> futures = executor.invokeAll(functions);
        for (int i = 0; i < functions.size(); i++) {
            List<Point> points = futures.get(i).get();
            int index = missingIndexes.get(i);
            cachedPoints.put(index, points);
            cachedSteps.put(index, step);
        }
    }

    private List<List<Point>> normalizePoints() {
        return cachedPoints.values().parallelStream()
                .map(list -> list.parallelStream()
                        .map(point -> point.normalize(scale, graphView.getCanvas().getWidth(),
                                graphView.getCanvas().getHeight(), offsetX, offsetY))
                        .collect(Collectors.toList()))
                .toList();
    }

    public List<List<Point>> calculatePoints() throws InterruptedException, ExecutionException {
        calculateRawPoints();
        return normalizePoints();
    }
}
