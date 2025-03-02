package org.example.demo.parser.expression;

import lombok.RequiredArgsConstructor;
import org.example.demo.parser.Expression;


import java.util.Map;

@RequiredArgsConstructor
public class VariableExpression implements Expression {
    private final String name;

    @Override
    public double evaluate(Map<String, Double> variables) {
        return variables.get(name);
    }
}
