package edu.wpi.cs3733d18.teamp;

public class ProxyImageLoader implements ImageLoader {

    private ImageLoader realImageLoader;
    private String filePath;

    /**
     * ProxyImageLoader defers the loading of an image until it is
     * necessary to prevent unnecessary overheads.
     * @param filePath url of image to load
     */
    public ProxyImageLoader(String filePath){
        this.filePath = filePath;
    }

    /**
     * ProxyImageLoader instantiates the RealImageLoader when the
     * image needs to be displayed. The user never interacts with the
     * ProxyImageLoader directly.
     */
    @Override
    public void display() {
        // Load RealImageLoader if it does not exist
        if (realImageLoader == null) {
            realImageLoader = new RealImageLoader(filePath);
        }
        // Forward to concrete ImageLoader
        realImageLoader.display();
    }
}
