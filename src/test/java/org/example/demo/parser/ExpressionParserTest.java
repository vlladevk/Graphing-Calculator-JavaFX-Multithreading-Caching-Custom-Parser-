package org.example.demo.parser;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {
    @Test
    void testParseNumber() {
        // 42
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.NUMBER, "42")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();
        assertEquals(42.0, expr.evaluate(new HashMap<>()));
    }

    @Test
    void testParseMultiplicationAndDivision() {
        // 10 / 2 * 3
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.NUMBER, "10"),
                new Lexeme(LexemeType.OP_DIV, "/"),
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.NUMBER, "3")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();

        assertEquals(15, expr.evaluate(new HashMap<>()));
    }

    @Test
    void testParseExponentiation() {
        // 2 ^ 3
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.OP_DEGREE, "^"),
                new Lexeme(LexemeType.NUMBER, "3")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();
        assertEquals(8, expr.evaluate(new HashMap<>()));
    }

    @Test
    void testParseParentheses() {
        // (5 + 3) * 2
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "5"),
                new Lexeme(LexemeType.OP_PLUS, "+"),
                new Lexeme(LexemeType.NUMBER, "3"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.NUMBER, "2")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();

        assertEquals(16, expr.evaluate(new HashMap<>()));
    }


    @Test
    void testParseComplexExpression() {
        // sin(3+4) * cos(5-2)
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_SIN, "sin"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "3"),
                new Lexeme(LexemeType.OP_PLUS, "+"),
                new Lexeme(LexemeType.NUMBER, "4"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.OP_COS, "cos"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "5"),
                new Lexeme(LexemeType.OP_MINUS, "-"),
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();

        double expected = Math.sin(3 + 4) * Math.cos(5 - 2);
        assertEquals(expected, expr.evaluate(new HashMap<>()), 1e-9);
    }

    @Test
    void testExponentiationAssociativity() {
        //2 ^ 3 ^ 4
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.OP_DEGREE, "^"),
                new Lexeme(LexemeType.NUMBER, "3"),
                new Lexeme(LexemeType.OP_DEGREE, "^"),
                new Lexeme(LexemeType.NUMBER, "4")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();

        double expected = Math.pow(2, Math.pow(3, 4));
        assertEquals(expected, expr.evaluate(new HashMap<>()), 1e-9);
    }

    @Test
    void testBinaryMinus() {
        //-2 + 5
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_MINUS, "-"),
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.OP_PLUS, "+"),
                new Lexeme(LexemeType.NUMBER, "5")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Expression expr = parser.parse();

        assertEquals(3, expr.evaluate(new HashMap<>()), 1e-9);
    }

    @Test
    void testUnexpectedToken() {
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.NUMBER, "1")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        Exception exception = assertThrows(RuntimeException.class, parser::parse);
        assertTrue(exception.getMessage().contains("Unexpected token"));
    }

    @Test
    void testMissingRightBracket() {
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_SIN, "sin"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "0")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);
        assertThrows(RuntimeException.class, parser::parse);
    }


    @Test
    void testWithValuable() {
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_COS, "cos"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.VARIABLE, "X"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")")
        );
        ExpressionParser parser = new ExpressionParser(lexemes);

        assertEquals(0, parser.parse().evaluate(Map.of("X", Math.toRadians(90.0))), 1e-9 );
        assertEquals(1, parser.parse().evaluate(Map.of("X", 0.0)), 1e-9 );
    }

    @Test
    void testWithComplexValuable() {
        // sin (X+2) * 3 * cos(Y)
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_SIN, "sin"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.VARIABLE, "X"),
                new Lexeme(LexemeType.OP_PLUS, "+"),
                new Lexeme(LexemeType.NUMBER, "2"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.NUMBER, "3"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.OP_COS, "cos"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.VARIABLE, "Y"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")")
        );

        ExpressionParser parser = new ExpressionParser(lexemes);
        // Test case where X = 90 and Y = 0
        assertEquals(3 * Math.sin(90 + 2) * Math.cos(0), parser.parse().evaluate(Map.of("X", 90.0, "Y", 0.0)), 1e-9);

        // Test case where X = 0 and Y = 0
        assertEquals(3 * Math.sin(2) * Math.cos(0), parser.parse().evaluate(Map.of("X", 0.0, "Y", 0.0)), 1e-9);

        // Test case where X = 0 and Y = 90
        assertEquals(3 * Math.sin(2) * Math.cos(90), parser.parse().evaluate(Map.of("X", 0.0, "Y", 90.0)), 1e-9);
    }

    @Test
    void testWithLogarithmicAndSqrtOperations() {
        // log(100) * ln(e) * sqrt(16)
        List<Lexeme> lexemes = List.of(
                new Lexeme(LexemeType.OP_LOG, "log"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "100"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.OP_LN, "ln"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "2.718281828459045"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")"),
                new Lexeme(LexemeType.OP_MUL, "*"),
                new Lexeme(LexemeType.OP_SQRT, "sqrt"),
                new Lexeme(LexemeType.LEFT_BRACKET, "("),
                new Lexeme(LexemeType.NUMBER, "16"),
                new Lexeme(LexemeType.RIGHT_BRACKET, ")")
        );

        ExpressionParser parser = new ExpressionParser(lexemes);

        double result = Math.log10(100) * Math.log(2.718281828459045) * 4;
        assertEquals(result, parser.parse().evaluate(Map.of()), 1e-9);
    }
}