package com.crowdsimulation.controller.graphics.amenity.graphic;

import com.crowdsimulation.model.core.environment.Environment;

// Contains the location and other relevant information of a single sprite in a sprite sheet
public class GraphicLocation implements Environment {
    // Denotes the base image unit of the sprite sheet in pixels
    // i.e.: a 1x1 ratio image will be a 200x200 pixel one
    public static final int BASE_IMAGE_UNIT = 200;

    private final int graphicRow;
    private final int graphicColumn;

    private int graphicWidth;
    private int graphicHeight;

    public GraphicLocation(int graphicRow, int graphicColumn) {
        this.graphicRow = graphicRow;
        this.graphicColumn = graphicColumn;
    }

    public int getGraphicRow() {
        return graphicRow;
    }

    public int getGraphicColumn() {
        return graphicColumn;
    }

    public void setGraphicWidth(int graphicWidth) {
        this.graphicWidth = graphicWidth;
    }

    public void setGraphicHeight(int graphicHeight) {
        this.graphicHeight = graphicHeight;
    }

    public int getSourceY() {
        return graphicRow * BASE_IMAGE_UNIT;
    }

    public int getSourceX() {
        return graphicColumn * BASE_IMAGE_UNIT;
    }

    public int getSourceWidth() {
        return graphicWidth * BASE_IMAGE_UNIT;
    }

    public int getSourceHeight() {
        return graphicHeight * BASE_IMAGE_UNIT;
    }
}
