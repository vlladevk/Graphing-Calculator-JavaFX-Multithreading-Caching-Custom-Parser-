package org.example.demo.parser;


import org.example.demo.parser.expression.BinaryOperationExpression;
import org.example.demo.parser.expression.FunctionExpression;
import org.example.demo.parser.expression.NumberExpression;
import org.example.demo.parser.expression.VariableExpression;

import java.util.List;
import java.util.Objects;

// AST
/*
Expression → Term ((+ | -) Term)*
Term → Power ((* | /) Power)*
Power → Factor (^ Power)?
Factor → Number
       | Variable
       | ( Expression )
       | Function ( Expression )
       | (+ | -) Factor
Number → digits (floating-point number)
Function → (sin | cos | log | other) ( Expression )
 */


public class ExpressionParser {
    private final List<Lexeme> lexemes;
    private int currentIndex = 0;

    public ExpressionParser(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
    }

    private Lexeme current() {
        if (currentIndex >= lexemes.size()) {
            return null;
        }
        return lexemes.get(currentIndex);
    }

    private void advance() {
        ++currentIndex;
    }

    public Expression parse() {
        currentIndex = 0;
        return parseExpression();
    }


    private Expression parseExpression() {
        Expression left = parseTerm();
        while (current() != null && (Objects.requireNonNull(current()).getType() == LexemeType.OP_PLUS ||
                        Objects.requireNonNull(current()).getType() == LexemeType.OP_MINUS)) {
            LexemeType operator = Objects.requireNonNull(current()).getType();
            advance();
            Expression right = parseTerm();
            left = new BinaryOperationExpression(left, right, operator);
        }
        return left;
    }

    private Expression parsePower() {
        Expression left = parseFactor();
        if (current() != null && Objects.requireNonNull(current()).getType() == LexemeType.OP_DEGREE) {
            advance();
            Expression right = parsePower();
            left = new BinaryOperationExpression(left, right, LexemeType.OP_DEGREE);
        }
        return left;
    }

    private Expression parseTerm() {
        Expression left = parsePower();
        while (current() != null && (Objects.requireNonNull(current()).getType() == LexemeType.OP_MUL ||
                Objects.requireNonNull(current()).getType() == LexemeType.OP_DIV)) {
            LexemeType operator = Objects.requireNonNull(current()).getType();
            advance();
            Expression right = parsePower();
            left = new BinaryOperationExpression(left, right, operator);
        }
        return left;
    }


    private Expression parseFactor() {
        Lexeme signLexeme = null;
        Expression resultExp = null;
        if (Objects.requireNonNull(current()).getType() == LexemeType.OP_PLUS ||
                Objects.requireNonNull(current()).getType() == LexemeType.OP_MINUS) {
            signLexeme = Objects.requireNonNull(current());
            advance();
        }
        if (Objects.requireNonNull(current()).getType() == LexemeType.NUMBER) {
            double value = Double.parseDouble(Objects.requireNonNull(current()).getValue());
            advance();
            resultExp =  new NumberExpression(value);
        } else if (Objects.requireNonNull(current()).getType() == LexemeType.VARIABLE) {
            String nameVariable = Objects.requireNonNull(current()).getValue();
            advance();
            resultExp =  new VariableExpression(nameVariable);
        } else if (Objects.requireNonNull(current()).getType() == LexemeType.LEFT_BRACKET) {
            advance();
            Expression expr = parseExpression();
            if (Objects.requireNonNull(current()).getType() == LexemeType.RIGHT_BRACKET) {
                advance();
            }
            resultExp =  expr;
        } else if (Objects.requireNonNull(current()).getType().name().startsWith("OP_")) {
            LexemeType function = Objects.requireNonNull(current()).getType();
            advance();
            if (Objects.requireNonNull(current()).getType() == LexemeType.LEFT_BRACKET) {
                advance();
                Expression argument = parseExpression();
                if (Objects.requireNonNull(current()).getType() == LexemeType.RIGHT_BRACKET) {
                    advance();
                    resultExp = new FunctionExpression(function, argument);
                }
            }
        }
        if (resultExp != null) {
            if (signLexeme != null) {
                resultExp = new FunctionExpression(signLexeme.getType(), resultExp);
            }
            return resultExp;
        }
        throw new RuntimeException("Unexpected token: " + Objects.requireNonNull(current()).getValue());
    }
}
