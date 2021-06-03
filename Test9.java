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
import java.util.Map;

/*
      Descrption: 基于数据拆分和组合算法的增强型 GEMD 图像解析算法代码实现
      Version：1.0.0
      Created By : 赵鑫
 */
public class Test9 {
    public static List<Integer> getOriImagePx2(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        List<Integer> dataFeed = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(y,x);
                Color color =new Color(rgb);
                dataFeed.add(color.getBlue());
            }
        }
        return dataFeed;
    }
    public static void main(String[] args) throws Exception {
        //解析第一步：也是从隐写后的照片中获取隐写后的像素值
        String OriginPath = "D:/EncryptImg.png";
        List<Integer> OriList = getOriImagePx2(new File(OriginPath));
        int pxBatch = PhotoUtils.caculateJiaMi(); //209712
        List<Integer> list = OriList.subList(0, pxBatch);
        for(int a=0;a<list.size();a++){
            System.out.println(list.get(a));
        }
        //循环处理原始图像，两个一组
        StringBuilder jiaMiList = new StringBuilder("");
        //获取加密信息：
        List<Map<String, Integer>> dPxList = PhotoUtils.getDPx();
        int maxSize = list.size();
        for (int a = 4; a <= maxSize; ) {
            if (a > maxSize) {
                break;
            }
            List<Integer> pData = list.subList(a - 4, a);
            // 得到LSB（returnLsb）的值
            String strJiaMi1= PhotoUtils.lsbAction(pData);
            //先LSB反向解析
            Map<String, Integer> map1 = dPxList.get((a / 4) - 1);
            int d1 = map1.get("name");
            int d2 = map1.get("size");
            List<Integer> returnLsb = PhotoUtils.decrForLsb(pData, d1, d2);//初步还原图像得到四位
            StringBuilder stringBuilder = new StringBuilder("");
            String strJiaMi2 = PhotoUtils.getDescryptList(stringBuilder,returnLsb);//6位加密信息
            jiaMiList.append(strJiaMi1).append(strJiaMi2);
            a = a + 4;
        }
        System.out.println(jiaMiList.length());
        printJimPhoto(jiaMiList.toString());
    }


    public static int LsbAnaylize(Integer val) {
        if (val % 2 == 0) {
            return 0;
        }
        return 1;
    }

    public static int LsbNextAnaylize(int gFirst, int gSec) {
        int fVal = (gFirst) / 2;
        return LsbAnaylize(fVal + gSec);
    }

    //反向调用求出x和y
    public static int reverseXY(List<Integer> list) {
        int fData = 0;
        int n = list.size();
        for (int i = 1; i <= list.size(); i++) {
            int pInt = list.get(i - 1);
            fData = fData + pInt * (2 * i - 1);
        }

        int modData = (int) Math.pow(2, n + 1);
        return Math.floorMod(fData, modData);
    }

    public static String decimalToBinary(int decimal) {
        return Integer.toBinaryString(decimal);
    }

    public static int fFunction(List<Integer> p) {
        int fData = 0;
        int n = p.size();
        for (int i = 1; i <= p.size(); i++) {
            int pInt = p.get(i - 1);
            fData = fData + pInt * (2 * i - 1);
        }
        int modData = (int) Math.pow(2, n) - 1;
        return Math.floorMod(fData, (int) Math.pow(2, modData));
    }

    public static void printJimPhoto(String jiaMiStr) throws IOException {
        int []dataJiaMi = new int[jiaMiStr.length()/8];

        for (int a = 8; a < jiaMiStr.length(); ) {
            int rgb =  Integer.parseInt(jiaMiStr.substring(a - 8, a), 2);
            Color color =new Color(rgb,rgb,rgb);
            dataJiaMi[(a-8)/8]=(color.getRGB());
            a = a + 8;
        }

       int bat = (dataJiaMi.length)/256;
        int height =(int)Math.ceil(bat)+1;
        int width =(dataJiaMi.length)%256;
        BufferedImage img = ImageIO.read(new File("D:/jiami.png"));

       for (int i = 0;i<256; i++) {
            for (int j = 0; j < 256; j++) {
                if(i==height-1){
                    if(j==width-1){
                        int gray =dataJiaMi[256*i+j];
//                        Color color = new Color(gray,gray,gray);
                        img.setRGB(j,i,gray);
                        break;
                    }
                }
                int gray =dataJiaMi[256*i+j];
                img.setRGB(j,i,gray);
            }
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/DecryPNG.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
//        ImageIO.write(img, "PNG", new File("D:/DecryPNG.png"));
        img.flush();
        ios.flush();
    }
}
