package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/bad")
public class BadPracticesController {

    // ❌ Non-thread-safe static SimpleDateFormat (shared mutable)
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

    // ❌ Hardcoded credentials (security hotspot)
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "P@ssw0rd"; // NOSONAR (leave as-is to trigger)
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    // ❌ Public mutable static field
    public static String GLOBAL_STATE = "INIT";

    // ❌ Magic numbers sprinkled around
    private static final int MAGIC_TIMEOUT_MS = 1234;

    // ❌ Unused field
    private String unused = "I am never used";

    // Example endpoint that triggers several issues:
    //  - Command injection hotspot (Runtime.exec with user input)
    //  - Logging sensitive-ish info
    //  - Empty catch
    //  - Potential NPE
    @GetMapping("/exec")
    public String exec(@RequestParam(defaultValue = "echo Hello") String cmd) {
        String today = null; // ❌ will cause NPE below
        try {
            // ❌ Command execution with user input (security hotspot)
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor(MAGIC_TIMEOUT_MS); // ❌ magic number use
        } catch (Exception e) {
            // ❌ Empty catch block
        }

        // ❌ Potential NPE
        int len = today.trim().length();

        // ❌ Not closing resource (resource leak)
        try {
            FileInputStream fis = new FileInputStream(new File("README.md"));
            byte[] buf = fis.readAllBytes();
            // forgot fis.close();  ❌
            return "Read bytes: " + buf.length;
        } catch (IOException e) {
            e.printStackTrace(); // ❌ printing stack trace
            return "error";
        }
    }

    // Endpoint with SQL injection pattern and weak crypto:
    @GetMapping("/user")
    public String user(@RequestParam(defaultValue = "admin") String name) {
        try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement st = con.createStatement()) {

            // ❌ SQL injection (string concatenation)
            String sql = "SELECT USERNAME FROM USERS WHERE USERNAME = '" + name + "'";
            ResultSet rs = st.executeQuery(sql);
            return rs.next() ? rs.getString(1) : "not found";
        } catch (Exception e) {
            // ❌ Weak hashing (MD5)
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(("fallback:" + name).getBytes(StandardCharsets.UTF_8));
                return "fallback md5:" + bytesToHex(digest);
            } catch (Exception ignored) { }
            return "error";
        }
    }

    // ❌ Redundant code, poor naming and formatting
    public String dateNowBad() {
        // ❌ Using shared SimpleDateFormat in a static field
        return SDF.format(new Date());
    }

    // Helper
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ❌ TODO left in code
    // TODO: fix everything above later

    // ❌ Commented-out code left behind
    // public void dead() { System.out.println("dead"); }
}
