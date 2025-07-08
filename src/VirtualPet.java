/* VirtualPet.java with image changes for 3 pets: dog, cat, bird */

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class VirtualPet extends JFrame {
    private int hunger = 50, health = 100, tiredness = 30, boredom = 40, cleanliness = 70;
    private final JLabel hungerLabel;
    private final JLabel healthLabel;
    private final JLabel tiredLabel;
    private final JLabel boredomLabel;
    private final JLabel cleanlinessLabel;
    private final JLabel moodLabel;
    private final JLabel petImageLabel;
    private final String petImageFile;
    private String petName;
    private String petType;

    public VirtualPet(String imageFile, String name) {
        this.petImageFile = imageFile;
        this.petName = name;

        if (imageFile.startsWith("dog")) petType = "dog";
        else if (imageFile.startsWith("cat")) petType = "cat";
        else if (imageFile.startsWith("bird")) petType = "bird";
        else petType = "pet";

        setTitle("Your Pet: " + name);
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();

        ImageIcon petIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + imageFile)));
        petImageLabel = new JLabel(petIcon);
        petImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(petImageLabel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font font = new Font("Arial", Font.PLAIN, 16);
        hungerLabel = new JLabel();
        healthLabel = new JLabel();
        tiredLabel = new JLabel();
        boredomLabel = new JLabel();
        cleanlinessLabel = new JLabel();
        moodLabel = new JLabel();

        for (JLabel label : new JLabel[]{hungerLabel, healthLabel, tiredLabel, boredomLabel, cleanlinessLabel, moodLabel}) {
            label.setFont(font);
            statusPanel.add(label);
        }

        add(statusPanel, BorderLayout.WEST);

        JPanel buttonPanel = getJPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        Timer timer = new Timer(60000, event -> updatePet());
        timer.start();

        updateLabels();
        setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel buttonPanel = new JPanel();
        JButton feedBtn = new JButton("Feed 🍖");
        JButton playBtn = new JButton("Play 🎾");
        JButton napBtn = new JButton("Nap 😴");
        JButton cleanBtn = new JButton("Clean 🛁");

        feedBtn.addActionListener(event -> feedPet());
        playBtn.addActionListener(event -> playPet());
        napBtn.addActionListener(event -> restPet());
        cleanBtn.addActionListener(event -> cleanPet());

        buttonPanel.add(feedBtn);
        buttonPanel.add(playBtn);
        buttonPanel.add(napBtn);
        buttonPanel.add(cleanBtn);
        return buttonPanel;
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
        playPetSound();
        updateLabels();
    }

    private void playPet() {
        boredom = Math.max(boredom - 20, 0);
        health = Math.min(health + 5, 100);
        tiredness = Math.min(tiredness + 10, 100);
        hunger = Math.min(hunger + 5, 100);
        cleanliness = Math.max(cleanliness - 5, 0);
        setPetImageTemporarily(petType + "_happy.png");
        playPetSound();
        updateLabels();
    }

    private void restPet() {
        tiredness = Math.max(tiredness - 30, 0);
        boredom = Math.min(boredom + 5, 100);
        setPetImageTemporarily(petType + "_sleeping.png");
        playPetSound();
        updateLabels();
    }

    private void cleanPet() {
        cleanliness = Math.min(cleanliness + 30, 100);
        playPetSound();
        JOptionPane.showMessageDialog(this, petName + " feels clean now!");
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
        healthLabel.setText("Health: " + health);
        tiredLabel.setText("Tiredness: " + tiredness);
        boredomLabel.setText("Boredom: " + boredom);
        cleanlinessLabel.setText("Cleanliness: " + cleanliness);
        moodLabel.setText("Mood: " + getMood());
        setTitle("Your Pet: " + petName);
    }

    private String getMood() {
        if (health >= 80 && hunger <= 30 && tiredness <= 30 && boredom <= 30 && cleanliness >= 70) {
            return "(^-^) Happy";
        } else if (health < 50 || hunger >= 70 || tiredness >= 70 || boredom >= 70 || cleanliness <= 40) {
            return "(T_T) Sad";
        } else if (hunger <= 60 && tiredness <= 60 && boredom <= 60 && cleanliness >= 50) {
            return "(0_0) Okay";
        }
        return "(0_0) Okay";
    }

    private void setPetImage(String imageFileName) {
        ImageIcon newIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + imageFileName)));
        petImageLabel.setIcon(newIcon);
    }

    private void setPetImageTemporarily(String imageFileName) {
        setPetImage(imageFileName);
        new Timer(3500, event -> setPetImage(petType + "_normal.png")).start();
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
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save pet.");
        }
    }

    private void loadPet() {
        File file = new File("pet_save.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No saved pet found (pet_save.txt not found)." );
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
        } catch (IOException | NumberFormatException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load pet. Make sure pet_save.txt is correctly formatted.");
        }
    }

    private void renamePet() {
        String newName = JOptionPane.showInputDialog(this, "Enter new pet name:");
        if (newName != null && !newName.trim().isEmpty()) {
            petName = newName.trim();
            updateLabels();
        }
    }

    private void playPetSound() {
        try {
            String soundFileName;
            if ("dog".equals(petType)) {
                soundFileName = "dog.wav";
            } else if ("cat".equals(petType)) {
                soundFileName = "cat.wav";
            } else if ("bird".equals(petType)) {
                soundFileName = "bird.wav";
            } else {
                return;
            }

            File soundFile = new File(Objects.requireNonNull(getClass().getResource("/" + soundFileName)).getFile());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception ex) {
            System.err.println("Failed to play pet sound.");
        }
    }
}
