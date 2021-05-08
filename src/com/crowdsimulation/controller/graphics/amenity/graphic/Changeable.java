package com.crowdsimulation.controller.graphics.amenity.graphic;

// Changeable graphics are graphics that are influenced by the object's state
// (i.e., a visual change brought about by a change in the object's state, such as how a station entrance may be open
// or closed)
public interface Changeable {
    void change();
}
