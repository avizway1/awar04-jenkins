package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/bad")
public class BadPracticesController {

    // ❌ Non-thread-safe shared formatter
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

    // ❌ Hardcoded creds (security hotspot)
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "P@ssw0rd"; // NOSONAR (keep to trigger)
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    // ❌ Public mutable static
    public static String GLOBAL_STATE = "INIT";

    // ❌ Magic number
    private static final int MAGIC_TIMEOUT_MS = 1234;

    // ❌ Unused field
    private String unused = "I am never used";

    @GetMapping("/exec")
    public String exec(@RequestParam(defaultValue = "echo Hello") String cmd) {
        String today = null; // ❌ will cause NPE below
        try {
            // ❌ Command execution with user input (hotspot)
            Process p = Runtime.getRuntime().exec(cmd);
            // ✅ compile-safe signature; still a smell using a magic number
            p.waitFor(MAGIC_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // ❌ Empty catch
        }

        // ❌ Potential NPE
        int len = today.trim().length();

        // ❌ Resource leak: not closing stream
        try {
            FileInputStream fis = new FileInputStream(new File("README.md"));
            byte[] buf = fis.readAllBytes();
            return "Read bytes: " + buf.length;
        } catch (IOException e) {
            e.printStackTrace(); // ❌ printStackTrace
            return "error";
        }
    }

    @GetMapping("/user")
    public String user(@RequestParam(defaultValue = "admin") String name) {
        try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement st = con.createStatement()) {

            // ❌ SQL injection via concatenation
            String sql = "SELECT USERNAME FROM USERS WHERE USERNAME = '" + name + "'";
            ResultSet rs = st.executeQuery(sql);
            return rs.next() ? rs.getString(1) : "not found";
        } catch (Exception e) {
            // ❌ Weak crypto (MD5)
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(("fallback:" + name).getBytes(StandardCharsets.UTF_8));
                return "fallback md5:" + bytesToHex(digest);
            } catch (Exception ignored) { }
            return "error";
        }
    }

    public String dateNowBad() {
        // ❌ Using shared SimpleDateFormat
        return SDF.format(new Date());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // TODO: fix everything above later  ❌
    // public void dead() { System.out.println("dead"); }  ❌
}
