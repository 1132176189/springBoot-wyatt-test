package com.yt.test;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test6 {
    public static void main(String[] args) throws IOException {
        String path = "D:/Origin.png";
        changeImgtoGray(new File(path));
    }
    public static void changeImgtoGray(File file) throws IOException {
        int[] rgb = new int[3];
        BufferedImage img = ImageIO.read(file);
        int y = img.getHeight();
        int x = img.getWidth();
        BufferedImage grayImage = new BufferedImage(x,y,BufferedImage.TYPE_BYTE_GRAY);
        List<Integer> dataFeed = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int pixel = img.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
//                int gray = new Double(rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114).intValue();
//                dataFeed.add(gray);
//                Color color = new Color(    gray, gray, gray);
                grayImage.setRGB(i, j, img.getRGB(i,j));
            }
        }
//        printPhoto(dataFeed,img);
        ImageIO.write(grayImage, "png", new File("D:/jiami.png"));
    }
    public static void printPhoto(List<Integer> printList,BufferedImage img)throws IOException {
        int width = img.getWidth();
        int height =img.getHeight();
        for (int j = height-1; j >=0; j--) {
            for (int i = 0; i <= width-1; i++) {
                if((i==width-1)&&j==width-1){
                    System.out.println("最后一个像素");
                    break;
                }
                int gray =printList.get(width*(height-j-1)+i);
                Color color = new Color(gray,gray,gray);
                img.setRGB(j,i,color.getRGB());
            }
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/xiaobiao.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();
    }
}