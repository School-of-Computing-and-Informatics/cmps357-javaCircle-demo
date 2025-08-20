package com.example;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

/**
 * A custom JPanel that displays and manages interactive circles (nodes).
 * The circles are initially arranged in a circular pattern and can be
 * dragged by the user using mouse interactions.
 * 
 * @author Your Name
 * @version 1.0
 */
public class CirclePanel extends JPanel {
    private static final int SIZE = 800;
    private static final int CIRCLE_RADIUS = 200;
    private static final int NUM_NODES = 36;
    private ArrayList<Node> nodes;
    private Node selectedNode;
    private Point lastMousePoint;

    /**
     * Constructs a new CirclePanel with the specified size and initializes
     * the nodes in a circular arrangement with mouse interaction capabilities.
     */
    public CirclePanel() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setBackground(Color.WHITE);
        
        nodes = new ArrayList<>();
        createNodes();
        setupMouseListeners();
    }
    
    /**
     * Sets up mouse event listeners for interactive circle dragging.
     * Handles mouse press, release, and drag events to enable user interaction.
     */
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point mousePoint = e.getPoint();
                selectedNode = findNodeAt(mousePoint);
                if (selectedNode != null) {
                    lastMousePoint = mousePoint;
                    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedNode = null;
                setCursor(java.awt.Cursor.getDefaultCursor());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null && lastMousePoint != null) {
                    Point currentPoint = e.getPoint();
                    int deltaX = currentPoint.x - lastMousePoint.x;
                    int deltaY = currentPoint.y - lastMousePoint.y;
                    
                    Point currentPos = selectedNode.getPosition();
                    Point newPos = new Point(currentPos.x + deltaX, currentPos.y + deltaY);
                    selectedNode.setPosition(newPos);
                    
                    lastMousePoint = currentPoint;
                    repaint();
                }
            }
        });
    }
    
    /**
     * Finds the node at the specified mouse point using collision detection.
     * Returns the first node whose circular area contains the given point.
     * 
     * @param point the mouse point to check for node collision
     * @return the node at the specified point, or null if no node is found
     */
    private Node findNodeAt(Point point) {
        for (Node node : nodes) {
            Point nodePos = node.getPosition();
            int radius = node.getRadius();
            double distance = Math.sqrt(
                Math.pow(point.x - nodePos.x, 2) + 
                Math.pow(point.y - nodePos.y, 2)
            );
            if (distance <= radius) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Creates and initializes the nodes in a circular arrangement.
     * Generates 36 nodes with random colors and radii, positioned evenly
     * around a circle with the specified radius.
     */
    private void createNodes() {
        Random random = new Random();
        Point center = new Point(SIZE / 2, SIZE / 2);
        
        for (int i = 0; i < NUM_NODES; i++) {
            // Calculate position on circle
            double angle = (2 * Math.PI * i) / NUM_NODES;
            int x = center.x + (int) (CIRCLE_RADIUS * Math.cos(angle));
            int y = center.y + (int) (CIRCLE_RADIUS * Math.sin(angle));
            
            // Random radius between 20 and 50
            int radius = 20 + random.nextInt(31); // 20 to 50
            
            // Random color
            Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
            
            nodes.add(new Node(new Point(x, y), color, radius));
        }
    }

    /**
     * Paints the component by rendering all nodes as filled circles.
     * The selected node (if any) is highlighted with a black border.
     * Uses anti-aliasing for smooth rendering.
     * 
     * @param g the Graphics context to use for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw all nodes
            for (Node node : nodes) {
                Point pos = node.getPosition();
                int radius = node.getRadius();
                int diameter = radius * 2;
                int x = pos.x - radius;
                int y = pos.y - radius;
                
                g2.setColor(node.getColor());
                g2.fillOval(x, y, diameter, diameter);
                
                // Highlight selected node with a border
                if (node == selectedNode) {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(2));
                    g2.drawOval(x, y, diameter, diameter);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
