package org.example.demo.parser.expression;

import lombok.RequiredArgsConstructor;
import org.example.demo.parser.Expression;


import java.util.Map;

@RequiredArgsConstructor
public class NumberExpression implements Expression {
    private final double value;

    @Override
    public double evaluate(Map<String, Double> variables) {
        return value;
    }
}
