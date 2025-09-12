package edu.louisiana.cmps357.C00546097;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main application class that creates and displays a graphical window
 * containing interactive circles arranged in a circular pattern.
 * 
 * @author Liana Webre
 * @version 1.0
 */
public class App {
    
    /**
     * Main entry point for the application.
     * Creates a JFrame window and displays the CirclePanel with interactive circles.
     * Uses SwingUtilities.invokeLater to ensure thread safety for Swing components.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Node Circle 800x800");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            CirclePanel circlePanel = new CirclePanel();
            frame.setContentPane(circlePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            circlePanel.requestFocusInWindow();
        });
    }
}