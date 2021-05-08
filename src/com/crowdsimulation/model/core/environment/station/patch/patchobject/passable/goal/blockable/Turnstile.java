package com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable;

import com.crowdsimulation.controller.graphics.amenity.editor.TurnstileEditor;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.GenericGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.TurnstileGraphic;
import com.crowdsimulation.model.core.agent.passenger.movement.PassengerMovement;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.QueueObject;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;

import java.util.ArrayList;
import java.util.List;

public class Turnstile extends BlockableAmenity {
    // Denotes the current mode of this turnstile
    private TurnstileMode turnstileMode;

    // Denotes the floor field states needed to access the floor fields of this turnstile
    private final QueueingFloorField.FloorFieldState turnstileFloorFieldStateBoarding;
    private final QueueingFloorField.FloorFieldState turnstileFloorFieldStateAlighting;

    // Factory for turnstile creation
    public static final TurnstileFactory turnstileFactory;

    // Handles how this turnstile is displayed
    private final TurnstileGraphic turnstileGraphic;

    // Denotes the footprint of this amenity when being drawn
    public static final AmenityFootprint turnstileFootprint;

    // Denotes the editor of this amenity
    public static final TurnstileEditor turnstileEditor;

    static {
        turnstileFactory = new TurnstileFactory();

        // Initialize this amenity's footprints
        turnstileFootprint = new AmenityFootprint();

        // Up view
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN10;
        AmenityFootprint.Rotation.AmenityBlockTemplate upBlockN11;

        AmenityFootprint.Rotation upView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.UP);

        upBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                0,
                0,
                Turnstile.class,
                true,
                false
        );

        upBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                -1,
                Turnstile.class,
                false,
                true
        );

        upBlockN10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                0,
                Turnstile.class,
                false,
                false
        );

        upBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                upView.getOrientation(),
                -1,
                1,
                Turnstile.class,
                false,
                false
        );

        upView.getAmenityBlockTemplates().add(upBlock00);
        upView.getAmenityBlockTemplates().add(upBlockN1N1);
        upView.getAmenityBlockTemplates().add(upBlockN10);
        upView.getAmenityBlockTemplates().add(upBlockN11);

        turnstileFootprint.addRotation(upView);

        // Right view
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlockN11;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock01;
        AmenityFootprint.Rotation.AmenityBlockTemplate rightBlock11;

        AmenityFootprint.Rotation rightView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.RIGHT);

        rightBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                0,
                Turnstile.class,
                true,
                false
        );

        rightBlockN11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                -1,
                1,
                Turnstile.class,
                false,
                true
        );

        rightBlock01 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                0,
                1,
                Turnstile.class,
                false,
                false
        );

        rightBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                rightView.getOrientation(),
                1,
                1,
                Turnstile.class,
                false,
                false
        );

        rightView.getAmenityBlockTemplates().add(rightBlock00);
        rightView.getAmenityBlockTemplates().add(rightBlockN11);
        rightView.getAmenityBlockTemplates().add(rightBlock01);
        rightView.getAmenityBlockTemplates().add(rightBlock11);

        turnstileFootprint.addRotation(rightView);

        // Down view
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock10;
        AmenityFootprint.Rotation.AmenityBlockTemplate downBlock11;

        AmenityFootprint.Rotation downView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.DOWN);

        downBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                0,
                0,
                Turnstile.class,
                true,
                false
        );

        downBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                -1,
                Turnstile.class,
                false,
                true
        );

        downBlock10 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                0,
                Turnstile.class,
                false,
                false
        );

        downBlock11 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                downView.getOrientation(),
                1,
                1,
                Turnstile.class,
                false,
                false
        );

        downView.getAmenityBlockTemplates().add(downBlock00);
        downView.getAmenityBlockTemplates().add(downBlock1N1);
        downView.getAmenityBlockTemplates().add(downBlock10);
        downView.getAmenityBlockTemplates().add(downBlock11);

        turnstileFootprint.addRotation(downView);

        // Left view
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock00;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlockN1N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock0N1;
        AmenityFootprint.Rotation.AmenityBlockTemplate leftBlock1N1;

        AmenityFootprint.Rotation leftView
                = new AmenityFootprint.Rotation(AmenityFootprint.Rotation.Orientation.LEFT);

        leftBlock00 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                0,
                Turnstile.class,
                true,
                false
        );

        leftBlockN1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                -1,
                -1,
                Turnstile.class,
                false,
                true
        );

        leftBlock0N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                0,
                -1,
                Turnstile.class,
                false,
                false
        );

        leftBlock1N1 = new AmenityFootprint.Rotation.AmenityBlockTemplate(
                leftView.getOrientation(),
                1,
                -1,
                Turnstile.class,
                false,
                false
        );

        leftView.getAmenityBlockTemplates().add(leftBlock00);
        leftView.getAmenityBlockTemplates().add(leftBlockN1N1);
        leftView.getAmenityBlockTemplates().add(leftBlock0N1);
        leftView.getAmenityBlockTemplates().add(leftBlock1N1);

        turnstileFootprint.addRotation(leftView);

        // Initialize the editor
        turnstileEditor = new TurnstileEditor();
    }

    protected Turnstile(
            List<AmenityBlock> amenityBlocks,
            boolean enabled,
            int waitingTime,
            boolean blockEntry,
            TurnstileMode turnstileMode
    ) {
        super(
                amenityBlocks,
                enabled,
                waitingTime,
                new QueueObject(),
                blockEntry
        );

        // Initialize this turnstile's floor field states
        this.turnstileFloorFieldStateBoarding = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.BOARDING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        this.turnstileFloorFieldStateAlighting = new QueueingFloorField.FloorFieldState(
                PassengerMovement.Direction.ALIGHTING,
                PassengerMovement.State.IN_QUEUE,
                this
        );

        this.turnstileMode = turnstileMode;

        // Add blank floor fields, one for each direction
        QueueingFloorField floorFieldBoarding = QueueingFloorField.queueingFloorFieldFactory.create(this);
        QueueingFloorField floorFieldAlighting = QueueingFloorField.queueingFloorFieldFactory.create(this);

        // Using the floor field states defined earlier, create the floor fields
        this.getQueueObject().getFloorFields().put(this.turnstileFloorFieldStateBoarding, floorFieldBoarding);
        this.getQueueObject().getFloorFields().put(this.turnstileFloorFieldStateAlighting, floorFieldAlighting);

        this.turnstileGraphic = new TurnstileGraphic(this);
    }

    public TurnstileMode getTurnstileMode() {
        return turnstileMode;
    }

    public void setTurnstileMode(TurnstileMode turnstileMode) {
        this.turnstileMode = turnstileMode;
    }

    public QueueingFloorField.FloorFieldState getTurnstileFloorFieldStateBoarding() {
        return turnstileFloorFieldStateBoarding;
    }

    public QueueingFloorField.FloorFieldState getTurnstileFloorFieldStateAlighting() {
        return turnstileFloorFieldStateAlighting;
    }

    @Override
    public String toString() {
        return "Turnstile";
    }

    @Override
    public List<QueueingFloorField.FloorFieldState> retrieveFloorFieldStates() {
        List<QueueingFloorField.FloorFieldState> floorFieldStates = new ArrayList<>();

        floorFieldStates.add(this.turnstileFloorFieldStateBoarding);
        floorFieldStates.add(this.turnstileFloorFieldStateAlighting);

        return floorFieldStates;
    }

    @Override
    public QueueingFloorField retrieveFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        return this.getQueueObject().getFloorFields().get(
                floorFieldState
        );
    }

    @Override
    // Denotes whether the floor field for this turnstile is complete
    public boolean isFloorFieldsComplete() {
        QueueingFloorField boardingFloorField;
        QueueingFloorField alightingFloorField;

        boolean boardingCheck;
        boolean alightingCheck;

        boardingFloorField = retrieveFloorField(turnstileFloorFieldStateBoarding);
        alightingFloorField = retrieveFloorField(turnstileFloorFieldStateAlighting);

        // Despite the actual mode of the turnstile, always require that the user fill all (boarding and alighting)
        // floor fields
        boardingCheck = boardingFloorField.getApex() != null && !boardingFloorField.getAssociatedPatches().isEmpty();
        alightingCheck = alightingFloorField.getApex() != null && !alightingFloorField.getAssociatedPatches().isEmpty();

        // The floor field of this queueable is complete when, for both floor fields, there are floor fields present and
        // apex patches are present
        return boardingCheck && alightingCheck;
    }

    // Clear all floor fields of the given floor field state in this turnstile
    @Override
    public void deleteFloorField(QueueingFloorField.FloorFieldState floorFieldState) {
        QueueingFloorField boardingFloorField = retrieveFloorField(floorFieldState);

        QueueingFloorField.clearFloorField(
                boardingFloorField,
                floorFieldState
        );
    }

    @Override
    public void deleteAllFloorFields() {
        // Sweep through each and every floor field and delete them
        List<QueueingFloorField.FloorFieldState> floorFieldStates = retrieveFloorFieldStates();

        for (QueueingFloorField.FloorFieldState floorFieldState : floorFieldStates) {
            deleteFloorField(floorFieldState);
        }
    }

    @Override
    public AmenityGraphic getGraphicObject() {
        return this.turnstileGraphic;
    }

    @Override
    public String getGraphicURL() {
        return this.turnstileGraphic.getGraphicURL();
    }

    // Turnstile block
    public static class TurnstileBlock extends Amenity.AmenityBlock {
        public static Turnstile.TurnstileBlock.TurnstileBlockFactory turnstileBlockFactory;

        private final int graphicXOffset;
        private final int graphicYOffset;

        static {
            turnstileBlockFactory = new Turnstile.TurnstileBlock.TurnstileBlockFactory();
        }

        private TurnstileBlock(
                Patch patch,
                boolean attractor,
                boolean hasGraphic,
                int graphicXOffset,
                int graphicYOffset
        ) {
            super(patch, attractor, hasGraphic);

            this.graphicXOffset = graphicXOffset;
            this.graphicYOffset = graphicYOffset;
        }

        public int getGraphicXOffset() {
            return graphicXOffset;
        }

        public int getGraphicYOffset() {
            return graphicYOffset;
        }

        // Turnstile block factory
        public static class TurnstileBlockFactory extends Amenity.AmenityBlock.AmenityBlockFactory {
            @Override
            public Turnstile.TurnstileBlock create(
                    Patch patch,
                    boolean attractor,
                    boolean hasGraphic,
                    AmenityFootprint.Rotation.Orientation... orientation
            ) {
                int graphicXOffset = 0;
                int graphicYOffset = 0;

                AmenityFootprint.Rotation.Orientation turnstileOrientation = orientation[0];

                // Make the necessary adjustments
                if (turnstileOrientation == AmenityFootprint.Rotation.Orientation.DOWN) {
                    graphicYOffset = -1;
                } else if (turnstileOrientation == AmenityFootprint.Rotation.Orientation.RIGHT) {
                    graphicXOffset = -1;
                }

                return new Turnstile.TurnstileBlock(
                        patch,
                        attractor,
                        hasGraphic,
                        graphicXOffset,
                        graphicYOffset
                );
            }
        }
    }

    // Turnstile factory
    public static class TurnstileFactory extends GoalFactory {
        public Turnstile create(
                List<AmenityBlock> amenityBlocks,
                boolean enabled,
                int waitingTime,
                boolean blockEntry,
                TurnstileMode turnstileMode
        ) {
            return new Turnstile(
                    amenityBlocks,
                    enabled,
                    waitingTime,
                    blockEntry,
                    turnstileMode
            );
        }
    }

    // Lists the possible modes of this turnstile
    public enum TurnstileMode {
        BOARDING("Boarding"),
        ALIGHTING("Alighting"),
        BIDIRECTIONAL("Bidirectional");

        private final String name;

        private TurnstileMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
