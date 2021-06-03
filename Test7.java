package com.yt.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test7 {
    public static void main(String[] args) throws IOException {
        List<Integer> list = get1Px("D:/EncryptImg.png");
        List<String> list2 = new ArrayList<>();
        BufferedImage img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
//                list2.add(list.get(x * 10 + y));
//                int rgb = img.getRGB(x, y);
                int gray =list.get((x*10)+y);
                Color color = new Color(gray,gray,gray);
                img.setRGB(x,y,color.getRGB());
            }
        }
        ImageIO.write(img,"png",new File("D:/0513.png"));
//        System.out.println(list2);
    }

    public static List<Integer> get1Px(String path) throws IOException {
        BufferedImage img = ImageIO.read(new File(path));
        int []a = new int[512*512];
        Graphics grf =img.getGraphics();
        img.getRGB(0,0,512,512,a,0,512);

      /*  int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
//                int gray = new Double(r * 0.299 + g * 0.587 + b * 0.114).intValue();
                int gray = (r+g+b)/3;
//                list.add("x" + x + "y" + y + "," + gray);
                list.add(gray);
            }

        }
        System.out.println(list);*/
        return null;
    }
}
