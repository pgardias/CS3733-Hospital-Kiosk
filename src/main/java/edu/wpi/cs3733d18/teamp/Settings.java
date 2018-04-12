package edu.wpi.cs3733d18.teamp;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;

public class Settings {
    private static Settings _settings;
    private static PathfindingContext.PathfindingSetting _pathfindingSettings;

    private Settings() {
        _pathfindingSettings = PathfindingContext.PathfindingSetting.AStar;
    }

    public static Settings getSettings() {
        if (_settings == null) {
            _settings = new Settings();
        }
        return _settings;
    }

    public static void setSettings(PathfindingContext.PathfindingSetting pathfindingSettings){
        _pathfindingSettings = pathfindingSettings;
    }

    public static PathfindingContext.PathfindingSetting getPathfindingSettings() {
        return _pathfindingSettings;
    }
}
