package cn.edu.njupt.buhuqi;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
* 声音类
*/
public class Sound extends Thread{
    private final String filename;
    
    public Sound(String wavfile){
        filename=wavfile;
    }
    
    @Override
    public void run(){
        File soundFile=new File(filename);
        AudioInputStream audioInputStream;
        try{
            audioInputStream=AudioSystem.getAudioInputStream(soundFile);
        }catch(IOException|UnsupportedAudioFileException e){
            e.printStackTrace();
            return;
        }
        AudioFormat format=audioInputStream.getFormat();
        SourceDataLine auline;
        DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
        try{
            auline=(SourceDataLine)AudioSystem.getLine(info);
            auline.open(format);
        }catch(LineUnavailableException e){
            e.printStackTrace();
            return;
        }
        auline.start();
        int nBytesRead=0;
        byte[] abData=new byte[512];
        try{
            while(nBytesRead != -1){
                nBytesRead=audioInputStream.read(abData,0,abData.length);
                if(nBytesRead>=0)auline.write(abData,0,nBytesRead);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            auline.drain();
            auline.close();
        }
    }
    
    /**
    * 播放音效
    * @param path 音效路径
    */
    public static void playSE(String path){
        try{
            Sound selectSE=new Sound(path);
            selectSE.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}