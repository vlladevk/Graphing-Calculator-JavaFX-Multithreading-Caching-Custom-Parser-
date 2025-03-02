package org.example.demo.parser.expression;


import lombok.RequiredArgsConstructor;
import org.example.demo.parser.Expression;
import org.example.demo.parser.LexemeType;


import java.util.Map;


@RequiredArgsConstructor
public class FunctionExpression implements Expression {
    private final LexemeType function;
    private final Expression argument;

    @Override
    public double evaluate(Map<String, Double> variables) {
        double argumentValue = argument.evaluate(variables);
        return switch (function) {
            case OP_SIN -> Math.sin(argumentValue);
            case OP_COS -> Math.cos(argumentValue);
            case OP_LOG -> Math.log10(argumentValue);
            case OP_LN -> Math.log(argumentValue);
            case OP_SQRT -> Math.sqrt(argumentValue);
            case OP_MINUS -> -argumentValue;
            default -> throw new UnsupportedOperationException("Unknown function " + function);
        };
    }
}
