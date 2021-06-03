package com.yt.test;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/*
      Descrption: 基于数据拆分和组合算法的增强型 GEMD 图像隐写算法代码实现
      Version：1.0.0
      Created By : 赵鑫
 */
public class Test10 {

    public static void main(String[] args) throws IOException {
        String jiaMiPath = "D:/Jiami.png";
        String OriginPath = "D:/Origin.png";
        //获取原始图片的像素值
        List<Integer> OriList=PhotoUtils.getOriImagePx2(new File(OriginPath));
        //把加密图片转换成二进制字符串
        String jiaMiStr=PhotoUtils.getJiaMiImagePx(new File(jiaMiPath));
        int strBatch = jiaMiStr.length()/10;
        int pxBatch = strBatch *4;
        List<Integer> list = OriList.subList(0,pxBatch);
        for(int a=0;a<list.size();a++){
            System.out.println(list.get(a));
        }
        //循环处理原始图像，两个一组
        //循环分好的组，每四个像素对应10个加密信息
        //将存储到文件
        List<Integer>  listLsb = new ArrayList<>();
        List<Map<String,Integer>> dList = new ArrayList<>();
        for (int a =4;a<=list.size();){
            List<Integer> terminateList = new ArrayList<>();
            List<Integer> pData = list.subList(a-4,a);
            StringBuilder jiaMiList =new StringBuilder("");
            if(a==4){
                jiaMiList = jiaMiList.append(jiaMiStr.substring(a-4,10));
            }else{
                int first =10*((a/4)-1);
                int last =10*((a/4));
                jiaMiList = jiaMiList.append(jiaMiStr.substring(first,last));
            }
            String jiaMiData =jiaMiList.toString();
//            先拆分秘密信息，秘密信息分解成两组，一组是n 1001
            String jiaMi1 = jiaMiData.substring(0, pData.size());
            //一组是n+2  110011
            String jiaMi2 = jiaMiData.substring(pData.size(), jiaMiData.length());
           /* 利用GEMD嵌入*/
            List<Integer> p1,p2;
            //第一步：将原始信息分为两组（判断基数和偶数）.第一组是p1.第二组是p2
            if ((pData.size() % 2 == 0)) {
                p1 = pData.subList(0, pData.size() / 2);
                p2 = pData.subList(pData.size() / 2, pData.size());
            } else {
                p1 = pData.subList(0, pData.size() / 2);
                p2 = pData.subList(pData.size() / 2, pData.size() - 1);
            }
            //第二步，拆分后每一个p里面都有listLsb/2个像素
            int nPx = pData.size() / 2;
            //这里要对jiaMiDecimal2进行一个分解
//            int jiaMiDecimal1 = Integer.parseInt(jiaMi1, 2);
//            System.out.println("jiaMiDecimal1:" + jiaMiDecimal1);
            //jiaMi2 :110011,二进制转换成十进制
            int jiaMiDecimal2 = Integer.parseInt(jiaMi2, 2);
//            System.out.println("jiaMiDecimal2:" + jiaMiDecimal2);
            //mod 方法
            int xValue = (int) (jiaMiDecimal2 / Math.pow(2, nPx + 1));
            int yValue = (int) (jiaMiDecimal2 % Math.pow(2, nPx + 1));
            //调用f（g1,g2）方法
            int f1 = fFunction(p1);
            int f2 = fFunction(p2);
            //第五步：根据（3-14）计算d1 和d2
            int modData = (int) Math.pow(2, 2 * 2 - 1);//等于8
            int d1 = Math.floorMod(yValue - f1, modData);
            int d2 = Math.floorMod(xValue - f2, modData);
            Map<String,Integer>dMap = new HashMap<>();
            //第六步 根据GEMD因写算法,
            int x1 = returnX(d1, nPx);
            int x2 = returnX(d2, nPx);
            //第七部：得到隐写后的信息（1，0）减1，（0，0）或者（1，1）相等，（0，1）就加1
            //pEncry1 是p1隐写后的数据
            List<Integer> pEncry1 = PhotoUtils.getGemdEncrypt(nPx, x1, d1, p1);
            //pEncry2 是p2隐写后的数据
            List<Integer> pEncry2 = PhotoUtils.getGemdEncrypt(nPx, x2, d2, p2);
            terminateList.addAll(pEncry1);
            terminateList.addAll(pEncry2);
              /*
            此时得到LSB加密后的像素值
            //pData 例如：[205,198,156,178]    jiaMi1:  1001
         */
            Map<String,Object>lsbMap = PhotoUtils.getLsbFunction(terminateList, jiaMi1);
            listLsb.addAll((List<Integer>)lsbMap.get("lsbList"));
            dMap.put("value1",Integer.valueOf(String.valueOf(lsbMap.get("value2"))));
            dMap.put("value2",Integer.valueOf(String.valueOf(lsbMap.get("value4"))));
            dList.add(dMap);
            a=a+4;
        }
        PhotoUtils.writePhoto(dList);
        for(int a=0;a<listLsb.size();a++){
            System.out.println(listLsb.get(a));
        }
        printJimPhoto(listLsb);

        System.out.println(dList.size());
        System.out.println( listLsb.size());
    }

