package com.yt.test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test5 {
    public static void main(String[] args) throws IOException {
//        printJimPhoto("D:/9.png");
        List<Integer>list =get1Px("D:/8.png");
        printJimPhoto("D:/0513. png",list);
    }
    public static void printJimPhoto(String path,List<Integer>list) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int width = img.getWidth();
        int height =img.getHeight();
        for (int x = 0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                    int rgb = img.getRGB(x,y);
                    Color color = new Color(rgb);
                   /* int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int c =r+g+b;*/
                int gray =list.get((x*width)+y);
                   int c = gray/2;
                    Color newColor = new Color(c,c,c);
                    img.setRGB(x,y,newColor.getRGB());
            }
        }
        ImageIO.write(img,"png",new File("D:/8.png"));
    }
    public static List<Integer> get1Px(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        List<Integer> list = new ArrayList<>();
        int width = img.getWidth();
        int height =img.getHeight();
        for (int x = 0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                int rgb = img.getRGB(x,y);
                Color color = new Color(rgb);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int gray = new Double(r * 0.299 + g * 0.587 + b * 0.114).intValue();
//                list.add("x"+x+"y"+y+" "+gray);
                list.add(gray);
            }

        }
        System.out.println(list);
        return list;
    }
}