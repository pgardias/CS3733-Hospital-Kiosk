package edu.wpi.cs3733d18.teamp.ui.home;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Animate a shake effect on the given node
 * <p>
 * Port of Shake from Animate.css http://daneden.me/animate by Dan Eden
 * <p>
 * {@literal @}keyframes shake {
 * 0%, 100% {transform: translateX(0);}
 * 10%, 30%, 50%, 70%, 90% {transform: translateX(-10px);}
 * 20%, 40%, 60%, 80% {transform: translateX(10px);}
 * }
 *
 * @author Jasper Potts
 */
public class ShakeTransition extends CachedTimelineTransition {

    private static final int ANIM_DIST = 5;
    /**
     * Create new ShakeTransition
     *
     * @param node1 The node to affect
     */
    public ShakeTransition(final Node node1, final Node node2) {
        super(
                node1,
                TimelineBuilder.create()
                        .keyFrames(
                                new KeyFrame(Duration.millis(0), new KeyValue(node1.translateXProperty(), 0, WEB_EASE)),
                                new KeyFrame(Duration.millis(100), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(200), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(300), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(400), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(500), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(600), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(700), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(800), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(900), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(1000), new KeyValue(node1.translateXProperty(), 0, WEB_EASE)),
                                new KeyFrame(Duration.millis(0), new KeyValue(node2.translateXProperty(), 0, WEB_EASE)),
                                new KeyFrame(Duration.millis(100), new KeyValue(node2.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(200), new KeyValue(node2.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(300), new KeyValue(node2.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(400), new KeyValue(node2.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(500), new KeyValue(node2.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(600), new KeyValue(node2.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(700), new KeyValue(node2.translateXProperty(), -ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(800), new KeyValue(node2.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(900), new KeyValue(node2.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(1000), new KeyValue(node2.translateXProperty(), 0, WEB_EASE))
                        )
                        .build()
        );
        setCycleDuration(Duration.seconds(0.75));
        setDelay(Duration.seconds(0.0));
    }

    public ShakeTransition(final Node node1) {
        super(
                node1,
                TimelineBuilder.create()
                        .keyFrames(
                                new KeyFrame(Duration.millis(0), new KeyValue(node1.translateXProperty(), 0, WEB_EASE)),
                                new KeyFrame(Duration.millis(100), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(200), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(300), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(400), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(500), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(600), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(700), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(800), new KeyValue(node1.translateXProperty(), ANIM_DIST, WEB_EASE)),
//                                new KeyFrame(Duration.millis(900), new KeyValue(node1.translateXProperty(), -ANIM_DIST, WEB_EASE)),
                                new KeyFrame(Duration.millis(1000), new KeyValue(node1.translateXProperty(), 0, WEB_EASE))
                                )
                        .build()
        );
        setCycleDuration(Duration.seconds(0.75));
        setDelay(Duration.seconds(0.0));
    }
}