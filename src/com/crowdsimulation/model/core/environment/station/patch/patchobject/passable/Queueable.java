package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable;

import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;

import java.util.List;

public interface Queueable {
    // Retrieves the floor field states of this queueable
    List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates();

    // Retrieves a floor field of this queueable, given the state
    QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState);

    // Denotes whether this queueable's floor fields are filled
    boolean isFloorFieldsComplete();

    // Delete a floor field of a certain state in this queueable
    void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState);

    // Delete all floor fields in this queueable
    void deleteAllFloorFields();

    // Retrieve the queue object
    QueueObject getQueueObject();
}
