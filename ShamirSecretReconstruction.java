import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;

public class ShamirSecretReconstruction {

    static class SharePoint {
        BigInteger x;
        BigInteger y;

        SharePoint(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    // Parse a number string from given base into BigInteger
    static BigInteger decodeValue(String value, int base) {
        // For bases <= 36, use built-in BigInteger parsing
        if (base <= 36) {
            return new BigInteger(value.toLowerCase(), base);
        }
        // For bases > 36, parse manually digit by digit
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(base);
        for (char ch : value.toCharArray()) {
            int digit = charToDigit(ch);
            if (digit >= base)
                throw new IllegalArgumentException("Invalid digit '" + ch + "' for base " + base);
            result = result.multiply(b).add(BigInteger.valueOf(digit));
        }
        return result;
    }

    static int charToDigit(char ch) {
        if (ch >= '0' && ch <= '9') return ch - '0';
        if (ch >= 'a' && ch <= 'z') return ch - 'a' + 10;
        if (ch >= 'A' && ch <= 'Z') return ch - 'A' + 10;
        throw new IllegalArgumentException("Invalid character in number: " + ch);
    }

    // Compute the constant term of polynomial using Lagrange interpolation at x=0
    private static BigInteger lagrangeInterpolationAtZero(List<SharePoint> points) {
        BigInteger secret = BigInteger.ZERO;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE; // product of (0 - x_j)
            BigInteger denominator = BigInteger.ONE; // product of (x_i - x_j)

            for (int j = 0; j < k; j++) {
                if (j == i) continue;
                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(BigInteger.ZERO.subtract(xj));
                denominator = denominator.multiply(xi.subtract(xj));
            }

            // Perform exact division since values are integers
            BigInteger li = numerator.divide(denominator);
            secret = secret.add(yi.multiply(li));
        }

        return secret;
    }

    // Reads shares from JSON content and returns Map<x, y>
    private static Map<BigInteger, BigInteger> parseShares(String jsonContent) {
        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

        JsonObject keys = jsonObject.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();

        Map<BigInteger, BigInteger> shares = new TreeMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            if (key.equals("keys")) continue;

            BigInteger x = new BigInteger(key);
            JsonObject shareObj = entry.getValue().getAsJsonObject();

            int base = Integer.parseInt(shareObj.get("base").getAsString());
            String value = shareObj.get("value").getAsString();

            BigInteger y = decodeValue(value, base);
            shares.put(x, y);
        }

        if (shares.size() != n) {
            throw new IllegalArgumentException("Number of shares parsed does not match 'n'");
        }

        return shares;
    }

    // Selects first k shares for reconstruction
    private static List<SharePoint> selectKShares(Map<BigInteger, BigInteger> shares, int k) {
        List<SharePoint> chosen = new ArrayList<>();
        int count = 0;
        for (Map.Entry<BigInteger, BigInteger> entry : shares.entrySet()) {
            chosen.add(new SharePoint(entry.getKey(), entry.getValue()));
            count++;
            if (count == k) break;
        }
        return chosen;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ShamirSecretReconstruction <testcase1.json> <testcase2.json>");
            return;
        }

        for (int testIndex = 0; testIndex < 2; testIndex++) {
            String filePath = args[testIndex];
            String jsonContent = Files.readString(Paths.get(filePath));

            Map<BigInteger, BigInteger> shares = parseShares(jsonContent);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonObject keys = jsonObject.getAsJsonObject("keys");
            int k = keys.get("k").getAsInt();

            List<SharePoint> selectedPoints = selectKShares(shares, k);

            BigInteger secret = lagrangeInterpolationAtZero(selectedPoints);

            System.out.println("Secret for test case " + (testIndex + 1) + ": " + secret.toString());
        }
    }
}
