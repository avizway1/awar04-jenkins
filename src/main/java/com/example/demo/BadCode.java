package com.example.demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class BadCode {

    // ❌ Hardcoded secret (Security Hotspot)
    public static final String DEFAULT_PASSWORD = "Admin@123";

    // ❌ Public mutable static state
    public static String GLOBAL_STATE = "INIT";

    // ❌ Unused field
    private String unused = "never used";

    // ❌ Magic numbers and poor naming
    public int sum(int a, int b) {
        int x = 42; // magic number
        return a + b + x;
    }

    // ❌ Bad string equality
    public boolean isAdmin(String role) {
        return role == "admin"; // should use "admin".equals(role)
    }

    // ❌ Empty catch + broad exception
    public void swallow() {
        try {
            risky();
        } catch (Exception e) {
            // nothing
        }
    }

    // ❌ Weak randomness for security-like use
    public String token() {
        Random r = new Random();
        return "T-" + r.nextInt(); // should use SecureRandom
    }

    // ❌ Resource leak (never closed) — still compiles, not invoked
    public void leak() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("not-real.txt");
        // no close()
    }

    // ❌ TODO left behind
    // TODO: clean up and refactor

    private void risky() throws Exception {
        if (System.currentTimeMillis() % 2 == 0) throw new Exception("boom");
    }

    // ❌ Dead/commented code
    // public void old() { System.out.println("old"); }
}
