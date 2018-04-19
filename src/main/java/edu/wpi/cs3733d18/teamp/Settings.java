package edu.wpi.cs3733d18.teamp;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;

public class Settings {
    private static Settings _settings;
    private static PathfindingContext _pathfindingContext = new PathfindingContext();
    private static PathfindingContext.PathfindingSetting _pathfindingSettings;
    private static double _feetPerPixel;

    private Settings() {
        _pathfindingSettings = PathfindingContext.PathfindingSetting.AStar;
        _feetPerPixel = 85./260.; // Initial value
        _pathfindingContext.setPathfindingContext(_pathfindingSettings);
    }

    public static Settings getSettings() {
        if (_settings == null) {
            _settings = new Settings();
        }
        return _settings;
    }

    public static double getFeetPerPixel() {
        return _feetPerPixel;
    }

    public static PathfindingContext getPathfindingContext() {
        return _pathfindingContext;
    }

    public static void setPathfindingContext(PathfindingContext pathfindingContext) {
        _pathfindingContext = pathfindingContext;
    }

    public static void setPathfindingAlgorithm(PathfindingContext.PathfindingSetting pathfindingSettings){
        _pathfindingContext.setPathfindingContext(pathfindingSettings);
        _pathfindingSettings = pathfindingSettings;
    }

    public static void setFeetPerPixel(double _feetPerPixel) {
        Settings._feetPerPixel = _feetPerPixel;
    }

    public static PathfindingContext.PathfindingSetting getPathfindingSettings() {
        return _pathfindingSettings;
    }
}
