package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.ArrayList;

public class PathfindingContext {
    private Pathfinder pathfinder;

    public enum PathfindingSetting {
        AStar, BreadthFirst, DepthFirst
    }

    public PathfindingContext() { }

    public PathfindingContext(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }
    public void setPathfindingContext(PathfindingSetting pathfindingSetting) {
        Pathfinder pathfinder;
        switch (pathfindingSetting) {
            case AStar:
                pathfinder = new AStar();
                break;

            case BreadthFirst:
                pathfinder = new BreadthFirst();
                break;

            case DepthFirst:
                pathfinder = new DepthFirst();
                break;

            default:
                pathfinder = new AStar();
                break;
        }
        this.pathfinder = pathfinder;
    }

    public ArrayList<Node> findPath(Node srcNode, Node destNode) {
        return this.pathfinder.findPath(srcNode, destNode);
    }
}
