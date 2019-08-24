package cn.edu.njupt.buhuqi;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
* Sprite类
* @author eregerm
*/
public class Sprite{
    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected boolean visible;
    protected Image im;
    
    public Sprite(String path){
        this.x=0;
        this.y=0;
        visible=true;
        loadImage(path);
    }
    
    public Sprite(int x,int y,String path){
        this.x=x;
        this.y=y;
        visible=true;
        loadImage(path);
    }
    
    /**
    * 加载图像
    * @param path 图像路径
    */
    private void loadImage(String path){
        ImageIcon ii=new ImageIcon(path);
        im=ii.getImage();
        w=im.getWidth(null);
        h=im.getHeight(null);
    }
    
    /**
    * 获取横坐标
    * @return 横坐标
    */
    public int getX(){
        return x;
    }
    
    /**
    * 获取纵坐标
    * @return 纵坐标
    */
    public int getY(){
        return y;
    }
    
    /**
    * 获取宽度
    * @return 宽度
    */
    public int getWidth(){
        return w;
    }
    
    /**
    * 获取高度
    * @return 高度
    */
    public int getHeight(){
        return h;
    }
    
    /**
    * 获取是否可见
    * @return 是否可见
    */
    public boolean getVisible(){
        return visible;
    }
    
    /**
    * 获取图像
    * @return 图像
    */
    public Image getImage(){
        return im;
    }
    
    /**
    * 设置横坐标
    * @param x 横坐标
    */
    public void setX(int x){
        this.x=x;
    }
    
    /**
    * 设置纵坐标
    * @param y 纵坐标
    */
    public void setY(int y){
        this.y=y;
    }
    
    /**
    * 设置是否可见
    * @param  visible 是否可见
    */
    public void setVisible(boolean visible){
        this.visible=visible;
    }
}