package com.reelrec;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class ReelRecFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Configuration panel fields
    private JTextField moviesField;
    private JTextField usersField;
    private JTextField recommendationsField;

    public ReelRecFrame() {
        super("ReelRec - Movie Recommender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Larger window
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createSplashPanel(), "Splash");
        cardPanel.add(createConfigPanel(), "Config");
        getContentPane().add(cardPanel);
    }

    // Creates a splash screen panel with text-based title and subtitle.
    private JPanel createSplashPanel() {
        JPanel splash = new JPanel(new BorderLayout());
        splash.setBackground(Color.WHITE);
        splash.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("ReelRec", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Your Personal Movie Recommender", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(subtitleLabel);
        content.add(Box.createVerticalGlue());

        splash.add(content, BorderLayout.CENTER);
        return splash;
    }

    // Creates a configuration panel with text fields and browse buttons.
    private JPanel createConfigPanel() {
        JPanel config = new JPanel(new BorderLayout());
        config.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Configuration", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 32));
        config.add(header, BorderLayout.NORTH);

        // Use GridBagLayout for form entries.
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Movies file
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Movies File:"), gbc);
        
        gbc.gridx = 1;
        moviesField = new JTextField("resources/movies.txt", 30);
        form.add(moviesField, gbc);
        
        gbc.gridx = 2;
        JButton browseMovies = new JButton("Browse");
        form.add(browseMovies, gbc);
        browseMovies.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(ReelRecFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                moviesField.setText(file.getAbsolutePath());
            }
        });

        // Users file
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Users File:"), gbc);
        
        gbc.gridx = 1;
        usersField = new JTextField("resources/users.txt", 30);
        form.add(usersField, gbc);
        
        gbc.gridx = 2;
        JButton browseUsers = new JButton("Browse");
        form.add(browseUsers, gbc);
        browseUsers.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(ReelRecFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                usersField.setText(file.getAbsolutePath());
            }
        });

        // Recommendations output file
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Output File:"), gbc);
        
        gbc.gridx = 1;
        recommendationsField = new JTextField("recommendations.txt", 30);
        form.add(recommendationsField, gbc);
        
        gbc.gridx = 2;
        JButton browseOutput = new JButton("Browse");
        form.add(browseOutput, gbc);
        browseOutput.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(ReelRecFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                recommendationsField.setText(file.getAbsolutePath());
            }
        });
        
        config.add(form, BorderLayout.CENTER);

        // Buttons panel at the bottom
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start Processing");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        config.add(buttonPanel, BorderLayout.SOUTH);

        // Start Processing: Confirm settings and invoke core functionality.
        startButton.addActionListener(e -> {
            String moviesPath = moviesField.getText().trim();
            String usersPath = usersField.getText().trim();
            String outputPath = recommendationsField.getText().trim();
            
            int confirm = JOptionPane.showConfirmDialog(ReelRecFrame.this,
                    "Movies file: " + moviesPath + "\n" +
                    "Users file: " + usersPath + "\n" +
                    "Output file: " + outputPath + "\n" +
                    "Proceed?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Run processing in a background thread.
                new Thread(() -> {
                    ReelRecApp.start(moviesPath, usersPath, outputPath);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(ReelRecFrame.this,
                                "Processing complete.\nCheck output file: " + outputPath);
                    });
                }).start();
            }
        });

        // Cancel the application
        cancelButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(ReelRecFrame.this,
                    "Are you sure you want to cancel?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        return config;
    }

    // Shows the splash screen then automatically moves to the configuration screen.
    public void showApp() {
        cardLayout.show(cardPanel, "Splash");
        Timer timer = new Timer(2000, e -> {
            cardLayout.show(cardPanel, "Config");
        });
        timer.setRepeats(false);
        timer.start();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReelRecFrame app = new ReelRecFrame();
            app.getContentPane().add(app.cardPanel, BorderLayout.CENTER);
            app.showApp();
        });
    }
}
