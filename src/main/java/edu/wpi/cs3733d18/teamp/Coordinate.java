package edu.wpi.cs3733d18.teamp;

import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

public class Coordinate {
    private double X_SCALE_FACTOR = 0.8229;
    private double Y_SCALE_FACTOR = 0.600;

    private double X_OFFSET_FACTOR = 1010;
    private double Y_OFFSET_FACTOR = 460;

    private double X_ROT = Math.toRadians(-12);
    private double Y_ROT = Math.toRadians(5);
    private double Z_ROT = Math.toRadians(23.6);
    private double Z_ROT2 = Math.toRadians(2.9);

    private int x;
    private int y;
    private String floor;

    private int x3D;
    private int y3D;

    public Coordinate(int x, int y, String floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
        map2DCoords();
    }

    /**
     * Maps the 2D coordinates of the given node to 3D coordinates using globals
     */
    public void map2DCoords() {
//        double tempX = this.x*(Math.cos(Y_ROT)*Math.cos(Z_ROT) + Math.sin(X_ROT)*Math.sin(Y_ROT)*Math.sin(Z_ROT));
//        tempX -= Math.cos(Z_ROT)*Math.sin(Y_ROT) + Math.cos(Y_ROT)*Math.sin(X_ROT)*Math.sin(Z_ROT);
//        tempX -= this.y*Math.cos(X_ROT)*Math.sin(Z_ROT);
//
//        double tempY = this.x*(Math.cos(Y_ROT)*Math.sin(Z_ROT) - Math.cos(Z_ROT)*Math.sin(X_ROT)*Math.sin(Y_ROT));
//        tempY -= Math.sin(Y_ROT)*Math.sin(Z_ROT) - Math.cos(Y_ROT)*Math.cos(Z_ROT)*Math.sin(X_ROT);
//        tempY += this.y*Math.cos(X_ROT)*Math.cos(Z_ROT);
//
//        tempX = tempX*Math.cos(Z_ROT2) - tempY*Math.sin(Z_ROT2);
//        tempY = tempX*Math.sin(Z_ROT2) + tempY*Math.cos(Z_ROT2);
//
//        this.x3D = (int) Math.round((tempX + X_OFFSET_FACTOR) * X_SCALE_FACTOR);
//        this.y3D = (int) Math.round((tempY + Y_OFFSET_FACTOR) * Y_SCALE_FACTOR);
        double xMult;
        double yMult;
        double intercept;
        double tempX;
        double tempY;
        switch (this.floor) {
            case "1":
                xMult = 0.7165;
                yMult = -0.3491;
                intercept =  822.4610;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2701;
                yMult = 0.5337;
                intercept = 318.5789;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;

            case "2":
                xMult = 0.7321;
                yMult = -0.3431;
                intercept =  794.3872;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2690;
                yMult = 0.5394;
                intercept = 293.1805;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;

            case "3":
                xMult = 0.7282;
                yMult = -0.3445;
                intercept =  814.3572;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2640;
                yMult = 0.5265;
                intercept = 293.0829;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;

            case "G":
                xMult = 0.7321;
                yMult = -0.3431;
                intercept =  794.3872;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2690;
                yMult = 0.5394;
                intercept = 293.1805;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;

            case "L1":
                xMult = 0.7254;
                yMult = -0.3599;
                intercept =  836.3449;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2675;
                yMult = 0.5368;
                intercept = 355.1487;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;

            case "L2":
                xMult = 0.7657;
                yMult = -0.3583;
                intercept =  746.1721;
                tempX = (this.x*xMult + this.y*yMult + intercept);
                this.x3D = (int)Math.round(tempX);

                xMult = 0.2378;
                yMult = 0.5101;
                intercept = 467.6895;
                tempY = (this.x*xMult + this.y*yMult + intercept);
                this.y3D = (int)Math.round(tempY);
                break;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getX3D() {
        return x3D;
    }

    public int getY3D() {
        return y3D;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX3D(int x3D) {
        this.x3D = x3D;
    }

    public void setY3D(int y3D) {
        this.y3D = y3D;
    }
}
