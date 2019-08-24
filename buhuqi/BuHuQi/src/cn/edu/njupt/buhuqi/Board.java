package cn.edu.njupt.buhuqi;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
* 游戏面板类
* @author eregerm
*/
public class Board extends JPanel implements ActionListener{
    private final int DELAY=5; //每隔DELAY ms，timer调用actionPerformed() 
    
    private int preId; //选中的棋子的位置的编号
    private long lastTime; //剩余时间
    private Sprite titleImage; //标题画面
    private Sprite chessBoard; //棋盘
    private Rectangle mainRoom; //选中的位置
    private Rectangle[] room; //所有可选的位置
    private Manual curManual; //当前的棋局
    private List<Manual> allManuals; //历史棋局
    private Date dt; //游戏开始的时刻
    private Timer timer; //计时器
    
    public boolean inGame; //是否游戏中
    public boolean newGame; //是否按下“新游戏”按钮
    public boolean selectStep; //某个玩家做出决策时，需要先选择棋子再选择空位，若当前处于选择棋子的步骤，则变量为false，若处于选择空位的步骤，则变量为true
    public boolean repent; //是否按下“悔棋”按钮
    public int firstHand; //若老虎先手，则为false，若猎犬先手，则为true
    public int whoseTurn; //若轮到老虎，则为false，若轮到猎犬，则为true
    public long maxTime; //最长时间
    public String msg; //提示信息
    
    public static int [][]map; //根据棋盘各位置之间是否连通建立的无向图对应的邻接矩阵
    
    public Board(){
        initBoard();
    }
    
    /**
    * 初始化游戏面板
    */
    private void initBoard(){
        //初始化各变量
        repent=false;
        inGame=false;
        firstHand=1; //默认先手：猎犬
        maxTime=100; //默认最长时间：100s
        mainRoom=new Rectangle(); //用矩形表示选中的位置
        room=new Rectangle[29]; //用矩形表示可选的位置
        curManual=new Manual();
        allManuals=new ArrayList<>();
        titleImage=new Sprite(0,0,"img/title.png");
        chessBoard=new Sprite(0,0,"img/chessBoard.png");
        msg="选择“游戏/新游戏”以开始新游戏";
        initMap();
        setPreferredSize(new Dimension(640,480));
        setBackground(Color.white);
        addMouseListener(new TAdapter());
        setFocusable(true);
        timer=new Timer(DELAY,this);
        timer.start();
        Sound.playSE("se/se_hint00.wav");
    }
    
