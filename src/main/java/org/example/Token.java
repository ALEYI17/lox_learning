package org.example;

public class Token {
    final tokenType.TokenType type;
    final String lexme;
    final Object literal;
    final int line;

    public Token(tokenType.TokenType type, String lexme, Object literal, int line) {
        this.type = type;
        this.lexme = lexme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexme='" + lexme + '\'' +
                ", literal=" + literal +
                ", liune=" + line +
                '}';
    }
}
