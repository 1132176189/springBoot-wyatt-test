package com.yt.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

public class Test {

    public static void main(String[] args) throws Exception {

      /*  BufferedImage img = ImageIO.read(new File("D:/EncryptImg.png"));
        int alert = img.getRGB(241,306);
        System.out.println(alert);
        System.out.println(new Color(-3026479).getBlue());*/
        set();
    }
    public static  void set()throws Exception{
        File file = new File("E:/test3.png");
        BufferedImage img = ImageIO.read(file);
        BufferedImage img2 =new BufferedImage(512,512,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = img.getRaster();
        for (int h=0;h<512;h++) {
            for (int w = 0; w < 512; w++) {
            wr.setSample(w,h,0,223);
            }
        }
        ImageIO.write(img, "PNG",new File("E:/test2.png"));
    }
}
