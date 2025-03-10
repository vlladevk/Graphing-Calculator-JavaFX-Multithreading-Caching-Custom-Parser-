package org.example.demo.conroller;

import lombok.RequiredArgsConstructor;
import org.example.demo.model.Point;
import org.example.demo.parser.Expression;
import org.example.demo.parser.ExpressionParser;
import org.example.demo.parser.Lexeme;
import org.example.demo.parser.LexemeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CalculateFunction implements Callable<List<Point>> {
    private final String formula;
    private final double step;
    private final double minValue;
    private final double maxValue;
    private final double scale;
    private final double width;
    private final double height;
    private final double offsetX;
    private final double offsetY;

    @Override
    public List<Point> call() {
        LexemeParser lexemeParser = new LexemeParser();
        List<Lexeme> lexemes = lexemeParser.parse(formula);
        ExpressionParser syntaxParser;
        Expression expression;
        try {
            syntaxParser = new ExpressionParser(lexemes);
            expression = syntaxParser.parse();
        } catch (Exception e) {
            return List.of();
        }

        List<Point> points = new ArrayList<>();
        for (double x = minValue; x <= maxValue; x += step) {
            double y = expression.evaluate(Map.of("x", x));
            double screenX = x * scale + width / 2 + offsetX;
            double screenY = -y * scale + height / 2 + offsetY;
            Point point = new Point(screenX, screenY);
            points.add(point);
        }

        return points;
    }
}
