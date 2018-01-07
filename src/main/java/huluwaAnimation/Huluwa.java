package huluwaAnimation;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.util.Random;

public class Huluwa extends Thing2D implements Runnable {

    public Huluwa(Field field, int no, int x, int y){
        super(field, x, y);

        switch (no){
            case 1: name="红娃"; break;
            case 2: name="橙娃"; break;
            case 3: name="黄娃"; break;
            case 4: name="绿娃"; break;
            case 5: name="青娃"; break;
            case 6: name="蓝娃"; break;
            case 7: name="紫娃"; break;
            default: assert(false);
        }

//        URL loc1 = this.getClass().getClassLoader().getResource(name+".png");
//        ImageIcon iia1 = new ImageIcon(loc1);
//        Image image1 = iia1.getImage();
//        this.setliveImage(image1);
//        URL loc2 = this.getClass().getClassLoader().getResource(name+"遗像.png");
//        ImageIcon iia2 = new ImageIcon(loc2);
//        Image image2 = iia2.getImage();
//        this.setdeadImage(image2);


        InputStream loc1 = this.getClass().getClassLoader().getResourceAsStream(name+".png");
        //InputStream loc1 = this.getClass().getResourceAsStream("classes/"+name+".png");
        try {
            Image image1 = ImageIO.read(loc1);
            this.setliveImage(image1);
        }catch(IOException e){
            System.out.println("cannot read image for living huluwa.");
        }

        InputStream loc2 = this.getClass().getClassLoader().getResourceAsStream(name+"遗像.png");
        //InputStream loc2 = this.getClass().getResourceAsStream("classes/"+name+"遗像.png");
        try{
            Image image2 = ImageIO.read(loc2);
            this.setdeadImage(image2);
        }catch(IOException e){
            System.out.println("cannot read image for dead huluwa.");
        }
    }

    public void moveToPos(int x, int y) {
        if((y>=0&&y<field.getFieldWidth()) && (x>=0&&x<field.getFieldHeight()) ) {
            field.moveToPos(this.x, this.y, x, y);
        }
    }

    public void kill(){
        isDead=true;
    }

    public void run() {
        //在moveToPos中线程的isDead域可能被设置为true，安全地杀死线程：
        while((!Thread.interrupted()) && (!isDead) && (!field.getCompleted())){
            Random rand = new Random();

            int[] pos = field.getMonsterLoc();
            boolean right = (pos[0]-x>0);
            boolean up = (pos[1]-y>0);

            int increaseX = 0;
            int increaseY = 0;
            switch(rand.nextInt(3)){
                case 0: case 1:
                    increaseX = right?1:-1;
                    increaseY = up?1:-1;
                    break;
                case 2:
                    increaseX = rand.nextInt(3)-1;
                    increaseY = rand.nextInt(3)-1;
                    break;
            }

            try {
                this.moveToPos(this.x+increaseX, this.y+increaseY);
                Thread.sleep(rand.nextInt(500));
               // this.field.repaint();
            }catch (InterruptedException e){
                //System.out.printf("Huluwa thread: %s is interrupted.\n", name);
            }
            this.field.repaint();
        }

    }


}

