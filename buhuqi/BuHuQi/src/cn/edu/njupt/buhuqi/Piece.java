package cn.edu.njupt.buhuqi;

/**
* 棋子类
* @author eregerm
*/
public class Piece extends Sprite{
    private int id; //棋子的位置编号
    
    public Piece(int id,String path){
        super(path);
        this.id=id;
        initPiece();
    }
    
    /**
    * 初始化棋子的坐标
    */
    private void initPiece(){
        x=Board.IdMapX(id)-w/2;
        y=Board.IdMapY(id)-h/2;
    }
    
    /**
    * 获取棋子的位置编号
    * @return 棋子的位置编号
    */
    public int getId(){
        return id;
    }
    
    /**
    * 设置棋子的位置编号
    * @param id 位置编号
    */
    public void setId(int id){
        this.id=id;
        x=Board.IdMapX(this.id)-w/2;
        y=Board.IdMapY(this.id)-h/2;
    }
}