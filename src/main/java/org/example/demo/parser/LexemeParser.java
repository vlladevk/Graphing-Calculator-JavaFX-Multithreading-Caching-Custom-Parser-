package org.example.demo.parser;



import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexemeParser {
    public List<Lexeme> parse(String expression) {
        List<Lexeme> lexemes = new ArrayList<>();
        boolean parse = true;
        while (parse) {
            parse = false;
            for (LexemeType type : LexemeType.values()) {
                Pattern pattern = type.getPattern();
                if (pattern == null) continue;
                Matcher matcher = pattern.matcher(expression);
                if (matcher.find()) {
                    Lexeme next = new Lexeme(type, matcher.group().trim());
                    checkAbsentSign(lexemes, next);
                    lexemes.add(next);
                    expression = expression.substring(matcher.end());
                    parse = true;
                }
            }
        }

        if (!expression.matches("\\s*")) {
            throw new RuntimeException("Lexeme parsing failed");
        }

        return lexemes;
    }

    private void checkAbsentSign(List<Lexeme> lexemes, Lexeme next) {
        if (lexemes.isEmpty()) return;
        Lexeme Last = lexemes.getLast();
        if (Last.getType() == LexemeType.RIGHT_BRACKET ||
            Last.getType() == LexemeType.NUMBER ||
            Last.getType() == LexemeType.VARIABLE) {
            if (next.getType() == LexemeType.LEFT_BRACKET ||
                next.getType() == LexemeType.OP_SIN ||
                next.getType() == LexemeType.OP_COS ||
                next.getType() == LexemeType.OP_LN ||
                next.getType() == LexemeType.OP_LOG ||
                next.getType() == LexemeType.VARIABLE ||
                next.getType() == LexemeType.NUMBER
            ) {
                lexemes.add(new Lexeme(LexemeType.OP_MUL, "*"));
            }
        }
    }
}
