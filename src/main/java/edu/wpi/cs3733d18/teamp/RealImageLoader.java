package edu.wpi.cs3733d18.teamp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RealImageLoader implements ImageLoader {

    private String filePath;

    public RealImageLoader(String fileName){
        this.filePath = fileName;
        loadFromDisk(fileName);
    }

    @Override
    public void display() {
        // TODO
    }

    private void loadFromDisk(String filePath){
        // TODO
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
