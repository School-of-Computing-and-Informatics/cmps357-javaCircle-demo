package edu.louisiana.cmps357.C00546097;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Timer resizeTimer;


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
        setupResizeListener();

        lastWidth = SIZE;
        lastHeight = SIZE;

    }


    /**
     * Helper method which moves the currently selected node
     * by the specified amounts in x and y directions.
     *
     * @param dx the change in the x-coordinate
     * @param dy the change in the y-coordinate
     */
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


    /**
     * Sets up key bindings for moving the selected node with arrow keys.
     * <p>
     * Normal arrow keys move the node by 1 pixel, while Shift + arrow
     * moves it by 10 pixels. Key bindings work when the panel is focused.
     * </p>
     */
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
     * Returns the topmost node containing the specified point, or {@code null} if none.
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
     * Adds a resize listener that waits 200 ms after a resize
     * before calling {@link #scaleNodes(int, int)} to scale nodes.
     */
    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (resizeTimer != null && resizeTimer.isRunning()) {
                    resizeTimer.restart();
                } else {
                    resizeTimer = new Timer(200, evt -> {
                        resizeTimer.stop();
                        scaleNodes(getWidth(), getHeight());
                    });
                    resizeTimer.setRepeats(false);
                    resizeTimer.start();
                }
            }
        });
    }


    /**
     * Scales and repositions all nodes in the panel when the panel size changes.
     * <p>
     * This method recalculates each node's position and radius based on the
     * ratio between the new target dimensions and the previous dimensions,
     * ensuring that nodes maintain their relative layout and size as the
     * panel is resized.
     * </p>
     *
     * @param targetWidth  the new width of the panel after resizing
     * @param targetHeight the new height of the panel after resizing
     *
     * @see Node
     */
    private void scaleNodes(int targetWidth, int targetHeight) {
        double scaleX = (double) targetWidth / lastWidth;
        double scaleY = (double) targetHeight / lastHeight;

        for (Node node : nodes) {
            Point pos = node.getPosition();
            int newX = (int) Math.round(pos.x * scaleX);
            int newY = (int) Math.round(pos.y * scaleY);
            node.setPosition(new Point(newX, newY));

            // Optionally scale node radius (comment out if not desired)
            int newRadius = (int) Math.round(node.getRadius() * (scaleX + scaleY) / 2.0);
            node.setRadius(newRadius);
        }

        lastWidth = targetWidth;
        lastHeight = targetHeight;
        repaint();
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


    /**
     * Exports the current circle layout to a JSON file.
     * <p>
     * Each node is written as a JSON object containing its
     * x/y position, radius, and RGB color components.  The
     * resulting file is a JSON array of these node objects.
     * </p>
     *
     * @param filename the path and file name to write to,
     *                 for example "layout.json"
     */
    public void exportLayout(String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            Point p = n.getPosition();
            Color c = n.getColor();

            sb.append(String.format(
                    "  {\"x\":%d,\"y\":%d,\"radius\":%d,\"r\":%d,\"g\":%d,\"b\":%d}",
                    p.x, p.y, n.getRadius(),
                    c.getRed(), c.getGreen(), c.getBlue()
            ));
            if (i < nodes.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Pattern NODE_PATTERN = Pattern.compile(
            "\\{\"x\":(\\d+),\"y\":(\\d+),\"radius\":(\\d+),\"r\":(\\d+),\"g\":(\\d+),\"b\":(\\d+)\\}"
    );


    /**
     * Imports a circle layout from a JSON file and updates the panel.
     * <p>
     * Reads the JSON array produced by {@link #exportLayout(String)},
     * parses each node's x/y position, radius, and RGB color, and
     * replaces the current {@code nodes} list with the loaded layout.
     * After loading, the panel is repainted and any previous selection
     * is cleared.
     * </p>
     *
     * @param filename the path and file name to read from,
     *                 for example "layout.json"
     */
    public void importLayout(String filename) {
        String json;
        try {
            json = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        nodes.clear();
        Matcher m = NODE_PATTERN.matcher(json);
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int radius = Integer.parseInt(m.group(3));
            int r = Integer.parseInt(m.group(4));
            int g = Integer.parseInt(m.group(5));
            int b = Integer.parseInt(m.group(6));

            nodes.add(new Node(new Point(x, y), new Color(r, g, b), radius));
        }

        selectedNode = null;
        lastWidth  = getWidth();
        lastHeight = getHeight();
        repaint();
    }

    /**
     * Getter for testing
     * @return nodes
     */
    public List<Node> getNodesForTest() {
        return nodes;
    }
}
