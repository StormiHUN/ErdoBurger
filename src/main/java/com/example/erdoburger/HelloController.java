package com.example.erdoburger;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Scanner;

public class HelloController {


    public Pane pane;
    public HBox skinSelector;
    public ToggleGroup szerver;
    public TextField clientData;
    public ImageView ivPlayer;
    public RadioButton szerverCheck;

    Image[] icons = new Image[8];
    ImageView[] heads = new ImageView[icons.length];

    ImageView[][] forest = new ImageView[15][20];
    int[][] t = new int[15][20];

    String iconJel = " TB";
    String[] iconNev = {"null","tree","burger"};
    Image[] icon = new Image[3];

    int py = 1;
    int px = 1;
    int pi = 1;

    DatagramSocket socket = null;

    public void initialize(){
        try {
            socket = new DatagramSocket(678);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < icons.length; i++){
            int ii = i;
            icons[i] = new Image(getClass().getResourceAsStream("icons/head"+i+".png"));
            heads[i] = new ImageView(icons[i]);
            heads[i].setStyle("-fx-opacity: 0.25");
            heads[i].setOnMousePressed(e -> selectHead(ii));
            skinSelector.getChildren().add(heads[i]);
        }
        for(int i = 0; i < 3; i++) icon[i] = new Image(getClass().getResourceAsStream("icons/"+iconNev[i]+".png"));
        for(int s = 0; s < 15; s++){
            for(int o = 0; o < 20; o++){
                forest[s][o] = new ImageView();
                forest[s][o].setLayoutX(10+o*48);
                forest[s][o].setLayoutY(10+s*48);
                t[s][o] = 0;
                pane.getChildren().add(forest[s][o]);
            }
        }
        selectHead(1);
        loadMap("erdo.txt");
        gondolBurger();
        ivPlayer.toFront();
    }

    public void selectHead(int id){
        heads[pi].setStyle("-fx-opacity: 0.25");
        pi = id;
        heads[pi].setStyle("-fx-opacity: 1");
        ivPlayer.setImage(icons[pi]);
    }

    public void loadMap(String path){
        Scanner be = new Scanner(getClass().getResourceAsStream(path));
        for(int s = 0; s < 15; s++){
            String sor = be.nextLine();
            for(int o = 0; o < 20; o++){
                setImg(s, o, iconJel.indexOf(sor.charAt(o)));
                t[s][o] = iconJel.indexOf(sor.charAt(o));
            }
        }
        be.close();
    }

    public void setImg(int y, int x, int id){
        forest[y][x].setImage(icon[id]);
    }

    public void gondolBurger(){
        int s;
        int o;
        do {
            s = (int)(Math.random()*15);
            o = (int)(Math.random()*20);
        }while (t[s][o] != 0);
        t[s][o] = 2;
        setImg(s,o,2);
    }

    public void onKeyPressed(KeyEvent e){
        setImg(py,px,0);
        if(e.getCode() == KeyCode.W && t[py-1][px] != 1){
            py--;
        }
        if(e.getCode() == KeyCode.S && t[py+1][px] != 1){
            py++;
        }
        if(e.getCode() == KeyCode.A && t[py][px-1] != 1){
            px--;
        }
        if(e.getCode() == KeyCode.D && t[py][px+1] != 1){
            px++;
        }
        if(t[py][px] == 2){
            gondolBurger();
            t[py][px] = 0;
        }
        ivPlayer.setLayoutX(10+px*48);
        ivPlayer.setLayoutY(10+py*48);
        if(!szerverCheck.isSelected()){
            kuld(String.format("K;%d:%d:%d",py,px,pi),clientData.getText().split(":")[0],Integer.parseInt(clientData.getText().split(":")[1]));
        }
    }

    public void kuld(String msg, String ip, int port){
        try {
            byte[] adat = msg.getBytes("utf-8");
            InetAddress ipv4 = Inet4Address.getByName(ip);
            DatagramPacket packet = new DatagramPacket(adat,adat.length,ipv4,port);
            socket.send(packet);
            System.out.printf("%s:%d -> %s\n",ip,port,msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}