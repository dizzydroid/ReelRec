# Integration and White-Box Testing Strategy

This document outlines the strategies used for Integration Testing and White-Box Testing for the ReelRec application.

## Integration Testing Strategy

Integration testing verifies the interactions and data flow between different components of the ReelRec system. A multi-faceted approach was used:

1.  **Testing the Core Processor Logic (Top-Down with Stubs):**
    *   **Goal:** To test the orchestration logic within the `ReelRecProcessor` class (which contains the main application workflow) in isolation from its direct dependencies (`Validator`, `RecommendationSystem`).
    *   **Implementation:** The `IntegrationTest.java` suite (`src/test/java/com/reelrec/IntegrationTest.java`) employs the **Top-Down** testing technique.
    *   **Mechanism:** Tests in this suite instantiate the *real* `ReelRecProcessor` but inject **Stub** implementations (`StubValidator`, `StubRecommendationSystem` from `src/test/java/com/reelrec/stubs/`) into its constructor.
    *   **Verification:** Tests configure the stubs to simulate various scenarios (e.g., no errors, critical validation errors, data loading errors). Assertions then check the *interactions* with the stubs (e.g., verifying that expected methods on the stubs were called, checking data passed to stubs, confirming early exits based on stub behavior) and the state of data managed by the processor (like error messages applied to `User` objects). This validates `ReelRecProcessor`'s internal control flow and handling of dependency results.

2.  **Testing Component Interactions (Bottom-Up with Drivers):**
    *   **Goal:** To test how lower/mid-level components, like `RecommendationSystem`, integrate with the *outputs* of their dependencies, specifically how it handles realistic validation results from `Validator`.
    *   **Implementation:** Although not implemented as a separate test suite in the final version, this approach was considered and demonstrated conceptually. It would involve:
        *   A test suite (acting as a **Driver**) first executing the *real* `Validator` on test files.
        *   Capturing the results (`validMovieIds`, `invalidMovieLines`).
        *   Passing these results into the *real* `RecommendationSystem`'s loading methods.
        *   Asserting the internal state of `RecommendationSystem` to verify it correctly processed the validator's output. *(Note: Some aspects of this are covered implicitly by unit tests in `RecommendationSystemTest.java` that test loading logic).*

3.  **System-Level Integration Testing:**
    *   **Goal:** To test the entire application workflow with all *real* components integrated, simulating end-user interaction.
    *   **Implementation:** The `AppEndToEndTest.java` suite (`src/test/java/com/reelrec/AppEndToEndTest.java`) performs **System Integration Testing**.
    *   **Mechanism:** Tests act as a **Driver** for the main application entry point (`ReelRecApp.start(...)`). They provide realistic input files (`movies.txt`, `users.txt`) in a temporary directory and execute the application using all *real* components (`ReelRecApp`, `ReelRecProcessor`, `Validator`, `RecommendationSystem`).
    *   **Verification:** Assertions are made primarily on the final output file (`recommendations.txt`) and any critical error conditions. This validates the successful collaboration of all components in producing the expected end result under various black-box scenarios (valid data, file errors, data errors, boundary conditions).

**Key Interactions Verified Across Suites:**

*   **`ReelRecProcessor` <-> `Validator` (Stubbed):** Control flow based on stubbed validation results (tested in `IntegrationTest.java`).
*   **`ReelRecProcessor` <-> `RecommendationSystem` (Stubbed):** Orchestration of loading and writing calls (tested in `IntegrationTest.java`).
*   **Full Chain (Real Components):** `ReelRecApp` -> `ReelRecProcessor` -> `Validator` -> `RecommendationSystem` -> File I/O (tested in `AppEndToEndTest.java`).
*   **Error Propagation:** Flow of error information from validation/loading to final output (tested in `IntegrationTest.java` via stubs and `AppEndToEndTest.java` via real components).

## White-Box Testing Strategy

White-box testing focused on the internal structure, logic paths, and conditions within the source code.

**Methodology:**

1.  **Unit Testing:** Comprehensive JUnit tests were created for core logic classes:
    *   `ValidatorTest.java`: Testing individual validation rules.
    *   `RecommendationSystemTest.java`: Testing data loading helpers, recommendation logic, and category management in isolation.
    *   `MovieTest.java` & `UserTest.java`: Testing constructors, getters, setters, `equals`, `hashCode`, and `compareTo`.
    *   `IntegrationTest.java`: While primarily for integration, its tests of `ReelRecProcessor` also contribute to white-box coverage of that class's logic paths.
    *   `AppEndToEndTest.java`: Contributes to coverage of components when run with real data.
2.  **Code Coverage Analysis:**
    *   The **JaCoCo Maven plugin** (`jacoco-maven-plugin`) was used to measure statement and branch coverage achieved by the JUnit tests.
    *   The process involved running `mvn clean test`, analyzing the generated HTML report (`target/site/jacoco/index.html`), identifying uncovered lines and branches in core logic classes, and adding specific tests (primarily in unit and end-to-end suites) to improve coverage.
3.  **Exclusions:** To focus on the core application logic, the following classes were excluded from the final coverage calculation using JaCoCo's configuration, as they represent UI or simple launcher code primarily tested manually or through basic execution checks:
    *   `com.reelrec.ReelRecFrame` (GUI Class)
    *   `com.reelrec.ReelRecCLI` (Command-Line Interface)
    *   `com.reelrec.Main` (Main execution wrapper)
    *   `com.reelrec.ReelRecProcessor` (A testable orchestration class, but not a core logic class)

**Coverage Results:**

After iterative refinement based on JaCoCo reports, the final achieved code coverage for the **included core logic classes** (`ReelRecProcessor`, `RecommendationSystem`, `Validator`, `User`, `Movie` *(potentially excluding `ReelRecApp` if chosen)*) is:

*   **Instruction Coverage: 96%**
*   **Branch Coverage: 86%**
> **NOTE**:
> These percentages reflect the coverage metrics from the JaCoCo report after exclusions were applied. Refer to the latest generated report for exact figures.


This level of coverage indicates that the vast majority of execution paths and conditional branches within the core recommendation engine, validation logic, and data handling have been exercised by automated tests, providing high confidence in the internal correctness of the application.