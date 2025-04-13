// This class is deprecated refer to ReelRecFrame for the new implementation

package com.reelrec;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReelRecLauncher {

    // The main application frame (a fully decorated window).
    private static JFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a fully supported main window.
            mainFrame = new JFrame("ReelRec - Movie Recommender");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(400, 300);
            mainFrame.setLocationRelativeTo(null);
            // Optional: add a simple welcome label.
            JLabel welcomeLabel = new JLabel("Welcome to ReelRec!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mainFrame.getContentPane().add(welcomeLabel, BorderLayout.CENTER);
            mainFrame.setVisible(true);

            // Show the splash screen, then continue to configuration dialogs.
            showSplashScreenAndContinue();
        });
    }

    // Displays the splash screen (with title and subtitle) for 2 seconds before continuing.
    private static void showSplashScreenAndContinue() {
        // Create a JWindow with mainFrame as its owner.
        JWindow splash = new JWindow(mainFrame);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Create a panel for vertical layout of the title and subtitle.
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Title label in orange.
        JLabel titleLabel = new JLabel("ReelRec", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(Color.ORANGE);

        // Subtitle label.
        JLabel subtitleLabel = new JLabel("Your Personal Movie Recommender", SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        subtitleLabel.setForeground(Color.DARK_GRAY);

        // Add vertical glue and spacing.
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalGlue());

        content.add(panel, BorderLayout.CENTER);
        splash.getContentPane().add(content);
        splash.setSize(500, 300);
        // Center the splash screen on the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        splash.setLocation((screenSize.width - splash.getWidth()) / 2,
                           (screenSize.height - splash.getHeight()) / 2);
        splash.setVisible(true);

        // Use a Swing Timer to dispose the splash screen after 2 seconds and then show configuration.
        Timer timer = new Timer(2000, e -> {
            splash.setVisible(false);
            splash.dispose();
            showConfigurationDialog();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Displays the file-selection (configuration) dialog.
    private static void showConfigurationDialog() {
        int useCustom = JOptionPane.showConfirmDialog(mainFrame,
                "Do you want to use custom data files?",
                "ReelRec Configuration",
                JOptionPane.YES_NO_OPTION);
        if (useCustom == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(mainFrame, "Operation cancelled.");
            mainFrame.dispose();
            return;
        }

        String moviesFilePath;
        String usersFilePath;
        String recommendationsFilePath;

        if (useCustom == JOptionPane.YES_OPTION) {
            moviesFilePath = selectFile("Select Movies File");
            if (moviesFilePath == null || moviesFilePath.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No movies file selected. Exiting.");
                mainFrame.dispose();
                return;
            }
            usersFilePath = selectFile("Select Users File");
            if (usersFilePath == null || usersFilePath.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No users file selected. Exiting.");
                mainFrame.dispose();
                return;
            }
            recommendationsFilePath = selectSaveFile("Select Recommendations Output File");
            if (recommendationsFilePath == null || recommendationsFilePath.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No recommendations file selected. Exiting.");
                mainFrame.dispose();
                return;
            }
        } else {
            // Use default file paths. Ensure that, when deploying, the "resources" folder exists with these files.
            moviesFilePath = "resources/movies.txt";
            usersFilePath = "resources/users.txt";
            // Write output in the working directory.
            recommendationsFilePath = "recommendations.txt";
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Movies file: " + moviesFilePath + "\n" +
                "Users file: " + usersFilePath + "\n" +
                "Output file: " + recommendationsFilePath + "\n" +
                "Proceed?",
                "Confirm File Paths", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(mainFrame, "Operation cancelled.");
            mainFrame.dispose();
            return;
        }

        // Call the core functionality (assumed to be in ReelRecApp).
        ReelRecApp.start(moviesFilePath, usersFilePath, recommendationsFilePath);
        JOptionPane.showMessageDialog(mainFrame,
                "Processing complete.\nCheck output file: " + recommendationsFilePath);

        mainFrame.dispose();
    }

    // Opens a file chooser dialog (for opening a file) with mainFrame as the parent.
    private static String selectFile(String dialogTitle) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        int returnVal = fc.showOpenDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        }
        return null;
    }

    // Opens a file chooser dialog (for saving a file) with mainFrame as the parent.
    private static String selectSaveFile(String dialogTitle) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dialogTitle);
        int returnVal = fc.showSaveDialog(mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        }
        return null;
    }
}
