package edu.wpi.cs3733d18.teamp.Pathfinding;

import java.util.ArrayList;

public class PathfindingContext {
    private Pathfinder pathfinder;

    public enum PathfindingSetting {
        AStar, BreadthFirst, DepthFirst, Dijkstra, BestFirst
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

            case Dijkstra:
                pathfinder = new Dijkstra();
                break;

            /*case BestFirst:
                pathfinder = new BestFirst();
                break;*/

            default:
                pathfinder = new AStar();
                break;
        }
        this.pathfinder = pathfinder;
    }

    public ArrayList<Node> findPath(Node srcNode, Node destNode) {
        return pathfinder.runPathfinder(srcNode, destNode);
    }
}
