package edu.wpi.cs3733d18.teamp;

import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;

public class Settings {
    private static Settings _settings;
    private static PathfindingContext _pathfindingContext = new PathfindingContext();
    private static PathfindingContext.PathfindingSetting _pathfindingSettings;
    private static double _feetPerPixel;

    private static int _timeDelay = 300000;
    PhongMaterial floorL2Texture;
    PhongMaterial floorL1Texture;
    PhongMaterial floorGTexture;
    PhongMaterial floor1stTexture;
    PhongMaterial floor2ndTexture;
    PhongMaterial floor3rdTexture;

    private Settings() {
        _pathfindingSettings = PathfindingContext.PathfindingSetting.AStar;
        _feetPerPixel = 85./260.; // Initial value
        _pathfindingContext.setPathfindingContext(_pathfindingSettings);

        // Textures
        floorL2Texture = new PhongMaterial();
        floorL2Texture.setDiffuseMap(new Image(getClass().getResource("/models/textures/L2Floor_UV.png").toExternalForm()));
        floorL1Texture = new PhongMaterial();
        floorL1Texture.setDiffuseMap(new Image(getClass().getResource("/models/textures/L1Floor_UV.png").toExternalForm()));
        floorGTexture = new PhongMaterial();
        floorGTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/groundFloor_UV.png").toExternalForm()));
        floor1stTexture = new PhongMaterial();
        floor1stTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/1stFloor_UV.png").toExternalForm()));
        floor2ndTexture = new PhongMaterial();
        floor2ndTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/2ndFloor_UV.png").toExternalForm()));
        floor3rdTexture = new PhongMaterial();
        floor3rdTexture.setDiffuseMap(new Image(getClass().getResource("/models/textures/3rdFloor_UV.png").toExternalForm()));
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

    public static void setTimeDelay(int _timeDelay) {
        Settings._timeDelay = _timeDelay;
    }

    public static PathfindingContext.PathfindingSetting getPathfindingSettings() {
        return _pathfindingSettings;
    }

    public static int getTimeDelay() {
        return _timeDelay;
    }

    public PhongMaterial getFloorL2Texture() {
        return floorL2Texture;
    }

    public PhongMaterial getFloorL1Texture() {
        return floorL1Texture;
    }

    public PhongMaterial getFloorGTexture() {
        return floorGTexture;
    }

    public PhongMaterial getFloor1stTexture() {
        return floor1stTexture;
    }

    public PhongMaterial getFloor2ndTexture() {
        return floor2ndTexture;
    }

    public PhongMaterial getFloor3rdTexture() {
        return floor3rdTexture;
    }
}
