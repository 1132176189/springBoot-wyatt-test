package com.yt.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

public class Test11 {
    public static void main(String[] args) throws Exception {

      /*  BufferedImage img = ImageIO.read(new File("D:/EncryptImg.png"));
        int alert = img.getRGB(241,306);
        System.out.println(alert);
        System.out.println(new Color(-3026479).getBlue());*/
        get();
    }
    public static  void get()throws Exception{
        File file = new File("E:/test2.png");
        BufferedImage img = ImageIO.read(file);
        WritableRaster wr = img.getRaster();
        for (int h=0;h<512;h++) {
            for (int w = 0; w < 512; w++) {
                System.out.println(wr.getSample(w,h,0));
            }
        }
    }
}
