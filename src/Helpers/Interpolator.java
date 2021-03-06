/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import Helpers.FloatingCoordinate;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Robik Singh Shrestha
 *
 *
 * @description
 *
 * This class takes in a map from target (warped image) to source image i.e. it
 * needs to know the coordinates of the pixels inside the original image which
 * correspond to the coordinates in the interpolated image.
 *
 * It finds out the value of color for each pixel of the interpolated image by:
 *
 * a. Mapping the pixel coordinate in the interpolated image (only the
 * coordinate of the pixel is known, its color value is unknown initially!) to
 * floating point coordinate in the original image.
 *
 * b. There is no floating point coordinate in the original image because the
 * coordinates are integers. So, it uses neighbors of the floating point
 * coordinate to approximate the value of the color. For this, it assigns
 * weights to each neighbor. Nearer the neighbor to the floating point, greater
 * is its weight.
 *
 * c. Each color i.e. R, G and B is assigned the weight and then the actual
 * color is found out by adding the color components of the neighbors.
 */
public class Interpolator {

    protected BufferedImage originalImage, interpolatedImage;
    protected FloatingCoordinate targetToSourceMap[][];//

    public BufferedImage runGet() {
        this.interpolate();
        return this.getInterpolatedImage();
    }

    public BufferedImage setRunGet(BufferedImage originalImage, FloatingCoordinate targetToSourceMap[][], int interpolatedImageWidth, int interpolatedImageHeight) {
        this.setParameters(originalImage, targetToSourceMap, interpolatedImageWidth, interpolatedImageHeight);
        return this.runGet();
    }

    public Interpolator(BufferedImage originalImage, FloatingCoordinate targetToSourceMap[][], int interpolatedImageWidth, int interpolatedImageHeight) {
        this.setParameters(originalImage, targetToSourceMap, interpolatedImageWidth, interpolatedImageHeight);
    }

    public void setParameters(BufferedImage originalImage, FloatingCoordinate targetToSourceMap[][], int interpolatedImageWidth, int interpolatedImageHeight) {
        this.originalImage = originalImage;
        this.targetToSourceMap = targetToSourceMap;
        interpolatedImage = new BufferedImage(interpolatedImageWidth, interpolatedImageHeight, BufferedImage.TYPE_INT_ARGB);
    }
    //This is the main function

    public void interpolate() {
        //For each pixel
        //interpolatedImage=originalImage;
        for (int x = 0; x < interpolatedImage.getWidth(); x++) {
            for (int y = 0; y < interpolatedImage.getHeight(); y++) {
          Color c = findColorFromBilinearInterpolation(targetToSourceMap[x][y].x, targetToSourceMap[x][y].y);
          interpolatedImage.setRGB(x, y, c.getRGB());
          //interpolatedImage=originalImage;
            }
        }
    }

    //It uses 4 connectivity and distance based weight (greater the distance, lesser the weight) to find out the
    //color value of a mapped pixel.
    protected Color findColorFromBilinearInterpolation(float x, float y) {

        int floorX = (int) Math.floor(x);
        int floorY = (int) Math.floor(y);

        float tx = x - floorX;
        float ty = y - floorY;

        float c00 = (1 - tx) * (1 - ty);
        float c01 = tx * (1 - ty);
        float c10 = (1 - tx) * ty;
        float c11 = tx * ty;

        try {
            int rgb00 = originalImage.getRGB(floorX, floorY);
            Color col00 = new Color(rgb00,true);
            int rgb01 = originalImage.getRGB(floorX + 1, floorY);
            Color col01 = new Color(rgb01,true);
            int rgb10 = originalImage.getRGB(floorX, floorY + 1);
            Color col10 = new Color(rgb10,true);
            int rgb11 = originalImage.getRGB(floorX + 1, floorY + 1);
            Color col11 = new Color(rgb11,true);

            int r = (int) (col00.getRed() * c00 + col01.getRed() * c01 + col10.getRed() * c10 + col11.getRed() * c11);
            int g = (int) (col00.getGreen() * c00 + col01.getGreen() * c01 + col10.getGreen() * c10 + col11.getGreen() * c11);
            int b = (int) (col00.getBlue() * c00 + col01.getBlue() * c01 + col10.getBlue() * c10 + col11.getBlue() * c11);
            int a = (int) (col00.getAlpha() * c00 + col01.getAlpha() * c01 + col10.getAlpha() * c10 + col11.getAlpha() * c11);
            return new Color(r, g, b, a);
        } catch (Exception e) {
            return new Color(0, 0, 0,0);
        }
    }

    //Getters and Setters
    public BufferedImage getInterpolatedImage() {
        return this.interpolatedImage;
    }
}
