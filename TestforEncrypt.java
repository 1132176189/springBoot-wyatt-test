package com.yt.test;

import com.zx.project.PhotoUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/*
      Descrption: 基于数据拆分和组合算法的增强型 GEMD 图像隐写算法代码实现
      Version：1.0.0
      Created By : 赵鑫
 */
public class TestforEncrypt {
    public static void main(String[] args) throws IOException {
        //隐写第一步：根据输入图片得到原始像素值：
        String path = "D:/car.png";
//        List<Integer> pData=PhotoUtils.changeImgtoGray(new File(path));
        int dataFeed[] = {148, 171, 165, 95};
        List<Integer> pData = Arrays.stream(dataFeed).boxed().collect(Collectors.toList());
        //加密信息,待定
        String jiaMiData = "1010100011";
        List<Integer> p1 = new ArrayList<>();
        List<Integer> p2 = new ArrayList<>();
        //第一步：将原始信息分为两组（判断基数和偶数）.第一组是p1.第二组是p2
        if ((pData.size() % 2 == 0)) {
            p1 = pData.subList(0, pData.size() / 2);
            p2 = pData.subList(pData.size() / 2, pData.size());
        } else {
            p1 = pData.subList(0, pData.size() / 2);
            p2 = pData.subList(pData.size() / 2, pData.size() - 1);
        }
        System.out.println("p1:" + p1);
        System.out.println("p2:" + p2);
        //第二步，拆分后每一个p里面都有pData/2个像素
        int nPx = pData.size() / 2;
        //讲秘密信息分解成两组，一组是n+2，一组是
        String jiaMi1 = jiaMiData.substring(0, pData.size() + 2);
        String jiaMi2 = jiaMiData.substring(pData.size() + 2, jiaMiData.length());
        System.out.println("jiaMi1:" + jiaMi1);
        System.out.println("jiaMi2:" + jiaMi2);
        int jiaMiDecimal1 = Integer.parseInt(jiaMi1, 2);
        System.out.println("jiaMiDecimal1:" + jiaMiDecimal1);
        int jiaMiDecimal2 = Integer.parseInt(jiaMi2, 2);
        System.out.println("jiaMiDecimal2:" + jiaMiDecimal2);
        //这里要对42进行一个分解
        //42 = pow(2,n+1) *x+y
        int xValue = (int) (42 / Math.pow(2, nPx + 1));//5
        int yValue = (int) (42 % Math.pow(2, nPx + 1));//2
        System.out.println("xValue:" + xValue);
        System.out.println("yValue:" + yValue);
        //调用f（g1,g2）方法
        int f1 = fFunction(p1);//5
        System.out.println("f1:" + f1);
        int f2 = fFunction(p2);//2
        System.out.println("f2:" + f2);
        //第五步：根据（3-14）计算d1 和d2
        int modData = (int) Math.pow(2, 2 * 2 - 1);//等于8
        int d1 = Math.floorMod(yValue - f1, modData);
        int d2 = Math.floorMod(xValue - f2, modData);
        System.out.println("d1:" + d1);
        System.out.println("d2:" + d2);
        //第六步 根据GEMD因写算法,
        int x1 = returnX(d1, nPx);//4
        int x2 = returnX(d2, nPx);//3
        System.out.println("x1:" + x1);
        System.out.println("x2:" + x2);
        //第七部：得到隐写后的信息（1，0）减1，（0，0）或者（1，1）相等，（0，1）就加1
        //pEncry1 是p1隐写后的数据
        List<Integer> pEncry1 = getEncrypt(nPx, x1, d1, p1);
        //pEncry2 是p2隐写后的数据
        List<Integer> pEncry2 = getEncrypt(nPx, x2, d2, p2);
        System.out.println("隐写算法之后的p1:" + pEncry1);
        System.out.println("隐写算法之后的p2:" + pEncry2);
        pEncry1.addAll(pEncry2);
        System.out.println("隐写后的图像:" + pEncry1);
        //第八步：利用 LSB 匹配算法嵌入额外的 n 位秘密数据 M2=（0011）
        //pEncry1和pEncry2
        List<Integer> listLsb = getLsbFunction(pEncry1, jiaMi2);
        BufferedImage img = ImageIO.read(new File(path));
        printPhoto(listLsb,img);


        //






    }

    //将隐写后的像素值输出为图片
    public static void printPhoto(List<Integer> printList,BufferedImage img)throws IOException {
        int width = img.getWidth();
        int height =img.getHeight();
        for (int j = height-1; j >=0; j--) {
            for (int i = 0; i <= width-1; i++) {
                int gray =printList.get(width*(height-j-1)+i);
                Color color = new Color(gray,gray,gray);
                img.setRGB(j,i,color.getRGB());
            }
            System.out.println();
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/Encrypt.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();
    }

    //jiaMi = 0011
    public static List<Integer> getLsbFunction(List<Integer> pEncry1, String jiaMi) {
        List<Integer> lsbList = new ArrayList();
        for (int i = 1; i <= pEncry1.size(); i++) {//148  170  165 96
            if (i % 2 == 0) {//判断当前是不是g的偶数位数
                List<Integer> list = new ArrayList();
                list.add(pEncry1.get(i - 2));//148 . 165,
                list.add(pEncry1.get(i - 1));//170    96
                int gFirst, gSec = 0;
                //4-2+1=3
                if (jiaMi.charAt(jiaMi.length() - i + 1) == LsbAnaylize(list.get(0))) {
                    gFirst = list.get(0);//old
//                    gSec = list.get(1);//new
                    //4-2=2  //0011  JiaMi.get(2)
                    int bn = Integer.valueOf(jiaMi.charAt(jiaMi.length() - i));
                    int lsbNextAnaylize =LsbNextAnaylize(list.get(0),list.get(1));
                    if (bn == lsbNextAnaylize) {
                        gSec = list.get(1);//old
                    } else {
                        gSec = list.get(1) + 1;//old0
                    }
                } else {//148  170  165 96
                    gSec = list.get(1);
                    int bn = Integer.valueOf(jiaMi.charAt(jiaMi.length() - i));
                    int lsbNextAnaylize = LsbNextAnaylize(list.get(0)-1,list.get(1));
                    if (bn == lsbNextAnaylize) {
                        gFirst = list.get(0) - 1;
                    } else {
                        gFirst = list.get(0) + 1;
                    }
                }
                lsbList.add(gFirst);
                lsbList.add(gSec);
            }
        }
        return lsbList;
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
    public static List<Integer> getEncrypt(int n, int x, int d, List pData) {
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
