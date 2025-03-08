<div id="header" align="center">
 <img alt="DesignPatternsNutshell Logo" src="logo.png" width="750">
</div>

# ReelRec: Your Personal Movie Recommender🎬🍿
<div align="center">

![Project Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE) 
![Have a nice day!](https://img.shields.io/badge/Have_a-Nice_Day!-ff69b4)

</div>

**Tired of endless scrolling and still can't find a movie to watch?  ReelRec is here to rescue your movie nights!**  This simple yet effective Java application acts as your personal movie guru, recommending films based on your tastes.

## 🌟 About ReelRec

ReelRec is a command-line movie recommendation system built as a project for the **CSE337s Software Testing** course at **Ain Shams University**. It leverages a straightforward yet powerful genre-based recommendation engine.  Tell ReelRec what movies you like, and it will suggest other fantastic films from the same genres!

**Key Features:**

*   **Genre-Based Recommendations:** Discover new movies within genres you already love. 💖
*   **Simple Input Files:**  Uses easy-to-understand `movies.txt` and `users.txt` files to manage movie data and user preferences. No complex databases needed! 📁
*   **Robust Input Validation:** ReelRec rigorously checks your input data to ensure it meets the specified format and rules, providing clear error messages to guide you. ✅
*   **Clean Output:** Generates a `recommendations.txt` file with personalized movie suggestions for each user, ready for your next movie marathon. 📝
*   **Developed in Java:**  Built using Java, ensuring cross-platform compatibility and a solid foundation. ☕
*   **Testing Focused:**  Designed with software testing principles in mind, including unit, integration, and system testing to guarantee reliability. 🧪

## 🚀 Getting Started

Ready to get your personalized movie recommendations?  Follow these simple steps to get ReelRec up and running:

**Prerequisites:**

*   **Java Development Kit (JDK) 8 or higher:** Make sure you have Java installed on your system. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/javase-downloads.html) or use a distribution like [OpenJDK](https://openjdk.java.net/).
*   **(Optional) IDE (Integrated Development Environment):** While not strictly necessary, using an IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Eclipse](https://www.eclipse.org/ide/), or [NetBeans](https://netbeans.apache.org/) can make compiling and running the application easier.

**Steps:**

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/dizzydroid/ReelRec.git
    cd ReelRec
    ```

2.  **Compile the Java Code:**
    Navigate to the project directory in your terminal and compile the Java files. If you have a `src` folder, compile from there:
    ```bash
    javac src/*.java  # If source files are in a 'src' directory
    # or
    javac *.java      # If source files are directly in the project root
    ```
    If you are using an IDE, you can usually compile the project directly within the IDE.

3.  **Prepare Input Files:**
    Ensure you have the `movies.txt` and `users.txt` files in the project's root directory (or adjust the file paths in the code if needed).  Example files are provided in this repository to get you started!  Check the project documentation (or the comments in the files) for the expected format.

4.  **Run the Application:**
    Execute the compiled Java code from your terminal:
    ```bash
    java Main  # Assuming main class is named 'Main.java' and compiled to 'Main.class'
    ```
    Again, if using an IDE, you can typically run the main class directly from the IDE.

5.  **Get Your Recommendations:**
    After running the application, a `recommendations.txt` file will be generated in the project directory. Open this file to see your personalized movie recommendations! 🎉

## 🎬 Usage

**Input Files:**

*   **`movies.txt`:** This file contains the movie database. Each movie is represented by two lines:
    *   **Line 1:**  `Movie Title, Movie ID` (e.g., `The Shawshank Redemption, TSR001`)
    *   **Line 2:** `Genre1, Genre2, Genre3, ...` (e.g., `Drama`)
    *   This pattern repeats for each movie in your database.

*   **`users.txt`:** This file defines the users and their movie preferences. Each user is represented by two lines:
    *   **Line 1:** `User Name, User ID` (e.g., `John Doe, 12345678X`)
    *   **Line 2:** `MovieID1, MovieID2, MovieID3, ...` (e.g., `TSR001, TDK003`) - Movie IDs of movies the user likes.
    *   This pattern repeats for each user.

**How Recommendations Work:**

ReelRec's recommendation engine is based on genres.  If a user likes a movie, the system identifies its genres and then recommends other movies in the database that share those genres.  It's a simple yet effective way to discover movies you're likely to enjoy!

**Error Handling:**

ReelRec is designed to be robust and handle potential errors in your input files. If there are any issues with the format or data in `movies.txt` or `users.txt`, the application will:

*   Output an error message to the `recommendations.txt` file.
*   Clearly indicate the type of error and where it occurred (e.g., "ERROR: Movie Title {Movie Title} is wrong").
*   Only report the **first error** encountered in the files to simplify debugging.

## 🧪 Testing

ReelRec has been rigorously tested using various software testing techniques to ensure its quality and reliability:

*   **Unit Testing:** Individual components of the application, such as data validation and the recommendation engine, were tested in isolation using JUnit.
*   **Integration Testing:**  The interactions between different modules were tested to ensure they work seamlessly together.
*   **System Testing:**  End-to-end testing was performed to verify the overall functionality of the application and ensure it meets all requirements.
*   **Black Box Testing:**  Testing from a user perspective, focusing on input and output without knowledge of the internal code.
*   **Data Flow Testing:** Testing based on the flow of data through the application to ensure all logic paths are covered.

## 🤝 Contributing

Contributions are welcome!  If you have ideas for improvements, bug fixes, or new features, feel free to:

1.  Fork the repository.
2.  Create a new branch for your feature or fix.
3.  Make your changes and commit them with clear, concise messages.
4.  Submit a pull request.

Let's make ReelRec even better together! 🚀

## 📜 License

This project is licensed under the [MIT License](LICENSE) - see the `LICENSE` file for details. (Replace with your chosen license if different).

## 🙏 Acknowledgments

*   This project was created as part of the **CSE337s Software Testing** course at **Ain Shams University**.
*   Special thanks to [Dr. Mona](https://eng.asu.edu.eg/en/staff/mona.ismail) for the guidance and project assignment.
*   Inspired by the desire to simplify movie recommendations and learn about software testing! 😄

## 📧 Contact

For any questions, suggestions, or feedback, feel free to reach out to any of the project contributors.  We'd love to hear from you!

**Grab your popcorn!** 🍿🎬
