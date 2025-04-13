package com.reelrec;

import javax.swing.*;
import java.io.File;

public class ReelRecLauncher {

    public static void main(String[] args) {
        // Run the GUI-related code on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            int useCustom = JOptionPane.showConfirmDialog(null,
                    "Do you want to use custom data files?",
                    "ReelRec Configuration",
                    JOptionPane.YES_NO_OPTION);
            String moviesFilePath, usersFilePath, recommendationsFilePath;

            if (useCustom == JOptionPane.YES_OPTION) {
                moviesFilePath = selectFile("Select Movies File");
                if (moviesFilePath == null || moviesFilePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No movies file selected. Exiting.");
                    return;
                }
                usersFilePath = selectFile("Select Users File");
                if (usersFilePath == null || usersFilePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No users file selected. Exiting.");
                    return;
                }
                recommendationsFilePath = selectSaveFile("Select Recommendations Output File");
                if (recommendationsFilePath == null || recommendationsFilePath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No recommendations file selected. Exiting.");
                    return;
                }
            } else {
                // Use default file paths (these correspond to the development structure)
                moviesFilePath = "src/main/resources/movies.txt";
                usersFilePath = "src/main/resources/users.txt";
                // For output, write to a file in the working directory (so it's writable)
                recommendationsFilePath = "recommendations.txt";
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Movies file: " + moviesFilePath + "\n" +
                    "Users file: " + usersFilePath + "\n" +
                    "Output file: " + recommendationsFilePath + "\n" +
                    "Proceed?",
                    "Confirm File Paths", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Operation cancelled.");
                return;
            }

            // Launch the recommendation system using the provided file paths.
            ReelRecApp.start(moviesFilePath, usersFilePath, recommendationsFilePath);

            JOptionPane.showMessageDialog(null, "Processing complete.\nCheck output file: " + recommendationsFilePath);
        });
    }

    // Opens a file chooser dialog for opening a file.
    private static String selectFile(String dialogTitle) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    // Opens a file chooser dialog for selecting a file destination.
    private static String selectSaveFile(String dialogTitle) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }
}
