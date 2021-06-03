package com.yt.test;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.util.List;

public class PhotoUtils {
    //将隐写后的像素值输出为图片
    public static void printJimPhoto(List<Integer> printList)throws IOException {
        BufferedImage img = ImageIO.read(new File("D:/Origin.png"));
        int width = img.getWidth();
        int height =img.getHeight();
        for (int i = 0; i <width;i++) {
            for (int j = 0; i <= height; j++) {
                if(printList.size()<=i*j){
                    int gray =printList.get(width*i+j);
                    Color newColor = new Color(gray,gray,gray);
                    img.setRGB(j,i,newColor.getRGB());
                }
            }
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");//自定义图像格式
        ImageWriter writer = it.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File("D:/EncryptImg.png"));//自定义图像路径
        writer.setOutput(ios);
        writer.write(img);
        img.flush();
        ios.flush();
    }
    public static String getJiaMiImagePx(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height =img.getHeight();
        int []data = new int[width*width];
        img.getRGB(0,0,width,height,data,0,width);


        StringBuilder jiaMiStr= new StringBuilder("");
        for (int x = 0; x<data.length; x++) {
            Color color = new Color(data[x]);
            jiaMiStr.append(toBinary(8,color.getBlue()));
        }
        return jiaMiStr.toString();
    }

