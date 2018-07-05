package pascal.compiler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class PicToAscii {
    //private static final char[] defaultChar=new String("MNHQ$OC?7>!:-;.").toCharArray();
    private static final char[] defaultChar=new String(".,'^~-ocvxnzt+hysk!lpgea*%$wm&#@").toCharArray();//黑白反转
    //设置获取灰度的方格大小 需要设置为矩形，因为字符的大小为矩形
    private static final int NX=2;
    private static final int NY=4;

    public static void main(String[] args){
        //输入一张图片
        String image= transform("image/solon4.jpg");
        System.out.println(image);

    }

    //转换函数
    private static String transform(String path){
        BufferedImage image=getImage(path);
        StringBuffer stringBuffer=new StringBuffer();
        for (int y = 0; y < image.getHeight()-NY; y+=NY) {
            for (int x = 0; x < image.getWidth()-NX; x+=NX) {
                int gray=avgGray(x,y,image);
                int num=(int)Math.floor(gray/8);//  256/18=14个灰度等级
                stringBuffer.append(defaultChar[num]);
            }
            stringBuffer.append("\n\r");
        }
        return stringBuffer.toString();
    }

    //平均灰度
    private static int avgGray(int x,int y,BufferedImage image){
        int result=0;
        for (int i=y;i<y+NY;i++) {
            for (int j=x;j<x+NX;j++) {
                result+=gray(image.getRGB(x,y));
            }
        }
        return result/(NX*NY);
    }




    private static int  gray(int pixel){
        int gray=0;
        int r=(pixel>>16)&0xff;
        int g=(pixel>>8)&0xff;
        int b=(pixel)&0xff;
        gray=(int)(r * 0.3 + g * 0.59 + b * 0.11);
        return gray;
    }


    private static BufferedImage getImage(String path){
        BufferedImage image=null;
        try {
            image=ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("获取图片错误");
            e.printStackTrace();
        }
        return image;
    }
}
