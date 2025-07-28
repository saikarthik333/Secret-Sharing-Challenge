text
# Secret-Sharing-Challenge

This project implements a **simplified Shamir's Secret Sharing** reconstruction algorithm to recover the secret constant term of an unknown polynomial given encoded polynomial roots. It is designed to handle large integers and supports decoding secret shares encoded in various bases.

---

## Problem Overview

Shamir's Secret Sharing is a cryptographic technique that splits a secret into multiple shares, requiring a threshold number (k) of these shares to reconstruct the secret.

In this challenge:

- You are given `n` encoded shares (roots) of an unknown polynomial of degree `m = k - 1`.
- Each share is provided as an `(x, y)` pair, where `x` is the key, and `y` is encoded in a specified base.
- Your task is to decode all shares, reconstruct the polynomial using any `k` shares, and identify the **constant term `c`** of the polynomial, which is the secret.

---

## Features

- Reads input test cases from JSON files.
- Decodes `y` values from arbitrary bases (e.g., binary, base-6, base-15, base-16).
- Uses **Lagrange interpolation** over arbitrary large integers (`BigInteger`) to reconstruct the polynomial.
- Extracts and prints the secret constant term (`c`).
- Supports very large numbers (20-40 digits or more) robustly.
- Pure Java implementation with no cryptography-specific libraries; only uses standard Java and Google Gson for JSON parsing.

---

## Repository Contents

- `ShamirSecretReconstruction.java` — Main Java source file implementing the solver.
- `testcase1.json` — Sample input JSON with smaller base-encoded shares.
- `testcase2.json` — Larger input JSON with multiple shares encoded in various bases.
- `pom.xml` — Maven project configuration with Gson dependency.
- `.gitignore` — Recommended ignore rules for Java projects.
- `README.md` — This documentation file.

---

## Requirements

- Java 11 or higher
- Maven (if building via Maven) or a compatible Java IDE (e.g., IntelliJ IDEA, Eclipse)
- Internet connection (to download Gson dependency if using Maven/Gradle)

---

## How to Build and Run

### Using Maven

1. Clone or download the repository.

2. Place the `testcase1.json` and `testcase2.json` files in the project root directory (or provide the full path).

3. Compile the project:

mvn compile

text

4. Run the program with:

mvn exec:java -Dexec.mainClass=ShamirSecretReconstruction -Dexec.args="testcase1.json testcase2.json"

text

This will print the secret values of both test cases.

### Using IDE

1. Import the project as a Maven project.

2. Add the Gson dependency if not automatically resolved.

3. Run `ShamirSecretReconstruction` with program arguments:

testcase1.json testcase2.json

text

---

## Example Output

For the provided test cases, the output will look like:

Secret for test case 1: 1
Secret for test case 2: 29453145932475923745923745923745923745

text

*(Note: The actual secret values depend on the inputs.)*

---

## Code Highlights

- **Base decoding:**

  Supports decoding from arbitrary bases using built-in BigInteger support (for base ≤ 36) or manual digit conversion otherwise.

- **Lagrange interpolation:**

  Computes the polynomial's constant term via interpolation directly at `x = 0`, using exact integer arithmetic.

- **Handling large numbers:**

  Uses Java’s `BigInteger` for all numeric computations to safely handle 256-bit or larger numbers.

---

## Extending the Project

You can extend this project by:

- Implementing modular arithmetic for finite field interpolation if required.
- Adding validation and detection of invalid/malicious shares.
- Allowing dynamic selection of which shares to use for polynomial reconstruction.
- Creating a UI or web interface for easier testing of new inputs.

---

## References

- [Shamir's Secret Sharing on Wikipedia](https://en.wikipedia.org/wiki/Shamir%27s_Secret_Sharing)
- [Lagrange Polynomial Interpolation](https://en.wikipedia.org/wiki/Lagrange_polynomial)
- [Java BigInteger Documentation](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html)
- [Google Gson Library](https://github.com/google/gson)

---

## License

This project is released under the MIT License.

---

## Contact

For any questions or feedback, please feel free to open an issue.

---

Thank you for checking out the Secret-Sharing-Challenge repository. Happy coding!