    public static List<Integer> getOriImagePx2(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        List<Integer> dataFeed = new ArrayList<>();
        int[] rgb = new int[3];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = img.getRGB(w, h);//获得像素值
                rgb[1] = (pixel & 0xff00) >> 8;
                dataFeed.add(rgb[1]);
            }
        }
        return dataFeed;
    }

    public static List<Integer> getOriImagePx(File file) throws IOException {

        BufferedImage img = ImageIO.read(file);
        int []data = new int[img.getWidth()*img.getHeight()];
        img.getRGB(0,0,512,512,data,0,512);
        List<Integer> dataFeed = new ArrayList<>();
        for(int a=0;a<data.length;a++){
            Color color = new Color(data[a]);
            dataFeed.add(color.getBlue());
        }
        return dataFeed;
    }

    //讲十进制转换成二进制
    public static String toBinary(int digit, int num) {
        int value = 1 << digit | num;
        String bs = Integer.toBinaryString(value);
        return bs.substring(1);
    }

    //LSB算法
    public static String returnLsb(List<Integer> list) {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < list.size(); i++) {//148  170  165 96
            String binary = "";
            if (i % 2 == 0) {
                if (list.get(i) % 2 == 0) {
                    binary = "0";
                } else {
                    binary = "1";
                }
                str.append(binary);
            } else {
                if (list.get(i - 1) % 2 == 0) {
                    binary = "0";
                } else {
                    binary = "1";
                }
                str.append(binary);
            }
        }
        return str.reverse().toString();
    }

    public static void writePhoto(int value1, int value2) {

        File photo = new File("C:/Program Files/Java/trace.txt");

        try {
            if (!photo.exists()) {
                photo.createNewFile();
            }
            FileWriter inLine1 = new FileWriter(photo);
            inLine1.write("");
            inLine1.flush();
            FileWriter inLine2 = new FileWriter(photo,true);
            inLine2.write("" + value1 + ">" + value2+"\n");
            inLine1.close();
            inLine2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writePhoto(List<Map<String,Integer>>list) {

        File photo = new File("C:/Program Files/Java/trace.txt");

        try {
            if (!photo.exists()) {
                photo.createNewFile();
            }
            FileWriter inLine1 = new FileWriter(photo);
            inLine1.write("");
            inLine1.flush();
            FileWriter inLine2 = new FileWriter(photo,true);
            for(Map<String,Integer> map :list){
                inLine2.write("" + map.get("value1") + ">" + map.get("value2")+"\n");
            }
            inLine1.close();
            inLine2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> readPhoto() {
        Map<String, Object> map = new HashMap();
        File photo = null;
        try {
            photo = new File("C:/Program Files/Java/trace.txt");
            if (!photo.exists()) {
                photo.createNewFile();
            }
            FileReader fr = new FileReader(photo);
            BufferedReader br = new BufferedReader(fr);
            String value = br.readLine();
            map.put("name", value.substring(0, value.indexOf(">")));
            map.put("size", value.substring(value.indexOf(">") + 1, value.length()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    public static List<Integer>getReverseLSB(List<Integer>list,int dValue){
        List<Integer>upList = new ArrayList<>();
        int fValue=0;
        int sValue=0;
        switch(dValue){
            case 1:
                fValue=list.get(0);
                sValue=list.get(1);
                break;
            case 2:
                fValue=list.get(0);
                sValue=list.get(1)-1;
                break;
            case 3:
                fValue=list.get(0)+1;
                sValue=list.get(1);
                break;
            case 4:
                fValue=list.get(0)-1;
                sValue=list.get(1);
                break;
        }
        upList.add(fValue);
        upList.add(sValue);
        return upList;
    }
    public static  List<Map<String,Integer>> getDPx(){
        List<Map<String,Integer>> retunList = new ArrayList<>();
        File photo = null;
        try {
            photo = new File("C:/Program Files/Java/trace.txt");
            if (!photo.exists()) {
                photo.createNewFile();
            }
            FileReader fr = new FileReader(photo);
            BufferedReader br = new BufferedReader(fr);
            String value = "";
            while ((value =br.readLine())!=null){
                Map<String, Integer> map = new HashMap();
                map.put("name", Integer.valueOf(value.substring(0, value.indexOf(">"))));
                map.put("size", Integer.valueOf(value.substring(value.indexOf(">") + 1, value.length())));
                retunList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return retunList;
        }
    }

    public static  Map<String,Object> getLsbFunction(List<Integer> pEncry1, String jiaMi) {
        Map<String,Object> map =new HashMap<>();
        List<Integer> lsbList = new ArrayList();
        //pEncry1 例如：[205,198,156,178]    jiaMi:  1001
        for (int i = 1; i <= pEncry1.size(); i++) {
            //判断当前是不是g的偶数位数
            if (i % 2 == 0) {
                List<Integer> list = new ArrayList();
                list.add(pEncry1.get(i - 2));
                list.add(pEncry1.get(i - 1));
                //list :[205,198]==>[gFirst,gSec]
                int gFirst, gSec = 0;
                //f1:加密信息倒数取值b0:1
                int f1 = Integer.valueOf(String.valueOf(jiaMi.charAt(jiaMi.length() - i + 1)));
                //f2:LSB(205)
                int f2 = LsbAnaylize(list.get(0));
                if (f1 == f2) {
                    //g1' : g1 :list.get(0) :205
                    gFirst = list.get(0);
                    //b1: jiaMi:  1001 :0
                    int bn = Integer.valueOf(String.valueOf(jiaMi.charAt(jiaMi.length() - i)));
                    //lsbNextAnaylize 计算
                    int lsbNextAnaylize = LsbNextAnaylize(list.get(0), list.get(1));

                    if (bn == lsbNextAnaylize) {
                        map.put("value"+i+"",1);
                        gSec = list.get(1);
                    } else {
                        map.put("value"+i+"",2);
                        gSec = list.get(1) + 1;
                    }
                }else {
                    gSec = list.get(1);
                    int bn = Integer.valueOf(String.valueOf(jiaMi.charAt(jiaMi.length() - i)));
                    int lsbNextAnaylize = LsbNextAnaylize(list.get(0) - 1, list.get(1));

                    if (bn == lsbNextAnaylize) {
                        map.put("value"+i+"",3);
                        gFirst = list.get(0) - 1;
                    } else {
                        map.put("value"+i+"",4);
                        gFirst = list.get(0) + 1;
                    }
                }
                lsbList.add(gFirst);
                lsbList.add(gSec);
            }
        }
        map.put("lsbList",lsbList);
        return map;
    }
    public static List<Integer> decrForLsb(List<Integer> inList, int d1,int d2) {
        List<Integer>outList = new ArrayList<>();
        for(int i=1;i<=inList.size();i++){
            List<Integer>reverList;
            if(i%4==0){
                reverList = inList.subList(2,4);
                outList.addAll(getReverseLSB(reverList,d2));
                break;
            }
            if(i%2==0){
                reverList = inList.subList(0,2);
                outList.addAll(getReverseLSB(reverList,d1));
            }
        }
        return outList;
    }
//第奇数位的lsb值
    public static int LsbAnaylize(int val) {
        if (val % 2 == 0) {
            return 0;
        }
        return 1;
    }
    //第偶数位的lsb值
    public static int LsbNextAnaylize(int gFirst, int gSec) {
        int fVal = gFirst / 2;
        return LsbAnaylize(fVal + gSec);
    }
    public static List<Integer> getGemdEncrypt(int n, int x, int d, List pData) {
        int size = pData.size();
        List<Integer> returnP = new ArrayList();
        if (x == 4) {//d' =Math.pow(2,n+1)-d    [148,171]  -->[148,170]
            int dTransform = (int) Math.pow(2, n + 1) - d;//
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
            String strBinary = PhotoUtils.toBinary(n + 1, d);//11
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
    public static List<Integer> getGemdDecrypt(int n, int x, int d, List<Integer> pData) {
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
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) + 1;
                    returnP.add(gx);//此处得到的数据需要反转回来
                }
                if (sBR == 1 && sBRN == 0) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) - 1;
                    returnP.add(gx);
                }
            }
            Collections.reverse(returnP);
        }
        if (x == 3) {
            String strBinary = PhotoUtils.toBinary(n + 1, d);//11
            for (int str = 1; str <= n; str++) {
                int sBR = Integer.valueOf(String.valueOf(strBinary.charAt(str - 1)));
                int sBRN = Integer.valueOf(String.valueOf(strBinary.charAt(str)));
                if (sBR == sBRN) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str)));
                    returnP.add(gx);
                }
                if (sBR == 0 && sBRN == 1) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) - 1;
                    returnP.add(gx);
                }
                if (sBR == 1 && sBRN == 0) {
                    int gx = Integer.valueOf(String.valueOf(pData.get(n - str))) + 1;
                    returnP.add(gx);//此处得到的数据需要反转回来
                }
            }
            Collections.reverse(returnP);
        }
        if (x == 2) {
            for (int str = 0; str < size; str++) {
                int data = (int) pData.get(str) - 1;
                returnP.add(data);
            }
        }
        if (x == 1) {
            returnP = pData;
        }
        return returnP;
    }

    //得到GEMD反向的值
    public static String getDescryptList(StringBuilder stringBuilder,List<Integer>inList){
        stringBuilder.append(PhotoUtils.fFunction(inList));
        return stringBuilder.toString();
    }
    //调用f（g1,g2）方法
    public static String fFunction(List<Integer> inList) {
        int modData = (int) Math.pow(2, inList.size()-1);
        int x =0 ;
        int y =0;
        for (int i = 1; i <= inList.size(); i++) {
            int data= 0;
            if(i==2){
                data =inList.get(i-2)*1+inList.get(i-1)*3;
                y=Math.floorMod(data,modData);
            }
            if(i==4){
                data =inList.get(i-2)*1+inList.get(i-1)*3;
                x =Math.floorMod(data,modData);
            }
        }
        int mValue = (modData* x)+y;
        return PhotoUtils.toBinary(inList.size() + 2, mValue);
    }
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
    public static int caculateJiaMi(){
        int jiaMiStr =256*256*8;
        int strBatch = jiaMiStr/10;
        int pxBatch = strBatch *4;
        return pxBatch ;
    }
    public static String lsbAction (List<Integer> lsbList){
        StringBuilder jiaMiStr = new StringBuilder("");
        for (int i=1;i<=lsbList.size();i++){
            if(i%2==0){
                jiaMiStr.append(LsbNextAnaylize(lsbList.get(i-2),lsbList.get(i-1)));
            }else{
                jiaMiStr.append(LsbAnaylize(lsbList.get(i-1)));
            }
        }
        return jiaMiStr.reverse().toString();
    }

}
