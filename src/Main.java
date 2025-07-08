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
        invokeLater(Main::showPetSelection);
    }

    public static void showPetSelection() {
        JFrame frame = new JFrame("Choose Your Pet");
        frame.setSize(450, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3));

        frame.add(createPetButton("Dog", "dog_normal.png"));
        frame.add(createPetButton("Cat", "cat_normal.png"));
        frame.add(createPetButton("Bird", "bird_normal.png"));

        frame.setVisible(true);
    }

    private static JButton createPetButton(String name, String imageFile) {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/" + imageFile)));
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

        return button;
    }
}