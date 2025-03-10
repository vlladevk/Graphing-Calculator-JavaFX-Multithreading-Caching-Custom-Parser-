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

    @Override
    public List<Point> call() {
        try {
            LexemeParser lexemeParser = new LexemeParser();
            List<Lexeme> lexemes = lexemeParser.parse(formula);
            ExpressionParser syntaxParser = new ExpressionParser(lexemes);
            Expression expression = syntaxParser.parse();
            List<Point> points = new ArrayList<>();
            for (double x = minValue; x <= maxValue; x += step) {
                double y = expression.evaluate(Map.of("x", x));
                points.add(new Point(x, y));
            }
            return points;
        } catch (Exception e) {
            return List.of();
        }

    }
}
