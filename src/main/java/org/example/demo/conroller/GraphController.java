package org.example.demo.conroller;

import javafx.scene.input.ScrollEvent;
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

    private final Map<Integer, List<Point>> cachedPoints = new HashMap<>();
    private final Map<Integer, Double> cachedSteps = new HashMap<>();

    private double lastMouseX;
    private double lastMouseY;


    private static final double MIN_SCALE = 0.05;
    private static final double MAX_SCALE = 100000;


    public GraphController(GraphView graphView) {
        this.graphView = graphView;
        setupMouseControls();
    }

    private void setupMouseControls() {
        graphView.getCanvas().setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            redraw();
        });

        graphView.getCanvas().setOnMouseDragged(event -> {
            offsetX += (event.getX() - lastMouseX);
            offsetY += (event.getY() - lastMouseY);
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            redraw();
        });


        graphView.getCanvas().setOnScroll((ScrollEvent event) -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            double targetScale = scale * zoomFactor;
            double newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, targetScale));

            double mouseX = event.getX();
            double mouseY = event.getY();

            double graphXBefore = (mouseX - graphView.getCanvas().getWidth() / 2 - offsetX) / scale;
            double graphYBefore = (mouseY - graphView.getCanvas().getHeight() / 2 - offsetY) / scale;

            scale = newScale;

            offsetX = mouseX - graphView.getCanvas().getWidth() / 2 - graphXBefore * scale;
            offsetY = mouseY - graphView.getCanvas().getHeight() / 2 - graphYBefore * scale;

            redraw();
        });
    }

    public void redraw() {
        graphView.drawGraph();
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
        redraw();
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
        double bufferFactor = 0.3;
        double minX = getMinX();
        double maxX = getMaxX();
        double delta = maxX - minX;
        double extra = delta * (bufferFactor) / 2;
        double adjustedMinX = minX - extra;
        double adjustedMaxX = maxX + extra;

        double step = (maxX - minX) / graphView.getCanvas().getWidth();
        double adjustedStep = step * (1 - bufferFactor);

        List<CalculateFunction> functions = new ArrayList<>();
        List<Integer> missingIndexes = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : formulas.entrySet()) {
            int index = entry.getKey();
            if (!cachedPoints.containsKey(index) || cachedPoints.get(index).isEmpty()) {
                functions.add(new CalculateFunction(entry.getValue(), adjustedStep, adjustedMinX, adjustedMaxX));
                missingIndexes.add(index);
            } else {
                List<Point> existingPoints = cachedPoints.get(index);
                double cachedMinX = existingPoints.getFirst().getX();
                double cachedMaxX = existingPoints.getLast().getX();
                double cachedStep = cachedSteps.getOrDefault(index, step);
                if (minX < cachedMinX || maxX > cachedMaxX || step < cachedStep ) {
                    functions.add(new CalculateFunction(entry.getValue(), adjustedStep, adjustedMinX, adjustedMaxX));
                    missingIndexes.add(index);
                }
            }
        }

        List<Future<List<Point>>> futures = executor.invokeAll(functions);
        for (int i = 0; i < functions.size(); i++) {
            List<Point> points = futures.get(i).get();
            int index = missingIndexes.get(i);
            cachedPoints.put(index, points);
            cachedSteps.put(index, adjustedStep);
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
