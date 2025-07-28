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
        if (base <= 36) {
            return new BigInteger(value.toLowerCase(), base);
        }
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

    // Solve the system M * A = Y where M is Vandermonde matrix of size kxk:
    // M[i][j] = (x_i)^j for j=0..k-1 (constant term is column 0)
    // A are the polynomial coefficients: a_0 (constant), a_1, ..., a_{k-1}
    // Y is vector of y_i values
    //
    // Gaussian elimination is implemented with BigInteger; exact division is expected to hold.
    private static BigInteger[] solvePolynomialCoefficients(List<SharePoint> points) {
        int k = points.size();
        BigInteger[][] mat = new BigInteger[k][k];
        BigInteger[] vec = new BigInteger[k];

        // Build Vandermonde matrix and Y vector
        for (int i = 0; i < k; i++) {
            BigInteger x = points.get(i).x;
            BigInteger power = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                mat[i][j] = power;
                power = power.multiply(x);
            }
            vec[i] = points.get(i).y;
        }

        // Gaussian Elimination to solve mat * A = vec
        for (int pivot = 0; pivot < k; pivot++) {
            // Find pivot row and swap if needed (partial pivoting for robustness)
            int maxRow = pivot;
            for (int r = pivot + 1; r < k; r++) {
                if (mat[r][pivot].abs().compareTo(mat[maxRow][pivot].abs()) > 0) {
                    maxRow = r;
                }
            }
            if (maxRow != pivot) {
                BigInteger[] tempRow = mat[pivot];
                mat[pivot] = mat[maxRow];
                mat[maxRow] = tempRow;

                BigInteger tempVal = vec[pivot];
                vec[pivot] = vec[maxRow];
                vec[maxRow] = tempVal;
            }

            if (mat[pivot][pivot].equals(BigInteger.ZERO)) {
                throw new ArithmeticException("Matrix is singular or system has no unique solution");
            }

            // Normalize pivot row (make pivot element = 1)
            BigInteger pivotVal = mat[pivot][pivot];
            for (int c = pivot; c < k; c++) {
                mat[pivot][c] = mat[pivot][c].divide(pivotVal); // exact division expected
            }
            vec[pivot] = vec[pivot].divide(pivotVal);

            // Eliminate below pivot
            for (int r = pivot + 1; r < k; r++) {
                BigInteger factor = mat[r][pivot];
                for (int c = pivot; c < k; c++) {
                    mat[r][c] = mat[r][c].subtract(factor.multiply(mat[pivot][c]));
                }
                vec[r] = vec[r].subtract(factor.multiply(vec[pivot]));
            }
        }

        // Back substitution
        BigInteger[] result = new BigInteger[k];
        for (int i = k - 1; i >= 0; i--) {
            BigInteger sum = vec[i];
            for (int j = i + 1; j < k; j++) {
                sum = sum.subtract(mat[i][j].multiply(result[j]));
            }
            result[i] = sum; // Should already be exact
        }

        return result;
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

    // Select first k shares for reconstruction
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

            BigInteger[] coefficients = solvePolynomialCoefficients(selectedPoints);

            BigInteger secret = coefficients[0];  // constant term c

            System.out.println("Secret for test case " + (testIndex + 1) + ": " + secret.toString());
        }
    }
}
