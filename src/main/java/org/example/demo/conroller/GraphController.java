package org.example.demo.conroller;

import org.example.demo.view.GraphView;

import java.util.HashMap;
import java.util.Map;

public class GraphController {
    private final GraphView graphView;
    private final Map<Integer, String> formulas = new HashMap<>();

    public GraphController(GraphView graphView) {
        this.graphView = graphView;
        setupMouseControls();
    }

    private void setupMouseControls() {
        graphView.getCanvas().setOnMousePressed(event -> {
            // Логика обработки ввода
        });

        graphView.getCanvas().setOnMouseDragged(event -> {
            // Логика обработки ввода
        });

        graphView.getCanvas().setOnScroll(event -> {
            // Логика обработки ввода
        });
    }

    public void setGraph(int index, String value) {
        formulas.put(index, value);
        //graphView.requestRedraw();
    }

    public void removeGraph(int index) {
        formulas.remove(index);
       // graphView.requestRedraw();
    }

}
