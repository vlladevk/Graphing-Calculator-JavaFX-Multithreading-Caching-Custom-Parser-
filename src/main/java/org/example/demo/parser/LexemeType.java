package org.example.demo.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Getter
public enum LexemeType {
    LEFT_BRACKET (Pattern.compile("^\\s*\\(")),
    RIGHT_BRACKET (Pattern.compile("^\\s*\\)")),
    OP_PLUS (Pattern.compile("^\\s*\\+")),
    OP_MINUS (Pattern.compile("^\\s*-")),
    OP_MUL (Pattern.compile("^\\s*\\*")),
    OP_DIV (Pattern.compile("^\\s*/")),
    OP_DEGREE (Pattern.compile("^\\s*\\^")),
    OP_SIN (Pattern.compile("^\\s*sin")),
    OP_COS (Pattern.compile("^\\s*cos")),
    OP_LOG (Pattern.compile("^\\s*log")),
    OP_LN (Pattern.compile("^\\s*ln")),
    OP_SQRT (Pattern.compile("^\\s*sqrt")),
    NUMBER (Pattern.compile("^\\s*\\d+(\\.\\d+)?")),
    VARIABLE (Pattern.compile("^\\s*[a-zA-Z]+")),
    EOF (null);
    private final Pattern pattern;
}