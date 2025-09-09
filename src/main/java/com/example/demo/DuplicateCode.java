package com.example.demo;

public class DuplicateCode {

    public int score(String s) {
        if (s == null || s.isEmpty()) return 0;
        int score = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                score += 1;
            } else if (Character.isLetter(c)) {
                score += 2;
            } else {
                score += 3;
            }
        }
        return score;
    }

    // ❌ Near-duplicate on purpose
    public int scoreAgain(String s) {
        if (s == null || s.isEmpty()) return 0;
        int score = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                score += 1;
            } else if (Character.isLetter(c)) {
                score += 2;
            } else {
                score += 3;
            }
        }
        return score;
    }
}
