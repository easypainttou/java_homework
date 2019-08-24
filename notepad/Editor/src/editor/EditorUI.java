package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.text.DefaultEditorKit;

public class EditorUI extends JFrame implements ActionListener{
    
    private JTextArea txt;
    private JMenuBar jmb;
    private JMenu file,edit;
    private JMenuItem create,open,save,exit,copy,paste,find,date;
    private FindAndReplace findAndReplace;
    
    public EditorUI(){
        init();
    }
    
    private void init(){
        setSize(400,300);
        setTitle("EasyEditor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        txt = new JTextArea("",0,0);
        txt.setTabSize(4);
        txt.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(txt);
        txt.setWrapStyleWord(true);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().setLayout(new BorderLayout());
        JPanel jp = new JPanel(new BorderLayout());
        
        jmb = new JMenuBar();
        file = new JMenu("文件");
        edit = new JMenu("编辑");
        
        create = new JMenuItem("创建");
        create.addActionListener(this);
        
        open = new JMenuItem("打开");
        open.addActionListener(this);
        
        save = new JMenuItem("保存");
        save.addActionListener(this);
        
        exit = new JMenuItem("退出");
        exit.addActionListener(this);
        
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("复制");
        
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("粘贴");
        
        find = new JMenuItem("搜索/替换");
        find.addActionListener(this);
        
        date = new JMenuItem("日期/时间");
        date.addActionListener(this);
        
        findAndReplace = new FindAndReplace(txt);
        
        file.add(create);
        file.add(open);
        file.add(save);
        file.add(exit);
        edit.add(copy);
        edit.add(paste);
        edit.add(find);
        edit.add(date);
        jp.add(jsp);
        getContentPane().add(jp);
        jmb.add(file);
        jmb.add(edit);
        setJMenuBar(jmb);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == exit){
            this.dispose();
        }else if(e.getSource() == create){
            txt.setText("");
        }else if(e.getSource() == open){
            JFileChooser openChooser=new JFileChooser();
            int option = openChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
                txt.setText("");
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(openChooser.getSelectedFile().getPath()),"UTF-8"));
                    String nextLineStr;
                    while((nextLineStr = in.readLine()) != null){
                        txt.append(nextLineStr+"\n");
                    }
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }else if(e.getSource() == save){
            JFileChooser saveChooser=new JFileChooser();
            int option = saveChooser.showSaveDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
                try{
                    BufferedWriter out = new BufferedWriter(new FileWriter(saveChooser.getSelectedFile().getPath()));
                    out.write(txt.getText());
                    out.close();
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }else if(e.getSource() == find){
            findAndReplace.findPos=0;
            findAndReplace.setLocationRelativeTo(null);
            findAndReplace.setVisible(true);
        }else if(e.getSource() == date){
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyy/MM/dd/HH:mm");
            txt.insert(df.format(date),txt.getCaretPosition());
        }
    }
}

class FindAndReplace extends JFrame implements ActionListener{
    private JTextField find,replace;
    private JButton findBtn,replaceBtn,closeBtn;
    private JLabel jl1,jl2;
    private JTextArea txt;
    public int findPos;
    
    public FindAndReplace(JTextArea textArea){
        this.txt = textArea;
        init();
    }
    
    public void init(){
        setLayout(null);
        setSize(300,190);
        setTitle("搜索/替换");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        find = new JTextField("",10);
        find.setBounds(80, 20, 100, 25);
        
        replace = new JTextField("",10);
        replace.setBounds(80, 90, 100, 25);
        
        findBtn = new JButton("查找");
        findBtn.addActionListener(this);
        findBtn.setBounds(200, 20, 60, 25);
        
        replaceBtn = new JButton("替换");
        replaceBtn.addActionListener(this);
        replaceBtn.setBounds(200, 90, 60, 25);
        
        jl1 = new JLabel("查找内容:");
        jl1.setBounds(10, 20, 60, 25);
        
        jl2 = new JLabel("替换为:");
        jl2.setBounds(10, 90, 60, 15);
        
        add(find);
        add(replace);
        add(findBtn);
        add(replaceBtn);
        add(jl1);
        add(jl2);
        setVisible(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == findBtn){
            find();
        }else if(e.getSource() == replaceBtn){
             txt.setText(txt.getText().replaceAll(find.getText(), replace.getText()));
        }else if(e.getSource() == closeBtn){
            setVisible(false);
        }
    }
    
    private void find(){
        String findStr = find.getText();
        int startPos = txt.getText().indexOf(findStr,findPos);
        int endPos = startPos+findStr.length();
        if(startPos != -1){
            txt.select(startPos, endPos);
            findPos = endPos;
        }else{
            txt.select(0,0);
            findPos = 0;
        }
    }
}