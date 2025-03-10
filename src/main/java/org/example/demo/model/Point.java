package org.example.demo.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Point {
    private final double x;
    private final double y;

    public Point normalize(double scale, double width, double height, double offsetX, double offsetY) {
        double normalizeX = x * scale + width / 2 + offsetX;
        double normalizeY = -y * scale + height / 2 + offsetY;
        return new Point(normalizeX, normalizeY);
    }
}
