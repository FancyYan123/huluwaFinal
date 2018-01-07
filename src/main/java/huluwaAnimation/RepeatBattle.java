package huluwaAnimation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class RepeatBattle implements Runnable{
    protected FileReader recordReader;
    protected BufferedReader br;
    protected Field field;

    public RepeatBattle(String path, Field field){
        this.field = field;

        try{
            File record = new File(path);
            recordReader = new FileReader(record);
            br = new BufferedReader(recordReader);
        }catch(Exception e){
            System.out.println("Wrong file path.");
        }
    }

    public void close(){
        try {
            recordReader.close();
            br.close();
        }catch(IOException e){
            System.out.println("Exception while closing recorder file.");
        }
    }

    public int[] readFileByLine(){
        int[] rtn=null;
        try{
            String context = br.readLine();
            if(context!=null)
                rtn = analyseLine(context);
        }catch(IOException e){
            System.out.println("Reading process interrupted. ");
        }
        return rtn;
    }

    public int[] analyseLine(String context){

        if(context.length()>0) {
            String[] position = context.split(" ");
            if(position.length<4)
                return null;
            int mark, x1, y1, x2, y2;
            x1 = Integer.parseInt(position[0]);
            y1 = Integer.parseInt(position[1]);
            if(position[2].equals("dead")){
                mark = 1;
                x2 = Integer.parseInt(position[3]);
                y2 = Integer.parseInt(position[4]);
            }
            else{
                mark = 0;
                x2 = Integer.parseInt(position[2]);
                y2 = Integer.parseInt(position[3]);
            }
            return new int[]{mark, x1, y1, x2, y2};
        }
        else
            return null;
    }

    public void run(){
        //复现前先展示初始阵型：
        field.repaint();
        try {
            Thread.sleep(1000);
        }catch(InterruptedException e){

        }

        //从文件中读入并重画：
        Random rand = new Random();
        int[] pos;

        while((pos=readFileByLine())!=null
                && (!Thread.interrupted()) && field.getIsRepeating()){

            if(pos[0]==0){
                Thing2D temp = field.getArea(pos[1], pos[2]);
                temp.setX(pos[3]);
                temp.setY(pos[4]);
                field.setArea(pos[1], pos[2], null);
                field.setArea(pos[3], pos[4], temp);
            }
            else{
                Thing2D temp = field.getArea(pos[1], pos[2]);
                temp.setX(pos[3]); temp.setY(pos[4]);
                temp.kill();
                field.addDeadCreature(temp);
                field.setArea(pos[1], pos[2], null);
            }
            try {
                Thread.sleep(rand.nextInt(100));
            }catch(InterruptedException e){

            }
            field.repaint();
        }

        field.setIsRepeating(false);
        close();

    }

}
