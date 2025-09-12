package edu.louisiana.cmps357.C00546097;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * A custom JPanel that displays and manages interactive circles (nodes).
 * The circles are initially arranged in a circular pattern and can be
 * dragged by the user using mouse interactions.
 * 
 * @author Liana Webre
 * @version 1.0
 */
public class CirclePanel extends JPanel {
    private static final int SIZE = 800;
    private static final int CIRCLE_RADIUS = 200;
    private static final int NUM_NODES = 36;
    private ArrayList<Node> nodes;
    private Node selectedNode;
    private Point lastMousePoint;

    // Track the base panel size for scaling
    private int lastWidth;
    private int lastHeight;

    /**
     * Constructs a new CirclePanel with the specified size and initializes
     * the nodes in a circular arrangement with mouse interaction capabilities.
     */
    public CirclePanel() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setBackground(Color.WHITE);
        
        nodes = new ArrayList<>();
        createNodes();

        setFocusable(true);
        requestFocusInWindow();

        setupMouseListeners();
        setupKeyBindings();

        lastWidth = SIZE;
        lastHeight = SIZE;

        // resize handler
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scaleNodes();
                repaint();
            }
        });
    }

    // --- Helper method to move the selected node ---
    private void moveSelectedNode(int dx, int dy) {
        if (selectedNode != null) {
            Point p = selectedNode.getPosition();
            selectedNode.setPosition(new Point(p.x + dx, p.y + dy));
            repaint();
        }
    }
    
    /**
     * Sets up mouse event listeners for interactive circle dragging.
     * Handles mouse press, release, and drag events to enable user interaction.
     */
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                Point mousePoint = e.getPoint();

                Node clickedNode = findNodeAt(mousePoint);
                if (clickedNode != null) {
                    selectedNode = clickedNode;
                    lastMousePoint = mousePoint;

                    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                } else {
                    // Clicked on empty space, deselect
                    selectedNode = null;
                    setCursor(java.awt.Cursor.getDefaultCursor());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                lastMousePoint = null;

                if (selectedNode != null) {
                    setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                } else {
                    setCursor(java.awt.Cursor.getDefaultCursor());
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null && lastMousePoint != null) {
                    Point currentPoint = e.getPoint();
                    int deltaX = currentPoint.x - lastMousePoint.x;
                    int deltaY = currentPoint.y - lastMousePoint.y;

                    moveSelectedNode(deltaX, deltaY);
                    
                    lastMousePoint = currentPoint;
                }
            }
        });
    }

    // --- Key bindings ---
    private void setupKeyBindings() {
        setFocusable(true);
        requestFocusInWindow();

        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};

        for (String dir : directions) {
            // normal arrow
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(dir), dir);
            getActionMap().put(dir, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int dx = 0, dy = 0;
                    switch (dir) {
                        case "UP" -> dy = -1;
                        case "DOWN" -> dy = 1;
                        case "LEFT" -> dx = -1;
                        case "RIGHT" -> dx = 1;
                    }
                    moveSelectedNode(dx, dy);
                }
            });

            // Shift + arrow
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift " + dir), "shift_" + dir);
            getActionMap().put("shift_" + dir, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int dx = 0, dy = 0;
                    int amount = 10; // shift moves by 10 pixels
                    switch (dir) {
                        case "UP" -> dy = -amount;
                        case "DOWN" -> dy = amount;
                        case "LEFT" -> dx = -amount;
                        case "RIGHT" -> dx = amount;
                    }
                    moveSelectedNode(dx, dy);
                }
            });
        }
    }

    
    /**
     * Finds the node at the specified mouse point using collision detection.
     * Returns the first node whose circular area contains the given point.
     * 
     * @param point the mouse point to check for node collision
     * @return the node at the specified point, or null if no node is found
     */
    private Node findNodeAt(Point point) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
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
     * Scale nodes continuously as the panel resizes.
     */
    private void scaleNodes() {
        int newWidth = getWidth();
        int newHeight = getHeight();

        double scaleX = (double) newWidth / lastWidth;
        double scaleY = (double) newHeight / lastHeight;

        for (Node node : nodes) {
            Point pos = node.getPosition();
            int newX = (int) Math.round(pos.x * scaleX);
            int newY = (int) Math.round(pos.y * scaleY);
            node.setPosition(new Point(newX, newY));

            // Optionally scale node radius (comment out if not desired)
            int newRadius = (int) Math.round(node.getRadius() * (scaleX + scaleY) / 2.0);
            node.setRadius(newRadius);
        }

        lastWidth = newWidth;
        lastHeight = newHeight;
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
