//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
// Main.java for selecting between dog, cat, and bird

// Main.java for selecting between dog, cat, and bird

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static javax.swing.SwingUtilities.*;

public class Main {
    public static void main(String[] args) {
        invokeLater(Main::showWelcomeScreen);
    }

    //STARTUP WELCOME SCREEN
    public static void showWelcomeScreen(){
        JFrame welcomeFrame = new JFrame("Welcome to Virtual Pet World!");
        welcomeFrame.setSize(600, 400);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLocationRelativeTo(null);

        //load bg image
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/welcome_bg.png")));
        Image bgImage = bgIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(bgImage));
        backgroundLabel.setLayout(new BorderLayout());

        //TITLE LABEL
        JLabel title = new JLabel("🐾 Welcome to Virtual Pet World 🐾", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("Arial", Font.PLAIN, 22));

        // Set background and text color
        startBtn.setBackground(new Color(255, 204, 102));  // light orange
        startBtn.setForeground(Color.BLACK);
        title.setForeground(Color.WHITE);

        startBtn.addActionListener(e -> {
            welcomeFrame.dispose(); // Close welcome screen
            showPetSelection();     // Open pet selection
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // transparent
        centerPanel.add(startBtn);

        backgroundLabel.add(title, BorderLayout.NORTH);
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);
        welcomeFrame.setContentPane(backgroundLabel);
        welcomeFrame.setVisible(true);
    }

    public static void showPetSelection() {
        JFrame frame = new JFrame("Choose Your Pet");
        //frame.setSize(450, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3, 10, 10));

        frame.add(createPetButton("Dog", "dog_normal.png"));
        frame.add(createPetButton("Cat", "cat_normal.png"));
        frame.add(createPetButton("Bird", "bird_normal.png"));

        frame.pack();           // size to fit buttons
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    private static JButton createPetButton(String name, String imageFile) {
        ImageIcon icon = null;
        try {

            // 1. Load raw icon
            ImageIcon raw = new ImageIcon(Objects.requireNonNull(
                    Main.class.getResource("/" + imageFile)
            ));

            // 2. Scale it
            Image scaled = raw.getImage().getScaledInstance(260, 260, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        catch (Exception e) {
            System.out.println("Image not found: " + imageFile);
        }

        JButton button = new JButton(name, icon);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> {
            String petName = JOptionPane.showInputDialog("Name your " + name + ":");
            if (petName != null && !petName.trim().isEmpty()) {
                getWindowAncestor(button).dispose();
                new VirtualPet(imageFile, petName.trim());
            }
        });

        // 3. Force all buttons to the same preferred size
        button.setPreferredSize(new Dimension(250, 300));

        return button;
    }
}