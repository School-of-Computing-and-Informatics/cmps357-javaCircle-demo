package com.example;

import java.awt.Color;
import java.awt.Point;

/**
 * Represents a graphical node that can be displayed as a circle on the screen.
 * Each node has a position, color, and radius, and can be moved interactively.
 * 
 * @author Your Name
 * @version 1.0
 */
public class Node {
    private Point position;
    private Color color;
    private int radius;

    /**
     * Constructs a new Node with the specified position, color, and radius.
     * 
     * @param position the center point of the node
     * @param color the color of the node when rendered
     * @param radius the radius of the node in pixels
     */
    public Node(Point position, Color color, int radius) {
        this.position = position;
        this.color = color;
        this.radius = radius;
    }

    /**
     * Gets the current position of this node.
     * 
     * @return the center point of the node
     */
    public Point getPosition() { return position; }
    
    /**
     * Gets the color of this node.
     * 
     * @return the color used for rendering the node
     */
    public Color getColor() { return color; }
    
    /**
     * Gets the radius of this node.
     * 
     * @return the radius in pixels
     */
    public int getRadius() { return radius; }
    
    /**
     * Sets the position of this node to the specified point.
     * This method is used when the node is being dragged by the user.
     * 
     * @param position the new center point for the node
     */
    public void setPosition(Point position) { this.position = position; }
}
