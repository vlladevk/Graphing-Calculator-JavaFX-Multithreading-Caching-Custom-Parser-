package org.example.demo.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class LexemeParserTest {

    LexemeParser parser = new LexemeParser();

    @Test
    void parse() {
        String expression = "(1 + 2) * 3";
        List<Lexeme> lexemes = parser.parse(expression);

        assertEquals(7, lexemes.size());
        assertEquals(LexemeType.LEFT_BRACKET, lexemes.get(0).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(1).getType());
        assertEquals(LexemeType.OP_PLUS, lexemes.get(2).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(3).getType());
        assertEquals(LexemeType.RIGHT_BRACKET, lexemes.get(4).getType());
        assertEquals(LexemeType.OP_MUL, lexemes.get(5).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(6).getType());
    }

    @Test
    public void testParse_expressionWithFunction() {
        LexemeParser parser = new LexemeParser();
        String expression = "sin(2) + cos(3)";
        List<Lexeme> lexemes = parser.parse(expression);

        assertEquals(9, lexemes.size());
        assertEquals(LexemeType.OP_SIN, lexemes.get(0).getType());
        assertEquals(LexemeType.LEFT_BRACKET, lexemes.get(1).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(2).getType());
        assertEquals(LexemeType.RIGHT_BRACKET, lexemes.get(3).getType());
        assertEquals(LexemeType.OP_PLUS, lexemes.get(4).getType());
        assertEquals(LexemeType.OP_COS, lexemes.get(5).getType());
        assertEquals(LexemeType.LEFT_BRACKET, lexemes.get(6).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(7).getType());
        assertEquals(LexemeType.RIGHT_BRACKET, lexemes.get(8).getType());
    }
    @Test
    public void testParse_expressionWithDecimal() {
        LexemeParser parser = new LexemeParser();
        String expression = "2.5 + 3.5";
        List<Lexeme> lexemes = parser.parse(expression);

        assertEquals(3, lexemes.size());  // Ожидаем 5 лексем
        assertEquals(LexemeType.NUMBER, lexemes.get(0).getType());
        assertEquals(LexemeType.OP_PLUS, lexemes.get(1).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(2).getType());
    }

    @Test
    public void testParse_empty() {
        LexemeParser parser = new LexemeParser();
        String expression = "";
        assertTrue(parser.parse(expression).isEmpty());
    }

    @Test
    public void testParse_expressionWithMultipleBrackets() {
        LexemeParser parser = new LexemeParser();
        String expression = "(2 + 3)(4 - 1)";
        List<Lexeme> lexemes = parser.parse(expression);

        assertEquals(11, lexemes.size());
        assertEquals(LexemeType.LEFT_BRACKET, lexemes.get(0).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(1).getType());
        assertEquals(LexemeType.OP_PLUS, lexemes.get(2).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(3).getType());
        assertEquals(LexemeType.RIGHT_BRACKET, lexemes.get(4).getType());
        assertEquals(LexemeType.OP_MUL, lexemes.get(5).getType());
        assertEquals(LexemeType.LEFT_BRACKET, lexemes.get(6).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(7).getType());
        assertEquals(LexemeType.OP_MINUS, lexemes.get(8).getType());
        assertEquals(LexemeType.NUMBER, lexemes.get(9).getType());
        assertEquals(LexemeType.RIGHT_BRACKET, lexemes.get(10).getType());
    }

}