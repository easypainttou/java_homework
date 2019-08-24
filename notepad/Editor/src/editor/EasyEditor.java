package editor;

import java.awt.EventQueue;

public class EasyEditor {
    public static void main(String[]args){
        EventQueue.invokeLater(()->{
            new EditorUI().setVisible(true);
        });
    }
}
