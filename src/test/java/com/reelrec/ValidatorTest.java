package com.reelrec;

// import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = new Validator(); // Initialize the class-level validator object
    }

    @Test
    public void testcheckMovieTitle_EmptyTitle() {
        String title = "";
        String result = validator.checkMovieTitle(title);
        assertEquals("ERROR: Movie Title \"\" is wrong", result);
    }

    @Test
    public void testcheckMovieTitle_NoWordStartsWithCapital() {
        String title = "the dark knight";
        String result = validator.checkMovieTitle(title);
        assertEquals("ERROR: Movie Title \"the dark knight\" is wrong", result);
    }

    @Test
    public void testcheckMovieTitle_AllWordsStartWithCapital() {
        String title = "The Lord Of The Rings: The Return Of The King";
        String result = validator.checkMovieTitle(title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieTitle_SomeWordsStartWithCapital() {
        String title = "Harry Potter and the Goblet of Fire";
        String result = validator.checkMovieTitle(title);
        assertEquals("ERROR: Movie Title \"Harry Potter and the Goblet of Fire\" is wrong", result);
    }

    @Test
    public void testcheckMovieTitle_OneWordStartsWithCapital() {
        String title = "Inception";
        String result = validator.checkMovieTitle(title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieTitle_OneWordDoesNotStartWithCapital() {
        String title = "inception";
        String result = validator.checkMovieTitle(title);
        assertEquals("ERROR: Movie Title \"inception\" is wrong", result);
    }

    @Test
    public void testcheckMovieTitle_titlewithnumbers() {
        String title = "Spider-Man 3";
        String result = validator.checkMovieTitle(title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieId_EmptyId() {
        String title = "The Dark Knight";
        String movieId = "";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"\" is wrong", result);
    }

    @Test
    public void testcheckMovieId_ValidId() {
        String title = "The Dark Knight";
        String movieId = "TDK003";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieId_LongValidId() {
        String title = "Spider-Man: Across The Spider-Verse";
        String movieId = "SMATSV457";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieId_OneWordValidId() {
        String title = "WALL-E";
        String movieId = "WALLE021";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("", result);
    }

    @Test
    public void testcheckMovieId_smallcaseId() {
        String title = "The Dark Knight";
        String movieId = "tdk003";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"tdk003\" is wrong", result);
    }

    @Test
    public void testcheckMovieId_MissingDigit() {
        String title = "The Dark Knight";
        String movieId = "TDK03";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"TDK03\" is wrong", result);
    }

    @Test
    public void testcheckMovieId_MissingAllDigits() {
        String title = "The Dark Knight";
        String movieId = "TDK";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"TDK\" is wrong", result);
    }

    @Test
    public void testcheckMovieId_WrongLetters() {
        String title = "The Dark Knight";
        String movieId = "TDY003";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id letters \"TDY003\" are wrong", result);
    }

    @Test
    public void testcheckMovieId_MissingLetters() {
        String title = "The Dark Knight";
        String movieId = "DK003";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id letters \"DK003\" are wrong", result);
    }

    @Test
    public void testcheckMovieId_WrongLettersAndMissingNumbers() {
        String title = "The Dark Knight";
        String movieId = "TTK03";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"TTK03\" is wrong", result);
    }

    @Test
    public void testcheckMovieId_Idwithspecialcharacter() {
        String title = "Spider-Man";
        String movieId = "$M003";
        String result = validator.checkMovieId(movieId, title);
        assertEquals("ERROR: Movie Id format \"$M003\" is wrong", result);
    }

    @Test
    public void testCheckMovieGenre_ValidGenreAction() {
        String result = validator.checkMovieGenre("ACTION");
        assertEquals("", result);
    }

    @Test
    public void testCheckMovieGenreInvalidGenreCaseInsensitive() {
        String result = validator.checkMovieGenre("aCtiOn");
        assertEquals("", result);
    }

    @Test
    public void testCheckMovieGenre_InvalidGenreMystery() {
        String result = validator.checkMovieGenre("MYSTERY");
        assertEquals("ERROR: Movie genre \"MYSTERY\" is not supported", result);
    }

    @Test
    public void testCheckMovieGenreInvalidGenreSpecialCharacters() {
        String result = validator.checkMovieGenre("ACT!ON");
        assertEquals("ERROR: Movie genre \"ACT!ON\" is not supported", result);
    }

    @Test
    public void testCheckMovieGenreEmptyGenre() {
        String result = validator.checkMovieGenre("");
        assertEquals("ERROR: Movie genre \"\" is not supported", result);
    }

    @Test
    public void testCheckUserName_ValidName() {
        String result = validator.checkUserName("John Doe");
        assertEquals("", result);
    }

    @Test
    public void testCheckUserName_ValidSingleWordName() {
        String result = validator.checkUserName("John");
        assertEquals("", result);
    }

    @Test
    public void testCheckUserName_ValidNameWithMultipleSpaces() {
        String result = validator.checkUserName("John Michael Doe");
        assertEquals("", result);
    }

    @Test
    public void testCheckUserName_InvalidNameWithNumbers() {
        String result = validator.checkUserName("John123 Doe");
        assertEquals("ERROR: User Name \"John123 Doe\" is wrong", result);
    }

    @Test
    public void testCheckUserName_InvalidNameWithSpecialCharacters() {
        String result = validator.checkUserName("John@Doe");
        assertEquals("ERROR: User Name \"John@Doe\" is wrong", result);
    }

    @Test
    public void testCheckUserName_InvalidNameStartingWithSpace() {
        String result = validator.checkUserName(" John Doe");
        assertEquals("ERROR: User Name \" John Doe\" is wrong", result);
    }

    @Test
    public void testCheckUserName_EmptyName() {
        String result = validator.checkUserName("");
        assertEquals("ERROR: User Name \"\" is wrong", result);
    }

    @Test
    public void testCheckUserName_InvalidNameWithOnlySpaces() {
        String result = validator.checkUserName("     ");
        assertEquals("ERROR: User Name \"     \" is wrong", result);
    }

    @Test
    public void testCheckUserName_ValidNameWithMultipleSpacesBetweenWords() {
        String result = validator.checkUserName("John   Doe");
        assertEquals("ERROR: User Name \"John   Doe\" is wrong", result);
    }

    @Test
    public void testCheckUserId_ValidAllNumbers() {
        String result = validator.checkUserId("123456789");
        assertEquals("", result);
    }

    @Test
    public void testCheckUserId_ValidEndsWithLetter() {
        String result = validator.checkUserId("12345678A");
        assertEquals("", result);
    }

    @Test
    public void testCheckUserId_TooShort() {
        String result = validator.checkUserId("1234567");
        assertEquals("ERROR: User Id \"1234567\" is wrong", result);
    }

    @Test
    public void testCheckUserId_TooLong() {
        String result = validator.checkUserId("1234567890");
        assertEquals("ERROR: User Id \"1234567890\" is wrong", result);
    }

    @Test
    public void testCheckUserId_StartsWithLetter() {
        String result = validator.checkUserId("A23456789");
        assertEquals("ERROR: User Id \"A23456789\" is wrong", result);
    }

    @Test
    public void testCheckUserId_HasMultipleLetters() {
        String result = validator.checkUserId("123A567BC");
        assertEquals("ERROR: User Id \"123A567BC\" is wrong", result);
    }

    @Test
    public void testCheckUserId_ContainsSpecialCharacters() {
        String result = validator.checkUserId("12345@78A");
        assertEquals("ERROR: User Id \"12345@78A\" is wrong", result);
    }

    @Test
    public void testCheckUserId_EmptyString() {
        String result = validator.checkUserId("");
        assertEquals("ERROR: User Id \"\" is wrong", result);
    }

    @Test
    public void testParseAndValidateMovies_ValidFile() {
        String filepath = "src/test/resources/longermovieswithnoerrors.txt";
        List<String> errors = validator.parseAndValidateMovies(filepath);
        assertTrue(errors.isEmpty(), "Expected no errors for a valid movies file");
    }

    @Test
    public void testParseAndValidateUsers_ValidFile() {
        // Ensure movies are validated first to populate existingMovieIds
        String moviesFilepath = "src/test/resources/longermovieswithnoerrors.txt";
        List<String> movieErrors = validator.parseAndValidateMovies(moviesFilepath);
        assertTrue(movieErrors.isEmpty());

        String usersFilepath = "src/test/resources/longeruserswithnoerrors.txt";
        List<String> userErrors = validator.parseAndValidateUsers(usersFilepath);
        assertTrue(userErrors.isEmpty(), "Expected no errors for a valid users file");
    }

    @Test
    public void testParseAndValidateMovies_ShortFile() {
        String filepath = "src/test/resources/movies_small.txt";
        List<String> errors = validator.parseAndValidateMovies(filepath);
        assertTrue(errors.isEmpty(), "Expected no errors for a valid short movies file");
    }

    @Test
    public void testParseAndValidateUsers_ShortFile() {
        // Ensure movies are validated first to populate existingMovieIds
        String moviesFilepath = "src/test/resources/movies_small.txt";
        List<String> movieErrors = validator.parseAndValidateMovies(moviesFilepath);
        assertTrue(movieErrors.isEmpty());

        String usersFilepath = "src/test/resources/users_small.txt";
        List<String> userErrors = validator.parseAndValidateUsers(usersFilepath);
        assertTrue(userErrors.isEmpty(), "Expected no errors for a valid short users file");
    }

    @Test
    public void testParseAndValidateMovies_invalidFile() {
        String filepath = "src/test/resources/Longermovieswitherrors.txt";
        List<String> errors = validator.parseAndValidateMovies(filepath);

        // Expected errors based on the issues in the file
        List<String> expectedErrors = List.of(
                "ERROR: Movie Title \"The Shawshank redemption\" is wrong at line 1 in the movies file.", // Wrong title
                "ERROR: Movie Id letters \"TR002\" are wrong at line 3 in the movies file.", // Wrong letters in ID
                "ERROR: Movie genre \"Crimea\" is not supported at line 6 in the movies file.", // Wrong genre
                "ERROR: Movie has no genres at line 8 in the movies file.", // No genres
                "ERROR: Movie Formatting is wrong at line 9 in the movies file.", // Wrong format
                "ERROR: Movie Id format \"SMATV0046\" is wrong at line 11 in the movies file.", // Wrong title
                "ERROR: Movie genre \"Familia\" is not supported at line 12 in the movies file.", // Wrong genre
                "ERROR: Movie Id numbers \"TKS003\" aren't unique at line 13 in the movies file.", // non unique ID
                                                                                                   // numbers
                "ERROR: Movie Title \"\" is wrong at line 15 in the movies file.", // Empty title
                "ERROR: Movie Title \"1917\" is wrong at line 19 in the movies file." // Title with numbers
        );

        assertEquals(expectedErrors, errors);
    }

    @Test
    public void testParseAndValidateUsers_WithInvalidMoviesFileAndValidUsersFile() {
        String moviesFilepath = "src/test/resources/Longermovieswitherrors.txt";
        List<String> movieErrors = validator.parseAndValidateMovies(moviesFilepath);
        assertFalse(movieErrors.isEmpty()); 
        String usersFilepath = "src/test/resources/longeruserswithnoerrors.txt";
        List<String> userErrors = validator.parseAndValidateUsers(usersFilepath);

        List<String> expectedErrors = List.of(
                "ERROR: Movie Id \"TSR001\" at line 2 is not in the movies file",
                "ERROR: Movie Id \"TG002\" at line 4 is not in the movies file",
                "ERROR: Movie Id \"IO009\" at line 6 is not in the movies file",
                "ERROR: Movie Id \"WALLE005\" at line 8 is not in the movies file",
                "ERROR: Movie Id \"SMATSV006\" at line 8 is not in the movies file",
                "ERROR: Movie Id \"GO008\" at line 10 is not in the movies file",
                "ERROR: Movie Id \"TKS007\" at line 12 is not in the movies file",
                "ERROR: Movie Id \"TG002\" at line 14 is not in the movies file",
                "ERROR: Movie Id \"WALLE005\" at line 16 is not in the movies file",
                "ERROR: Movie Id \"IO009\" at line 16 is not in the movies file");

        assertEquals(expectedErrors, userErrors);
    }

    @Test
    public void testParseAndValidateUsers_WithValidMoviesFileAndInvalidUsersFile() {
        // Ensure movies are validated first to populate existingMovieIds
        String moviesFilepath = "src/test/resources/longermovieswithnoerrors.txt";
        List<String> movieErrors = validator.parseAndValidateMovies(moviesFilepath);
        assertTrue(movieErrors.isEmpty()); 

        String usersFilepath = "src/test/resources/longeruserswitherrors.txt";
        List<String> userErrors = validator.parseAndValidateUsers(usersFilepath);

        List<String> expectedErrors = List.of(
                "ERROR: User Name \" Hassan Ali\" is wrong at line 1 in the users file.",
                "ERROR: User Id \"1584H32\" is wrong at line 5 in the users file.",
                "ERROR: User Formatting is wrong at line 9 in the users file.",
                "ERROR: User Id \"87654321W\" is not unique at line 11 in the users file.",
                "ERROR: User has no movies at line 14 in the users file.",
                "ERROR: User Name \"54lm4 F4thy\" is wrong at line 15 in the users file.");

        assertEquals(expectedErrors, userErrors);
    }

    @Test
    public void testParseAndValidateUsers_WithInvalidMoviesFileAndInvalidUsersFile() {
        // Ensure movies are validated first to populate existingMovieIds
        String moviesFilepath = "src/test/resources/Longermovieswitherrors.txt";
        List<String> movieErrors = validator.parseAndValidateMovies(moviesFilepath);
        assertFalse(movieErrors.isEmpty()); 

        String usersFilepath = "src/test/resources/longeruserswitherrors.txt";
        List<String> userErrors = validator.parseAndValidateUsers(usersFilepath);

        List<String> expectedErrors = List.of(
        "ERROR: User Name \" Hassan Ali\" is wrong at line 1 in the users file.",
        "ERROR: Movie Id \"TSR001\" at line 2 is not in the movies file",
        "ERROR: Movie Id \"TG002\" at line 4 is not in the movies file",
        "ERROR: User Id \"1584H32\" is wrong at line 5 in the users file.",
        "ERROR: Movie Id \"IO009\" at line 6 is not in the movies file",
        "ERROR: Movie Id \"WALLE005\" at line 8 is not in the movies file",
        "ERROR: Movie Id \"SMATSV006\" at line 8 is not in the movies file",
        "ERROR: User Formatting is wrong at line 9 in the users file.",
        "ERROR: Movie Id \"GO008\" at line 10 is not in the movies file",
        "ERROR: User Id \"87654321W\" is not unique at line 11 in the users file.",
        "ERROR: Movie Id \"TKS007\" at line 12 is not in the movies file",
        "ERROR: User has no movies at line 14 in the users file.",
        "ERROR: User Name \"54lm4 F4thy\" is wrong at line 15 in the users file.",
        "ERROR: Movie Id \"WALLE005\" at line 16 is not in the movies file",
        "ERROR: Movie Id \"IO009\" at line 16 is not in the movies file");

        assertEquals(expectedErrors, userErrors);
    }
}
