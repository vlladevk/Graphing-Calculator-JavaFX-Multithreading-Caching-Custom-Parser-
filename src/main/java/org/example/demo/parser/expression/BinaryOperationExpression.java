package org.example.demo.parser.expression;

import lombok.RequiredArgsConstructor;
import org.example.demo.parser.Expression;
import org.example.demo.parser.LexemeType;


import java.util.Map;


@RequiredArgsConstructor
public class BinaryOperationExpression implements Expression {
    private final Expression left;
    private final Expression right;
    private final LexemeType operator;


    @Override
    public double evaluate(Map<String, Double> variables) {
        double leftValue = left.evaluate(variables);
        double rightValue = right.evaluate(variables);
        return switch (operator) {
            case OP_PLUS -> leftValue + rightValue;
            case OP_MINUS -> leftValue - rightValue;
            case OP_MUL -> leftValue * rightValue;
            case OP_DIV -> leftValue / rightValue;
            case OP_DEGREE -> Math.pow(leftValue, rightValue);
            default -> throw new UnsupportedOperationException("Unknown operator " + operator);
        };
    }
}