    //将隐写后的像素值输出为图片
    public static void printJimPhoto(List<Integer> printList)throws IOException {
        BufferedImage img = ImageIO.read(new File("D:/Origin.png"));
        //先计算最多跑多宽度：
        int bat = (printList.size())/512;
        int height =(int)Math.ceil(bat);
        int width =(printList.size())%512;

        for (int i = 0;i<512; i++) {
            for (int j = 0; j < 512; j++) {
                if(i==height-1){
                    if(j==width-1){
                        int gray =printList.get(512*i+j);
                        Color color = new Color(gray,gray,gray);
                        img.setRGB(i,j,color.getRGB());
                        break;
                    }
                }
                int gray =printList.get(height*i+j);
                Color color = new Color(gray,gray,gray);
                img.setRGB(i,j,color.getRGB());
            }
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/EncryptImg.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();

       /* BufferedImage img = ImageIO.read(new File("D:/Origin.png"));
        Integer[] integers2 = printList.toArray(new Integer[printList.size()]);
        int [] data = new int[integers2.length];
        for (int i = 0;i<integers2.length; i++) {
            Color color =new Color(integers2[i],integers2[i],integers2[i]);
            data[i]=color.getRGB();
        }
*//*        int bat = (printList.size())/512;
        int height =(int)Math.ceil(bat);
        int width =(printList.size())%512;*//*
        DecimalFormat df2  = new DecimalFormat("###.000000");
        String a = df2.format(printList.size()/512);
        int height =(int)Math.ceil(Double.valueOf(a))+1;
        int width =(printList.size())%512;
        img.setRGB(0,0,width,height,data,0,512);
        //先计算最多跑多宽度：
        *//*int bat = (printList.size())/512;
        int height =(int)Math.ceil(bat);
        int width =(printList.size())%512;

        for (int i = 0;i<height; i++) {
            for (int j = 0; j < 512; j++) {
                if(i==height-1){
                    if(j==width-1){
                        int gray =printList.get(512*i+j);
                        Color color = new Color(gray,gray,gray);
                        img.setRGB(j,i,color.getRGB());
                        break;
                    }
                }
                int gray =printList.get(width*i+j);
                Color color = new Color(gray,gray,gray);
                img.setRGB(j,i,color.getRGB());
            }
        }
        *//*
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/EncryptImg.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();*/
    }

    public static int LsbAnaylize(int val) {
        if (val % 2 == 0) {
            return 0;
        }
        return 1;
    }
    public static int LsbNextAnaylize(int gFirst,int gSec) {
        int fVal = (gFirst - 1) / 2;
        return LsbAnaylize(fVal + gSec);
    }

    //第七部：根据x的值，得到隐写p1或者p2后的信息
    public static List<Integer> getGemdEncrypt(int n, int x, int d, List pData) {
        int size = pData.size();
        List<Integer> returnP = new ArrayList();
        if (x == 4) {//d' =Math.pow(2,n+1)-d    [148,171]  -->[148,170]
            int dTransform = (int) Math.pow(2, n + 1) - d;//4
            String strBinary = PhotoUtils.toBinary(n + 1, dTransform);//100
            //反转strBinary，例如将1101 反转成1011
//                String strBinaryReverse = new StringBuilder(strBinary).reverse().toString();//001
            for (int str = 1; str <= n; str++) {
                int sBR = Integer.valueOf(String.valueOf(strBinary.charAt(str - 1)));
                int sBRN = Integer.valueOf(String.valueOf(strBinary.charAt(str)));
                if (sBR == sBRN) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str)));
                    returnP.add(gx);
                }
                if (sBR == 0 && sBRN == 1) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) - 1;
                    returnP.add(gx);//此处得到的数据需要反转回来
                }
                if (sBR == 1 && sBRN == 0) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) + 1;
                    returnP.add(gx);
                }
            }
            Collections.reverse(returnP);
        }
        if (x == 3) {
            String strBinary = PhotoUtils.toBinary(n + 1, x);//11
            for (int str = 1; str <= n; str++) {
                int sBR = Integer.valueOf(String.valueOf(strBinary.charAt(str - 1)));
                int sBRN = Integer.valueOf(String.valueOf(strBinary.charAt(str)));
                if (sBR == sBRN) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str)));
                    returnP.add(gx);
                }
                if (sBR == 0 && sBRN == 1) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) + 1;
                    returnP.add(gx);
                }
                if (sBR == 1 && sBRN == 0) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) - 1;
                    returnP.add(gx);//此处得到的数据需要反转回来
                }
            }
            Collections.reverse(returnP);
        }
        if (x == 2) {
            for (int str = 0; str < size; str++) {
                int data = (int) pData.get(str) + 1;
                returnP.add(data);
            }
        }
        if (x == 1) {
            returnP = pData;
        }
        return returnP;

    }

    //第六步 根据GEMD因写算法,
    public static int returnX(int d, int n) {
        int returnVal = 0;
        int val = (int) Math.pow(2, n);
        if (d == 0) {
            returnVal = 1;
        }
        if (d == val) {
            returnVal = 2;
        }
        if (0 < d & d < val) {
            returnVal = 3;
        }
        if (d > val) {
            returnVal = 4;
        }
        return returnVal;
    }

    //调用f（g1,g2）方法
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
}