    /**
    * 初始化map及相关变量
    */
    private void initMap(){
        //暴力建图
        map=new int[30][30];
        for(int i=0;i<5;i++){
            for(int j=0;j<4;j++){
		int u=i+5*j,v=i+5*(j+1);
		map[u][v]=1;
            }
	}
	for(int i=0;i<5;i++){
            for(int j=0;j<4;j++){
		int u=i*5+j,v=i*5+(j+1);
		map[u][v]=1;
            }
	}
	map[10][6]=map[6][2]=map[20][16]
                =map[16][12]=map[12][8]=map[8][4]
                =map[22][18]=map[18][14]=map[10][16]
                =map[16][22]=map[0][6]=map[6][12]
                =map[12][18]=map[18][24]=map[2][8]
                =map[8][14]=map[28][25]=map[27][22]
                =map[28][27]=map[25][22]=map[28][26]
                =map[26][22]=map[25][26]=map[26][27]=1;
	for(int i=0;i<29;i++){
            for(int j=0;j<29;j++){
		map[i][j]=map[i][j]|map[j][i];
            }
            //初始化可选位置对应的矩形
            room[i]=(new Rectangle(IdMapX(i)-30, IdMapY(i)-30,60,60));
	}
        //初始化猎犬
        for(int i=0;i<5;i++){
            curManual.dogs[i]=new Piece(i*5,"img/dog.png");
            curManual.dogs[5+i]=new Piece(4+i*5,"img/dog.png");
        }
       for(int i=0;i<3;i++){
           curManual.dogs[10+i]=new Piece(i+1,"img/dog.png");
           curManual.dogs[13+i]=new Piece(i+21,"img/dog.png");
       }
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);
    }
    
    /**
    * 绘制游戏面板的画面
    */
    private void doDrawing(Graphics g){
        if(inGame&&!newGame){
            drawGame(g);
        }else{
            drawTitle(g);
        }
        //显示提示信息
        Graphics2D g2d=(Graphics2D)g;
        g2d.setFont(new Font("宋体",Font.BOLD, 20));
        g2d.setColor(Color.black);
        g2d.drawString("提示: "+msg, 10, 500);
    }
    
    /**
    * 绘制标题画面
    */
    private void drawTitle(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        g2d.drawImage(titleImage.getImage(),titleImage.getX(),
                titleImage.getY(),this);
    }
    
    /**
    * 绘制游戏画面
    */
    private void drawGame(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        g2d.drawImage(chessBoard.getImage(),chessBoard.getX(),
                chessBoard.getY(),this);
        g2d.setColor(Color.RED);
        g2d.draw(mainRoom);
        g2d.drawImage(curManual.tiger.getImage(),curManual.tiger.getX(),
                curManual.tiger.getY(),this);
        for(int i=0;i<16;i++){
            if(curManual.dogs[i].getVisible())
                g2d.drawImage(curManual.dogs[i].getImage(),curManual.dogs[i].getX(),
                curManual.dogs[i].getY(),this);
        }
        g2d.setColor(Color.blue);
        g2d.setFont(new Font("宋体",Font.BOLD, 18));
        g2d.drawString("猎犬数量： "+curManual.dogNum, 10, 540);//显示猎犬数量
        Date dt2=new Date(); //现在的时间
        long d=maxTime-(dt2.getTime()-dt.getTime())/1000; //最长时间减去游戏开始后经过的时间，即剩余时间，单位s
        g2d.drawString("剩余时间： "+d+" s", 10, 560); //显示剩余时间
        //剩余时间更新
        if(d!=lastTime){
            Sound.playSE("se/se_lgods1.wav");
            lastTime=d;
        }
        //超出最长时间，游戏结束
        if(d<=0){
            inGame=false;
            Sound.playSE("se/se_hint00.wav");
            msg="时间到了！";
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        //检测是否按下“悔棋”按钮
        if(repent){
            if(inGame&&allManuals.size()>0){
                curManual.copy(allManuals.get(allManuals.size()-1));
                allManuals.remove(allManuals.size()-1);
                whoseTurn^=1;
                Sound.playSE("se/se_hint00.wav");
                msg="悔棋成功！"+"轮到"+(whoseTurn==0?"老虎":"猎犬");
            }else{
                Sound.playSE("se/se_invalid.wav");
                msg="不能悔棋！";
            }
            repent=false;
        }
        //检测是否按下“新游戏”按钮
        if(newGame){
            initChessBoard();
            newGame=false;
            repent=false;
        }
        repaint();
    }
    
    /**
    * 初始化棋盘
    */
    private void initChessBoard(){
        dt=new Date(); //设置游戏开始的时刻
        allManuals.clear(); //清空历史棋局
        mainRoom=new Rectangle(); //初始化选择的位置
        Sound.playSE("se/se_hint00.wav");
        msg="先手是"+(whoseTurn==0?"老虎":"猎犬");
        curManual.dogNum=16; //设置猎犬的初始数量
        //初始化老虎和猎犬的位置
        curManual.tiger.setId(12);
        for(int i=0;i<5;i++){
            curManual.dogs[i].setId(i*5);
            curManual.dogs[5+i].setId(4+i*5);
        }
       for(int i=0;i<3;i++){
           curManual.dogs[10+i].setId(i+1);
           curManual.dogs[13+i].setId(i+21);
       }
       for(int i=0;i<16;i++){
           curManual.dogs[i].setVisible(true);
       }
    }
    
    /**
    * 通过位置编号获取横坐标
    * @param id 位置编号
    * @return 横坐标
    */
    static public int IdMapX(int id){
        if(id>=0&&id<=4){
            return 620;
        }else if(id>=5&&id<=9){
            return 520;
        }else if(id>=10&&id<=14){
            return 420;
        }else if(id>=15&&id<=19){
            return 320;
        }else if(id>=20&&id<=24){
            return 220;
        }else if(id>=25&&id<=27){
            return 120;
        }else{
            return 20;
        }
    }
    
    /**
    * 通过位置编号获取纵坐标
    * @param id 位置编号
    * @return 纵坐标
    */
    static public int IdMapY(int id){
        int r=id%5;
        if(id==25){
            return 140;
        }else if(id==26||id==28){
            return 240;
        }else if(id==27){
            return 340;
        }else if(r==0){
            return 40;
        }else if(r==1){
            return 140;
        }else if(r==2){
            return 240;
        }else if(r==3){
            return 340;
        }else{
            return 440;
        }
    }
    
    private class TAdapter extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e){
            if(!inGame)return;
            int cX=e.getX(),cY=e.getY();
            for(int id=0;id<29;id++){
                Rectangle r=room[id];
                if(!(cX>=r.x&&cX<=r.x+r.width&&cY>=r.y&&cY<=r.y+r.height))continue;
                mainRoom=r;
                if(selectStep){
                    if(hasPiece(id)==0&&map[preId][id]==1){
                        Manual tmp=new Manual();
                        tmp.copy(curManual);
                        allManuals.add(tmp);
                        Piece p=getPiece(preId);
                        p.setId(id);
                        Sound.playSE("se/se_ok00.wav");
                        whoseTurn^=1;
                        msg="轮到"+(whoseTurn==0?"老虎":"猎犬");
                        checkSituation();
                    }else{
                        Sound.playSE("se/se_invalid.wav");
                        msg="非法选择，请重新选择！"+"请选择一个"+(whoseTurn==0?"老虎":"猎犬")+"！";
                    }
                    selectStep=false;
                }else{
                    int hasp=hasPiece(id);
                    if(hasp>0&&whoseTurn+1==hasp){
                        selectStep=true;
                        preId=id;
                        Sound.playSE("se/se_ok00.wav");
                        msg="再选择一个空位";
                    }else{
                        Sound.playSE("se/se_invalid.wav");
                        msg="非法选择，请重新选择！"+"请选择一个"+(whoseTurn==0?"老虎":"猎犬")+"！";
                    }
                }
                break;
            }
        }
    }
    
    /**
    * 某个位置上的情况
    * @param id 位置编号
    * @return 0表示无棋子，1表示老虎，2表示猎犬
    */
    private int hasPiece(int id){
        int f=0;
        if(curManual.tiger.getId()==id)f=1;
        for(int i=0;i<16;i++){
            if(curManual.dogs[i].getVisible()&&curManual.dogs[i].getId()==id)
                f=2;
        }
        return f;
    }
    
    /**
    * 某个位置上棋子
    * @param id 位置编号
    * @return 一个Piece对象表示位置上的棋子
    */
    private Piece getPiece(int id){
        Piece p=curManual.tiger;
        if(curManual.tiger.getId()==id)p=curManual.tiger;
        for(int i=0;i<16;i++){
            if(curManual.dogs[i].getVisible()&&curManual.dogs[i].getId()==id)
                p=curManual.dogs[i];
        }
        return p;
    }
    
    /**
    * 检查局面
    */
    private void checkSituation(){
        int tId=curManual.tiger.getId();
        boolean deadFlag=true;
        if(whoseTurn==0){
            for(int i=0;i<29;i++){
		if(map[tId][i]==1){
                    if(hasPiece(i)==0)deadFlag=false;
                }
            }
        }else deadFlag=false;
        if(tId==28||deadFlag){
            inGame=false;
            Sound.playSE("se/se_bonus.wav");
            msg="猎犬赢了！";
        }
        if(whoseTurn==1){
            for(int i=0;i<16;i++){
                int dId=curManual.dogs[i].getId();
                if(!(curManual.dogs[i].getVisible()&&map[dId][tId]==1))continue;
                int dId2=getOpId(tId,dId);
                if(dId2>=0&&hasPiece(dId2)==2){
                    for(int j=0;j<16;j++){
                        if(curManual.dogs[j].getVisible()&&curManual.dogs[j].getId()==dId)
                            curManual.dogs[j].setVisible(false);
                        if(curManual.dogs[j].getVisible()&&curManual.dogs[j].getId()==dId2)
                            curManual.dogs[j].setVisible(false);
                    }
                    curManual.dogNum-=2;
                    Sound.playSE("se/se_pldead00.wav");
                }
            }
        }
        if(curManual.dogNum<=2){
            inGame=false;
            Sound.playSE("se/se_bonus.wav");
            msg="老虎赢了！";
        }
    }
    
    /**
    * 获取猎犬相对老虎对称的另一位置
    * @param tId 老虎位置编号
    * @param dId 猎犬位置编号
    * @return dId相对tId对称的另一位置编号
    */
    private int getOpId(int tId,int dId){
        if(tId==20&&(dId==15||dId==16)
                ||tId==21&&dId==16
                ||tId==23&&dId==18
                ||tId==24&&(dId==19||dId==18))return -1;
        if(tId==22&&dId>=25)return tId-(dId-tId+1);
        if(tId==22&&dId<=18)return tId+(tId-dId-1);
        if(tId==26&&dId==25)return 27;
        if(tId==26&&dId==27)return 25;
        if(tId==26&&dId==22)return 28;
        if(tId==26&&dId==28)return 22;
        int dId2=2*tId-dId;
        if(tId%5==0&&dId2%5==4||tId%5==4&&dId2%5==0||dId2<0)return -1;
        return dId2;
    }
}