<div id="header" align="center">
  <img alt="ReelRec Logo" src="logo.png" width="750">
</div>

# ReelRec: Your Personal Movie Recommender 🎬🍿
<div align="center">

![Project Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)
![Github Build status](https://github.com/dizzydroid/reelrec/actions/workflows/ci.yml/badge.svg)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE) 
![Have a nice day!](https://img.shields.io/badge/Have_a-Nice_Day!-ff69b4)

</div>

**Tired of endless scrolling and still can't find a movie to watch?**  
ReelRec is here to rescue your movie nights! This powerful Java application acts as your personal movie guru by recommending films based on your preferences. Now available in two modes—a fully featured GUI application and a flexible CLI version—to suit your workflow.

---

## 🌟 About ReelRec

ReelRec is a movie recommendation system built with Java as part of the **CSE337s Software Testing** course at **Ain Shams University**.  
It leverages a simple genre-based recommendation engine:
- **GUI Application:** Enjoy a fully integrated window-based experience complete with a splash screen, configuration panel (with file browser support), and standard window operations.
- **CLI Application:** Run the application from the command line either non-interactively (via arguments) or interactively through console prompts.

**Key Features:**
- **Genre-Based Recommendations:** Suggests movies based on your favorite genres.
- **Robust Input Validation:** Checks and validates the format of your input files, reporting errors clearly.
- **Clean Output:** Generates a `recommendations.txt` file with personalized suggestions.
- **Dual Interface Options:** Choose between a user-friendly graphical interface and a quick command-line interface.
- **Cross-Platform:** Built in Java (JDK 8+), ensuring wide compatibility.
- **Tested for Reliability:** Developed with a strong emphasis on unit, integration, and system testing.

---

## 🚀 Getting Started

### Prerequisites
- **Java Runtime Environment (JRE) 8 or higher** – Download from [Java.com](https://www.java.com).

### Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/ReelRec.git
   cd ReelRec
   ```

2. **Build the Project:**
   Use Maven to compile and package the project:
   ```bash
   mvn clean package
   ```
   After building, you’ll find the following file in the `target` directory:
    - `reelrec-<ver>.jar` (this is the GUI application)
  
   In case you want to run the CLI version, you can edit the `pom.xml` file to include the CLI module and build it as well.
    > **NOTE**:  
    > This can be done by setting `mainClass` to `ReelRecCLI` in the `pom.xml` file.

4. **Default Input Files:**
   The `resources` folder contains sample input files:
   - `movies.txt`
   - `users.txt`
  
  - This folder is not created automatically during the build process, so you need to create it manually.

> [!TIP]
> Alternatively, you can use the pre-built release files available in the [`releases`](https://github.com/dizzydroid/ReelRec/releases) section of the repository:
> - Download the latest release and extract it to your desired location.
> - The `resources` folder will be included in the release, so you can use it directly without creating it manually.
> - The GUI application can be run directly from the extracted folder.
> - The CLI application can be run from the command line by navigating to the extracted folder and executing the jar file.
---

## 🎬 Usage

### GUI Application

1. **Navigate to the `reelrec-<ver>` Folder:**
   - Run the GUI application by either double‑clicking the jar (e.g., `reelrec-1.0.jar`) or by using the command line:
     ```bash
     java -jar reelrec-1.0.jar
     ```
2. **What to Expect:**
   - A splash screen with the title "ReelRec" (in orange) and the subtitle "Your Personal Movie Recommender" appears.
   - After a short delay, the application transitions to a configuration panel within a full-featured window. Here you can:
     - Use the default file paths (which point to the `resources` folder).
     - Click the "Browse" buttons to select custom files.
     - Click **Start Processing** to run the recommendation engine.
3. **Output:**
   - A `recommendations.txt` file is generated in the designated output directory.

### CLI Application

1. **Navigate to the `reelrec_CLI` Folder:**
   - Run the CLI version by opening a terminal and executing:
     ```bash
     java -jar reelrec_cli.jar [moviesFilePath] [usersFilePath] [recommendationsFilePath]
     ```
2. **Modes of Operation:**
   - **Non-Interactive Mode:** Supply file paths as arguments.
   - **Interactive Mode:** If no (or insufficient) arguments are provided, the application will prompt you in the console for the required paths.
   - Defaults (if no input is provided):
     - Movies File: `resources/movies.txt`
     - Users File: `resources/users.txt`
     - Recommendations Output: `recommendations.txt`

---

## 📂 Folder Structure

- **reelrec-<ver>:** GUI application.
- **reelrec_CLI:** CLI application.
- **resources:** Default input files.
- **README.md:** This file.
- **LICENSE:** License information.

---

## 🧪 Testing

ReelRec has undergone extensive testing to ensure reliability and correctness, including:

*   **Unit Testing:** Focused on individual components (`Validator`, `RecommendationSystem`, `Movie`, `User`) using JUnit 5. *(See `src/test/java/com/reelrec/`)*
*   **Integration Testing:** Verified the interaction between components using multiple strategies:
    *   Top-Down testing of the core `ReelRecProcessor` logic using Stubs (`IntegrationTest.java`).
    *   System-level integration testing of the entire application flow with real components (`AppEndToEndTest.java`).
    *   *(See `docs/integration-tests.md` for detailed strategy)*.
*   **Black-Box Testing:** Validated application functionality against requirements using techniques like equivalence partitioning and boundary value analysis, primarily implemented via `AppEndToEndTest.java`. *(See `docs/test-cases.md`)*.
*   **White-Box Testing:** Ensured internal logic paths and conditions were covered, guided by code coverage analysis. *(See `docs/integration-tests.md`)*.
*   **Data Flow Testing:** Verified implicitly through unit and integration tests ensuring data (errors, IDs, objects, recommendations) flows correctly between components under various scenarios.

### Running Tests and Viewing Reports

You can execute the full test suite and generate reports using Maven:

1.  **Open a terminal or command prompt.**
2.  **Navigate to the root directory** of the cloned ReelRec project (where the `pom.xml` file is located).
3.  **Run the Maven test command:**
    ```bash
    mvn clean test
    ```
    *   This command cleans previous builds, compiles the code, runs all JUnit tests, and (due to the POM configuration) generates the code coverage report.

4.  **Viewing Test Results:**
    *   Maven Surefire plugin generates test execution reports.
    *   Look inside the `target/surefire-reports/` directory.
    *   You'll find `.txt` and `.xml` files summarizing the test outcomes (number run, failures, errors, skipped). `TEST-*.xml` files contain detailed results for each test class.

5.  **Viewing Code Coverage Report:**
    *   The JaCoCo Maven plugin generates an HTML code coverage report.
    *   Navigate to the `target/site/jacoco/` directory.
    *   Open the **`index.html`** file in your web browser.
    *   This report provides a detailed breakdown of line and branch coverage for each class and method (excluding configured classes like the GUI, CLI, and Main). You can drill down into classes to see specific lines highlighted in green (covered), red (not covered), or yellow (partially covered branches).

---

## 🤝 Contributing

Contributions are welcome! To get involved:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes with clear and concise messages.
4. Submit a pull request.

Let's improve ReelRec together!

---

## 📜 License

This project is licensed under the [MIT License](LICENSE). See the LICENSE file for details.

---

## 🙏 Acknowledgments

- Developed as part of **CSE337s Software Testing** at **Ain Shams University**.
- Special thanks to [Dr. Mona](https://eng.asu.edu.eg/en/staff/mona.ismail) for her guidance.
- Inspired by the need for a simple yet reliable movie recommendation system.

---

## 📧 Contact

For questions, suggestions, or feedback, feel free to contact the project maintainers.

**Grab your popcorn and enjoy the show!** 🍿🎬
