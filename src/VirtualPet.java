// VirtualPet.java with image changes for 3 pets: dog, cat, bird

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class VirtualPet extends JFrame {
    private int hunger = 50, health = 100, tiredness = 30, boredom = 40, cleanliness = 70;
    private JLabel hungerLabel, healthLabel, tiredLabel, boredomLabel, cleanlinessLabel, moodLabel;
    private JLabel petImageLabel;
    private String petImageFile;
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
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();

        ImageIcon petIcon = new ImageIcon(getClass().getResource("/" + imageFile));
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

        JPanel buttonPanel = new JPanel();
        JButton feedBtn = new JButton("Feed 🍖");
        JButton playBtn = new JButton("Play 🎾");
        JButton napBtn = new JButton("Nap 😴");
        JButton cleanBtn = new JButton("Clean 🧹");

        feedBtn.addActionListener(e -> feedPet());
        playBtn.addActionListener(e -> playPet());
        napBtn.addActionListener(e -> restPet());
        cleanBtn.addActionListener(e -> cleanPet());

        buttonPanel.add(feedBtn);
        buttonPanel.add(playBtn);
        buttonPanel.add(napBtn);
        buttonPanel.add(cleanBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        Timer timer = new Timer(60000, e -> updatePet());
        timer.start();

        updateLabels();
        setVisible(true);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");

        JMenuItem saveItem = new JMenuItem("Save Pet");
        JMenuItem loadItem = new JMenuItem("Load Pet");
        JMenuItem renameItem = new JMenuItem("Rename Pet");
        JMenuItem newPetItem = new JMenuItem("Adopt New Pet");

        saveItem.addActionListener(e -> savePet());
        loadItem.addActionListener(e -> loadPet());
        renameItem.addActionListener(e -> renamePet());
        newPetItem.addActionListener(e -> {
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
        updateLabels();
    }

    private void playPet() {
        boredom = Math.max(boredom - 20, 0);
        health = Math.min(health + 5, 100);
        tiredness = Math.min(tiredness + 10, 100);
        hunger = Math.min(hunger + 5, 100);
        cleanliness = Math.max(cleanliness - 5, 0);
        setPetImageTemporarily(petType + "_happy.png");
        updateLabels();
    }

    private void restPet() {
        tiredness = Math.max(tiredness - 30, 0);
        boredom = Math.min(boredom + 5, 100);
        setPetImageTemporarily(petType + "_sleeping.png");
        updateLabels();
    }

    private void cleanPet() {
        cleanliness = Math.min(cleanliness + 30, 100);
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
        if (health >= 70 && tiredness <= 30 && boredom <= 30 && cleanliness >= 60) {
            return "😊 Happy";
        } else if (health <= 40 || tiredness >= 80 || boredom >= 80 || cleanliness <= 30) {
            return "😢 Sad";
        } else {
            return "😐 Okay";
        }
    }

    private void setPetImage(String imageFileName) {
        ImageIcon newIcon = new ImageIcon(getClass().getResource("/" + imageFileName));
        petImageLabel.setIcon(newIcon);
    }

    private void setPetImageTemporarily(String imageFileName) {
        setPetImage(imageFileName);
        new Timer(3000, e -> setPetImage(petType + "_normal.png")).start();
    }

    private void savePet() {
        String defaultName = "save_" + petName.toLowerCase().replaceAll("\\s+", "") + ".txt";
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        int option = chooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Pet Name: " + petName);
                writer.println("Hunger: " + hunger);
                writer.println("Health: " + health);
                writer.println("Tiredness: " + tiredness);
                writer.println("Boredom: " + boredom);
                writer.println("Cleanliness: " + cleanliness);
                JOptionPane.showMessageDialog(this, "Pet saved to " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to save pet.");
            }
        }
    }

    private void loadPet() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                petName = reader.readLine().split(": ")[1].trim();
                petImageFile = reader.readLine().split(": ")[1].trim();
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
                JOptionPane.showMessageDialog(this, "Loaded pet from " + file.getName());
            } catch (IOException | NumberFormatException | NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Failed to load pet.\nMake sure the file is in correct format.");
            }
        }
    }


    private void renamePet() {
        String newName = JOptionPane.showInputDialog(this, "Enter new pet name:");
        if (newName != null && !newName.trim().isEmpty()) {
            petName = newName.trim();
            updateLabels();
        }
    }
}
