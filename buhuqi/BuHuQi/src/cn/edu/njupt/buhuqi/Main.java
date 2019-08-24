package cn.edu.njupt.buhuqi;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import javax.swing.*;

/**
* 游戏类
* @author eregerm
*/
public class Main extends JFrame{
    private final String rules="<html><body>" +
        "规则：<br/>" +
        "1.本棋供两人玩，四周摆猎犬，中间摆虎。<br/>"+
        "2.由猎犬先走，双方每次只走一步。猎犬不能吃虎，只能围逼虎至陷阱致死或当猎犬仅剩四只时把虎围至任何角落无法走动致死。猎犬可在陷阱走动。<br/>"+
        "3.当两只猎犬在一条线上，中间空位时，老虎走入中间，可以吃掉两边一对猎犬。老虎吃到只剩两只猎犬时算胜。<br/>"+
        "<br/>v1.0.0</p></body></html>"; //游戏规则，显示于“帮助/游戏规则”窗口
    private Board board; //游戏面板
    
    public static void main(String[]args){
        EventQueue.invokeLater(()->{
            Main ex=new Main();
            ex.setVisible(true);
        });
    }
    
    public Main(){
        initUI();
    }
    
    /**
    * 初始化UI
    */
    private void initUI(){
        //添加游戏面板
        board=new Board();
        add(board);
        //添加菜单栏
        createMenu();
        pack();
        //设置窗口显示参数
        setSize(650,650);
        setResizable(false);
        setTitle("捕虎棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    /**
    * 创建菜单栏
    */
    private void createMenu(){
        JMenuBar menuBar=new JMenuBar();
        //菜单栏
        //“游戏”菜单
        JMenu gameMenu=new JMenu("游戏");
        //“设置”菜单
        JMenu settingMenu=new JMenu("设置");
        //“帮助”菜单
        JMenu helpMenu=new JMenu("帮助");
        //“游戏”菜单项
        //“新游戏”选项
        JMenuItem newGameItem=new JMenuItem("新游戏");
        newGameItem.setToolTipText("开始玩一盘新游戏");
        newGameItem.addActionListener((e)->{
            board.inGame=true;
            board.newGame=true;
            board.selectStep=false;
            board.whoseTurn=board.firstHand;
        });
        //“停止游戏”选项
        JMenuItem stopItem=new JMenuItem("停止游戏");
        stopItem.setToolTipText("停止游戏，回到标题界面");
        stopItem.addActionListener((e)->{
            Sound.playSE("se/se_hint00.wav");
            if(board.inGame){
                board.msg="游戏已停止，选择“游戏/新游戏”以开始新游戏";
                board.inGame=false;
            }else board.msg="游戏未开始！";
        });
        //“悔棋”选项
        JMenuItem repentItem=new JMenuItem("悔棋");
        repentItem.setToolTipText("撤销最近的一步");
        repentItem.addActionListener((e)->{
            board.repent=true;
        });
        //“退出游戏”选项
        JMenuItem exitItem=new JMenuItem("退出游戏");
        exitItem.setToolTipText("退出游戏，关闭窗口");
        exitItem.addActionListener((e)->System.exit(0));
        //“设置”菜单项
        //“设置游戏参数”选项
        JMenuItem setPItem=new JMenuItem("设置游戏参数");
        setPItem.setToolTipText("可设置游戏的先手及最长时间");
        setPItem.addActionListener((e)->{
            //“设置游戏参数”窗口
            final JDialog dialog = new JDialog(this, "设置游戏参数", true);
            JPanel p = new JPanel();
            GridBagLayout gbaglayout=new GridBagLayout(); 
            p.setLayout(gbaglayout);
            //“先手”设置
            JLabel cl=new JLabel("先手:",JLabel.LEFT);
            ButtonGroup firstHand=new ButtonGroup();
            //“老虎”单选按钮
            JRadioButton tigerItem=new JRadioButton("老虎");
            tigerItem.addItemListener((se)->{
                if(se.getStateChange()==ItemEvent.SELECTED){
                    board.firstHand=0;
                }
            });
            //“猎犬”单选按钮
            JRadioButton dogItem=new JRadioButton("猎犬");
            dogItem.addItemListener((se)->{
                if(se.getStateChange()==ItemEvent.SELECTED){
                    board.firstHand=1;
                }
            });
            if(board.firstHand==1)dogItem.setSelected(true);
            else tigerItem.setSelected(true);
            firstHand.add(tigerItem);
            firstHand.add(dogItem);
            //“最长时间”设置
            JLabel lb=new JLabel("最长时间（单位：s）: ",JLabel.LEFT);
            //文本域
            final JTextField tf=new JTextField(Long.toString(board.maxTime));
            //“设定”按钮
            JButton okButton = new JButton("设定");
            okButton.addActionListener((se) -> {
                String data =tf.getText();
                boolean f=true;
                if(data.length()>=10){
                    Sound.playSE("se/se_invalid.wav");
                    board.msg="设定的时间太长了！";
                    return;
                }
                for(int i=0;i<data.length();i++){
                    if(!Character.isDigit(data.charAt(i)))f=false;
                }
                if(f){
                    board.maxTime=Integer.valueOf(data);
                    Sound.playSE("se/se_hint00.wav");
                    board.msg="设定游戏最长时间成功！";
                }else {
                    Sound.playSE("se/se_invalid.wav");
                    board.msg="非法时间！";
                }
            });
            //“关闭”按钮
            JButton closeButton = new JButton("关闭");
            closeButton.addActionListener((se) -> {
                dialog.dispose();
            });
            //添加各组件
            addComponent(cl,gbaglayout,0,0,0);
            addComponent(tigerItem,gbaglayout,1,0,0);
            addComponent(dogItem,gbaglayout,2,0,0);
            addComponent(lb,gbaglayout,0,1,10);
            addComponent(tf,gbaglayout,1,1,10);
            addComponent(okButton,gbaglayout,2,1,10);
            addComponent(closeButton,gbaglayout,3,3,50);
            p.add(closeButton);
            p.add(lb);
            p.add(tf);
            p.add(okButton);
            p.add(cl);
            p.add(tigerItem);
            p.add(dogItem);
            dialog.add(p);
            //设置窗口显示参数
            dialog.setSize(400, 200);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
        //“帮助”菜单项
        //“游戏规则”选项
        JMenuItem helpItem=new JMenuItem("游戏规则");
        helpItem.setToolTipText("查看“捕虎棋”的游戏规则");
        helpItem.addActionListener((e)->{
            final JDialog dialog = new JDialog(this, "游戏规则", true);
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(1, 1));
            JLabel rl = new JLabel(rules);
            //添加各组件
            p.add(rl);
            dialog.add(p);
            //设置窗口显示参数
            dialog.setSize(300, 250);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
        //添加各组件
        gameMenu.add(newGameItem);
        gameMenu.add(stopItem);
        gameMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        gameMenu.add(repentItem);
        gameMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        gameMenu.add(exitItem);
        settingMenu.add(setPItem);
        helpMenu.add(helpItem);
        menuBar.add(gameMenu);
        menuBar.add(settingMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    /**
    * 以特定方式添加组件
    * @param jc 组件
    * @param gbaglayout 目标网格袋布局管理器
    * @param x 单元格横坐标
    * @param y 单元格纵坐标
    * @param top 组件的外部填充顶部距离
    */
    private void addComponent(JComponent jc,GridBagLayout gbaglayout,int x,int y,int top){
        GridBagConstraints constraints=new GridBagConstraints();
        constraints.fill=GridBagConstraints.BOTH;
        constraints.gridx=x;
        constraints.gridy=y;
        constraints.insets=new Insets(top,1,1,1);
        gbaglayout.setConstraints(jc,constraints);
    }
}
