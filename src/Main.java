//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
// Main.java for selecting between dog, cat, and bird

// Main.java for selecting between dog, cat, and bird

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import javax.sound.sampled.*;
import  java.io.InputStream;
import java.io.BufferedInputStream;

import static javax.swing.SwingUtilities.*;

public class Main {
    public static Clip welcomeMusicClip;

    public static void main(String[] args) {
        invokeLater(Main::showWelcomeScreen);

    }

    //STARTUP WELCOME SCREEN
    public static void showWelcomeScreen() {
        // Stop any previous music to prevent duplication
        if (welcomeMusicClip != null) {
            welcomeMusicClip.stop();
            welcomeMusicClip.close();
            welcomeMusicClip = null;
        }

        // Start welcome music
        welcomeMusicClip = playLoopingSound("/welcome_music.wav");

        JFrame welcomeFrame = new JFrame("Welcome to Virtual Pet World!");
        welcomeFrame.setSize(600, 400);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLocationRelativeTo(null);

        // Load background image
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/welcome_bg.png")));
        Image bgImage = bgIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(bgImage));
        backgroundLabel.setLayout(new BorderLayout());

        JLabel title = new JLabel("🐾 Welcome to Virtual Pet World 🐾", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("Arial", Font.PLAIN, 22));
        startBtn.setBackground(new Color(255, 204, 102));  // light orange
        startBtn.setForeground(Color.BLACK);

        startBtn.addActionListener(e -> {
            if (welcomeMusicClip != null && welcomeMusicClip.isRunning()) {
                welcomeMusicClip.stop();
                welcomeMusicClip.close();
                welcomeMusicClip = null;
            }
            playStartSound();
            welcomeFrame.dispose();
            showPetSelection();
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(startBtn);

        backgroundLabel.add(title, BorderLayout.NORTH);
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);
        welcomeFrame.setContentPane(backgroundLabel);
        welcomeFrame.setVisible(true);
    }

    public static Clip playLoopingSound(String resourcePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(Main.class.getResourceAsStream(resourcePath))
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop until stopped
            clip.start();
            return clip;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void playStartSound() {
        try {
            String soundFile = "/start.wav";
            InputStream audioSrc = Main.class.getResourceAsStream(soundFile);
            if (audioSrc == null) {
                System.err.println("Sound file not found: " + soundFile);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showPetSelection() {
        // ❗ Stop any existing music to prevent duplication
        if (welcomeMusicClip != null) {
            welcomeMusicClip.stop();
            welcomeMusicClip.close();
            welcomeMusicClip = null;
        }

        // ✅ Start welcome music
        welcomeMusicClip = playLoopingSound("/welcome_music.wav");

        JFrame frame = new JFrame("Choose Your Pet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ⬇️ Add Menu Bar with "Back to Welcome"
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem backItem = new JMenuItem("Back");

        backItem.addActionListener(e -> {
            frame.dispose();
            showWelcomeScreen();
        });

        menu.add(backItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // ⬇️ Add pet buttons
        JPanel petPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        petPanel.add(createPetButton("Dog", "dog_normal.png"));
        petPanel.add(createPetButton("Cat", "cat_normal.png"));
        petPanel.add(createPetButton("Bird", "bird_normal.png"));

        frame.add(petPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
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
                //Stop bg music when starting pet screen
                if (welcomeMusicClip != null && welcomeMusicClip.isRunning()) {
                    welcomeMusicClip.stop();
                    welcomeMusicClip.close();
                    welcomeMusicClip = null;
                }
                getWindowAncestor(button).dispose();
                new VirtualPet(imageFile, petName.trim());
            }
        });

        // 3. Force all buttons to the same preferred size
        button.setPreferredSize(new Dimension(250, 300));

        return button;
    }
}