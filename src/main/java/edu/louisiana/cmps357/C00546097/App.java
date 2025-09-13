package edu.louisiana.cmps357.C00546097;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// Liana Webre
// CMPS 357
// Assignment #1
// 9/12/25
// I certify that the solutions in this assignment are my own work. I have not
// shared them with any person or publicly available website before the deadline
// or within the 24-hour extension period. Any use of AI tools in the preparation
// of this assignment has been explicitly acknowledged below. I have provided a
// complete list of all tools used, along with minimal documentation describing how each
// was applied to this work.

// ------ AI Tools Used ----------
// Tool: Chat GPT
// Usage: Implemented a resizable GUI panel featuring a circular layout of draggable nodes with randomized sizes and colors.
// Verification: Verified visually and through interactive testing that nodes maintain proportional
// positions and sizes during window resizing, and that drag operations correctly update unscaled coordinates.

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

            circlePanel.exportLayout("layout.json");
            circlePanel.importLayout("layout.json");
        });
    }
}