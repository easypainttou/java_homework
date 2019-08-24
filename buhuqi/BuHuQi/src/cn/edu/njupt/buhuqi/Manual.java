package cn.edu.njupt.buhuqi;

/**
* 棋局类
* @author eregerm
*/
public class Manual{
    public Piece tiger;
    public Piece[] dogs;
    public int dogNum;
    
    public Manual(){
        tiger=new Piece(12,"img/tiger.png");
        dogs=new Piece[29];
        for(int i=0;i<5;i++){
            dogs[i]=new Piece(i*5,"img/dog.png");
            dogs[5+i]=new Piece(4+i*5,"img/dog.png");
        }
        for(int i=0;i<3;i++){
           dogs[10+i]=new Piece(i+1,"img/dog.png");
           dogs[13+i]=new Piece(i+21,"img/dog.png");
        }
        dogNum=0;
    }
    
    /**
    * 拷贝源棋局到本棋局
    * @param b 源棋局
    */
    public void copy(Manual b){
        tiger.setId(b.tiger.getId());
        dogs[0].setId(b.dogs[0].getId());
        for(int i=0;i<16;i++){
            dogs[i].setId(b.dogs[i].getId());
            dogs[i].setVisible(b.dogs[i].getVisible());
        }
        dogNum=b.dogNum;
    }
}