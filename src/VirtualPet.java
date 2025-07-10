/* VirtualPet.java with image changes for 3 pets: dog, cat, bird */

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import javax.swing.Timer;

public class VirtualPet extends JFrame {
    private int hunger = 50, health = 100, tiredness = 30, boredom = 40, cleanliness = 70;
    private final JLabel hungerLabel;
    private final JLabel healthLabel;
    private final JLabel tiredLabel;
    private final JLabel boredomLabel;
    private final JLabel cleanlinessLabel;

    private final JProgressBar hungerBar = new JProgressBar(0, 100);
    private final JProgressBar healthBar = new JProgressBar(0, 100);
    private final JProgressBar tiredBar = new JProgressBar(0, 100);
    private final JProgressBar boredomBar = new JProgressBar(0, 100);
    private final JProgressBar cleanlinessBar = new JProgressBar(0, 100);

    private final JLabel moodLabel;
    private final JLabel petImageLabel;
    private final String petImageFile;
    private final JLabel nameLabel;
    private String petName;
    private String petType;
    private Clip currentClip;
    private Clip backgroundMusicClip;
    private JTextArea thoughtLog;


    public VirtualPet(String imageFile, String name) {
        this.petImageFile = imageFile;
        this.petName = name;

        // Bar colors
        hungerBar.setForeground(Color.ORANGE);
        healthBar.setForeground(Color.GREEN);
        tiredBar.setForeground(Color.BLUE);
        boredomBar.setForeground(Color.MAGENTA);
        cleanlinessBar.setForeground(Color.CYAN);

        if (imageFile.startsWith("dog")) petType = "dog";
        else if (imageFile.startsWith("cat")) petType = "cat";
        else if (imageFile.startsWith("bird")) petType = "bird";
        else petType = "pet";

        setTitle("Your Pet: " + name);
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();

        //pet image + pet name = center
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        //position - pet image
        ImageIcon petIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + imageFile)));
        Image scaledImage = petIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        petImageLabel = new JLabel(new ImageIcon(scaledImage));
        petImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center image
        topPanel.add(petImageLabel);

        //font styles
        Font fontName = new Font("Arial", Font.BOLD, 20);
        Font font = new Font("Arial", Font.PLAIN, 16);

        //panel - display pet's name
        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name: " + petName);

        nameLabel.setFont(fontName);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center name
        topPanel.add(Box.createVerticalStrut(5)); // spacing
        topPanel.add(nameLabel);
        add(topPanel, BorderLayout.NORTH);

        //panel - pet status
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //background
        statusPanel.setBackground(new Color(255, 254, 153));

        hungerLabel = new JLabel();
        healthLabel = new JLabel();
        tiredLabel = new JLabel();
        boredomLabel = new JLabel();
        cleanlinessLabel = new JLabel();
        moodLabel = new JLabel();

        addLabelWithBar(statusPanel, "Hunger", hungerLabel, hungerBar, font);
        addLabelWithBar(statusPanel, "Health", healthLabel, healthBar, font);
        addLabelWithBar(statusPanel, "Tiredness", tiredLabel, tiredBar, font);
        addLabelWithBar(statusPanel, "Boredom", boredomLabel, boredomBar, font);
        addLabelWithBar(statusPanel, "Cleanliness", cleanlinessLabel, cleanlinessBar, font);
        moodLabel.setFont(font);
        statusPanel.add(moodLabel);

        add(statusPanel, BorderLayout.WEST);

        JPanel buttonPanel = getJPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        playPetSound(petType); // Play pet sound on start

        // Stop welcome music (from Main)
        if (Main.welcomeMusicClip != null && Main.welcomeMusicClip.isRunning()) {
            Main.welcomeMusicClip.stop();
            Main.welcomeMusicClip.close();
            Main.welcomeMusicClip = null;
        }

        // Start pet background music
        backgroundMusicClip = Main.playLoopingSound("/welcome_music.wav");


        Timer timer = new Timer(60000, event -> updatePet());
        timer.start();

        //Thought bubble
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(180, 0)); // Width only
        eastPanel.setBackground(new Color(255, 255, 240)); // Light pastel

        thoughtLog = new JTextArea();
        thoughtLog.setEditable(false);
        thoughtLog.setLineWrap(true);
        thoughtLog.setWrapStyleWord(true);
        thoughtLog.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 14));
        thoughtLog.setBackground(new Color(255, 255, 240));
        thoughtLog.setForeground(new Color(60, 60, 60));
        thoughtLog.setBorder(BorderFactory.createTitledBorder("💭 Thoughts"));

        eastPanel.add(new JScrollPane(thoughtLog), BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);


        updateLabels();
        setLocationRelativeTo(null);   //center the window
        setVisible(true);
    }

    //panel - set of actions
    private JPanel getJPanel() {
        JPanel buttonPanel = new JPanel();
        //background
        buttonPanel.setBackground(new Color(154, 76, 0));   //bg brown
        Color buttonBgColor = new Color(255, 224,163);  //pastel orange
        Color borderColor = new Color(100, 60, 0);         // Border brown

        JButton feedBtn = new JButton("Feed 🍖");
        JButton playBtn = new JButton("Play 🎾");
        JButton napBtn = new JButton("Nap 🛏");
        JButton cleanBtn = new JButton("Clean 🛁");

        // Customize all buttons consistently
        for (JButton btn : new JButton[]{feedBtn, playBtn, napBtn, cleanBtn}) {
            btn.setBackground(buttonBgColor);
            btn.setForeground(Color.DARK_GRAY);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(borderColor, 2));
            btn.setPreferredSize(new Dimension(80, 30));

        }
        feedBtn.addActionListener(event -> {
            feedPet();
            playPetSound(petType + "_eat");
        });
        playBtn.addActionListener(event -> {
            playPet();
            playPetSound(petType + "_play");
        });
        napBtn.addActionListener(event -> {
            restPet();
            playPetSound(petType + "_nap");
        });
        cleanBtn.addActionListener(event -> {
            cleanPet();
            playPetSound( "clean");
        });

        buttonPanel.add(feedBtn);
        buttonPanel.add(playBtn);
        buttonPanel.add(napBtn);
        buttonPanel.add(cleanBtn);
        return buttonPanel;
    }

    private void addThought(String message) {
        thoughtLog.append("• " + message + "\n");
        thoughtLog.setCaretPosition(thoughtLog.getDocument().getLength()); // Auto-scroll
    }


    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");

        JMenuItem saveItem = new JMenuItem("Save Pet");
        JMenuItem loadItem = new JMenuItem("Load Pet");
        JMenuItem renameItem = new JMenuItem("Rename Pet");
        JMenuItem newPetItem = new JMenuItem("Adopt New Pet");

        saveItem.addActionListener(event -> savePet());
        loadItem.addActionListener(event -> loadPet());
        renameItem.addActionListener(event -> renamePet());
        newPetItem.addActionListener(event -> {
            dispose();
            Main.showPetSelection();
        });

        menu.add(saveItem);
        menu.add(loadItem);
        menu.add(renameItem);
        menu.add(newPetItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void feedPet() {
        hunger = Math.max(hunger - 15, 0);
        tiredness = Math.min(tiredness + 5, 100);
        cleanliness = Math.max(cleanliness - 10, 0);
        setPetImageTemporarily(petType + "_eating.png");
        addThought(petName + " enjoyed a tasty treat! 🍖");
        updateLabels();
    }

    private void playPet() {
        boredom = Math.max(boredom - 20, 0);
        health = Math.min(health + 5, 100);
        tiredness = Math.min(tiredness + 10, 100);
        hunger = Math.min(hunger + 5, 100);
        cleanliness = Math.max(cleanliness - 5, 0);
        setPetImageTemporarily(petType + "_happy.png");
        addThought(petName + " had a fun time playing! 🎾");
        updateLabels();
    }

    private void restPet() {
        tiredness = Math.max(tiredness - 30, 0);
        boredom = Math.min(boredom + 5, 100);
        setPetImageTemporarily(petType + "_sleeping.png");
        addThought(petName + " is taking a well-deserved nap. 😴");
        updateLabels();
    }

    private void cleanPet() {
        cleanliness = Math.min(cleanliness + 30, 100);
        JOptionPane.showMessageDialog(this, petName + " feels clean now!");
        addThought(petName + " feels so much cleaner! 🛁");
        updateLabels();

    }

    private void updatePet() {
        hunger = Math.min(hunger + 3, 100);
        boredom = Math.min(boredom + 3, 100);
        tiredness = Math.min(tiredness + 2, 100);
        cleanliness = Math.max(cleanliness - 2, 0);

        if (hunger >= 80 || tiredness >= 90 || boredom >= 90 || cleanliness <= 30) {
            health = Math.max(health - 5, 0);
        }
        updateLabels();
    }

    private void updateLabels() {
        hungerLabel.setText("Hunger: " + hunger);
        hungerBar.setValue(hunger);

        healthLabel.setText("Health: " + health);
        healthBar.setValue(health);

        tiredLabel.setText("Tiredness: " + tiredness);
        tiredBar.setValue(tiredness);

        boredomLabel.setText("Boredom: " + boredom);
        boredomBar.setValue(boredom);

        cleanlinessLabel.setText("Cleanliness: " + cleanliness);
        cleanlinessBar.setValue(cleanliness);

        moodLabel.setText("Mood: " + getMood());
        setTitle("Your Pet: " + petName);
    }

    private String getMood() {
        if (health >= 80 && hunger <= 30 && tiredness <= 30 && boredom <= 30 && cleanliness >= 70) {
            return "(^-^) Happy";
        } else if (health < 50 || hunger >= 70 || tiredness >= 70 || boredom >= 70 || cleanliness <= 40) {
            return "(T_T) Sad";
        } else {
            return "(0_0) Okay";
        }
    }

    //Always scale new images before updating label
    private void setPetImage(String imageFileName) {
        try {
            ImageIcon rawIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + imageFileName)));
            Image scaled = rawIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            petImageLabel.setIcon(new ImageIcon(scaled));
        }
        catch (Exception e) {
            System.err.println("Image not found: " + imageFileName);
        }
    }

    private void setPetImageTemporarily(String imageFileName) {
        setPetImage(imageFileName);

        // Reset after sound ends or after 3.5 sec fallback
        int delay = (currentClip != null && currentClip.getMicrosecondLength() > 0)
                ? (int)(currentClip.getMicrosecondLength() / 1000)
                : 3500;

        //Create timer and set it
        Timer resetTimer = new Timer(delay,event -> {setPetImage(petType + "_normal.png");
        });
        resetTimer.setRepeats(false);  //to prevent looping
        resetTimer.start();
    }

    private void savePet() {
        File file = new File("pet_save.txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Pet Name: " + petName);
            writer.println("Hunger: " + hunger);
            writer.println("Health: " + health);
            writer.println("Tiredness: " + tiredness);
            writer.println("Boredom: " + boredom);
            writer.println("Cleanliness: " + cleanliness);
            JOptionPane.showMessageDialog(this, "Pet saved to pet_save.txt");
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save pet.");
        }
    }

    private void loadPet() {
        File file = new File("pet_save.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No saved pet found (pet_save.txt not found).");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            petName = reader.readLine().split(": ")[1].trim();
            hunger = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            health = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            tiredness = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            boredom = Integer.parseInt(reader.readLine().split(": ")[1].trim());
            cleanliness = Integer.parseInt(reader.readLine().split(": ")[1].trim());

            if (petImageFile.startsWith("dog")) petType = "dog";
            else if (petImageFile.startsWith("cat")) petType = "cat";
            else if (petImageFile.startsWith("bird")) petType = "bird";
            else petType = "pet";

            setPetImage(petImageFile);
            updateLabels();

            JOptionPane.showMessageDialog(this, "Pet loaded from pet_save.txt");
        }
        catch (IOException | NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load pet. Make sure pet_save.txt is correctly formatted.");
        }
    }

    private void renamePet() {
        String newName = JOptionPane.showInputDialog(this, "Enter new pet name:");
        if (newName != null && !newName.trim().isEmpty()) {
            petName = newName.trim();
            nameLabel.setText("Name: " + petName);  //update name under pet image
            updateLabels();
        }
    }

    private void addLabelWithBar(JPanel panel, String labelText, JLabel label, JProgressBar bar, Font font) {
        label.setFont(font);
        bar.setStringPainted(false);
        panel.add(label);
        panel.add(bar);
    }

    private void playPetSound(String soundKey) {
        try {

            // Stop and close any currently playing clip
            if (currentClip != null && currentClip.isRunning()) {
                currentClip.stop();
                currentClip.close();
                currentClip = null;
            }

            String soundFileName = soundKey + ".wav";
            InputStream audioSrc = getClass().getResourceAsStream("/" + soundFileName);

            if (audioSrc == null) {
                System.err.println("Sound file not found: " + soundFileName);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.setMicrosecondPosition(0);
            clip.start();

            //update sound
            currentClip = clip;

            //action image
            if (soundKey.contains("_eat")) setPetImage(petType + "_eating.png");
            else if (soundKey.contains("_play")) setPetImage(petType + "_happy.png");
            else if (soundKey.contains("_nap")) setPetImage(petType + "_sleeping.png");

            //Reset image when sound finishes
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    SwingUtilities.invokeLater(() -> {
                        if (currentClip == clip) {
                            setPetImage(petType + "_normal.png");
                            currentClip.close();
                            currentClip = null;
                        }
                    });
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to play pet sound.");
        }
    }

}