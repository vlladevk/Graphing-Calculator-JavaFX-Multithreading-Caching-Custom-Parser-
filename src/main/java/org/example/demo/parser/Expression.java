package org.example.demo.parser;

import java.util.Map;

public interface Expression {
    double evaluate(Map<String, Double> variables);
}
