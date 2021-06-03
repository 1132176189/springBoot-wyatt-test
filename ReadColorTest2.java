package com.yt.test;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadColorTest2 {
    public static void main(String[] args) throws IOException {
        File file = new File("D:/car.png");
        changeImgtoGray(file);
    }
    public static void changeImgtoGray(File file) throws IOException {
        int[] rgb = new int[3];
        BufferedImage img = ImageIO.read(file);
        int y = img.getHeight();
        int x = img.getWidth();
        List<Integer> dataFeed = new ArrayList<>();
        for (int i = y-1; i >=0; i--) {
            for (int j = 0; j < x; j++) {
                int pixel = img.getRGB(j, i);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                int gray = new Double(rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114).intValue();
                dataFeed.add(gray);
                Color color = new Color(rgb[0], rgb[1], rgb[2]);
                img.setRGB(j, i, color.getRGB());
            }
        }
        System.out.println(dataFeed.size());
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/yinxie.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();
    }
}