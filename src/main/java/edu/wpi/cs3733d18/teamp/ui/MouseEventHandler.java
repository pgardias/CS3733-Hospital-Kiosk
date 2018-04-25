package edu.wpi.cs3733d18.teamp.ui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public class MouseEventHandler {

    public Thread timer = new Thread(() -> {
        try {

            System.out.println("Does it work?");

            Thread.sleep(1000);

            System.out.println("Nope, it doesn't...... again");
        } catch (InterruptedException v) {
            System.out.println(v);
        }
    });

    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        Boolean isDragging;

        @Override
        public void handle(MouseEvent event) {
            Originator localOriginator = new Originator();
            long start, now;
            System.out.println("Test3");
            if (event.getEventType() != null){
                System.out.println("Moving");
                localOriginator.setState("Active");
                localOriginator.saveStateToMemento();
            }

            if (event.getEventType() == null) {
                System.out.println("Not moving");
                start = System.currentTimeMillis(); // time right now
                now = System.currentTimeMillis(); // time right after now
                localOriginator.setState("Inactive");

                while (now - start < 30000 && event.getEventType() == null) {
                    now = System.currentTimeMillis();
                    if (event.getEventType() != null) {
                        start = 0;
                        now = 0;
                        localOriginator.setState("Active");
                        break;
                    }
                }

                if (now - start >= 1000 && event.getEventType() == null) {
                    System.out.println("Now we go to the screensaver");
                }
            }
        }
    };


    public EventHandler<MouseEvent> getMouseEventEventHandler() {
        return mouseEventEventHandler;
    }
}
