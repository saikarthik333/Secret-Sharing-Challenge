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
- Implements polynomial reconstruction by solving a system of linear equations using **Gaussian elimination** over big integers.
- Extracts and prints the secret constant term (`c`).
- Supports very large numbers (20-40 digits or more) robustly.
- Pure Java implementation with no external math or cryptographic libraries beyond standard Java and Google Gson for JSON parsing.

---

## Methodology Update

**Polynomial Reconstruction:**  
Originally, Lagrange interpolation was used for polynomial reconstruction. This approach has been replaced by a **matrix method** that:

1. Constructs a **Vandermonde matrix** from the `x` values of the shares.
2. Forms a linear system representing the polynomial coefficients.
3. Uses **Gaussian elimination implemented with Java's `BigInteger`** to solve the system exactly without any floating-point approximations or fractions.
4. Extracts the **constant term `c` (the secret)** from the solution vector.

This method relies solely on *normal linear algebraic calculations* over integers and aligns with your requirement to avoid using any “ready-made” interpolation formulas.

---

## Repository Contents

- `ShamirSecretReconstruction.java` — Main Java source file implementing the solver with Gaussian elimination.
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

2. Place the `testcase1.json` and `testcase2.json` files in the project root directory (or provide their full paths).

3. Compile the project:

mvn compile

text

4. Run the program with:

mvn exec:java -Dexec.mainClass=ShamirSecretReconstruction -Dexec.args="testcase1.json testcase2.json"

text

This will print the secret values for both test cases.

### Using IDE

1. Import the project as a Maven project.

2. Ensure the Gson dependency is resolved.

3. Run `ShamirSecretReconstruction` with these program arguments:

testcase1.json testcase2.json

text

---

## Example Output

For the provided test cases, the output will look like:

Secret for test case 1: 1
Secret for test case 2: 1260717229185586819189375243702254

text

*(Note: The actual secret values depend on the inputs.)*

---

## Code Highlights

- **Base decoding:**

  Supports decoding from arbitrary bases using built-in BigInteger support (for base ≤ 36) or manual digit conversion otherwise.

- **Polynomial reconstruction:**

  Utilizes **Gaussian elimination** on a Vandermonde matrix built from the `x` values of the shares to solve for all polynomial coefficients exactly with BigInteger arithmetics.

- **Exact integer arithmetic:**

  No floating-point operations or approximations, ensuring precise calculation of coefficients and secret.

- **Handles large numbers robustly:**

  BigInteger is used for all computations to safely manage large numeric values typical in cryptographic contexts.

---

## Extending the Project

You can extend this project by:

- Implementing modular arithmetic for secret sharing over finite fields if needed.
- Adding error detection and correction to identify or discard faulty shares.
- Supporting dynamic selection and verification of shares.
- Creating a user-friendly interface or integrating with blockchain wallets.

---

## References

- [Shamir's Secret Sharing on Wikipedia](https://en.wikipedia.org/wiki/Shamir%27s_Secret_Sharing)
- [Vandermonde Matrix](https://en.wikipedia.org/wiki/Vandermonde_matrix)
- [Gaussian Elimination](https://en.wikipedia.org/wiki/Gaussian_elimination)
- [Java BigInteger Documentation](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html)
- [Google Gson Library](https://github.com/google/gson)

---

## License

This project is released under the MIT License.

---

## Contact

For questions or feedback, please open an issue or contact the maintainer.

---

Thank you for exploring the Secret-Sharing-Challenge repository. Happy coding!
