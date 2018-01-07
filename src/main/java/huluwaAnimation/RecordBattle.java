package huluwaAnimation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecordBattle {
    FileWriter recordWriter;
    public RecordBattle(String recordPath){
        try{
            File record = new File(recordPath);
            record.createNewFile();
            recordWriter = new FileWriter(record);
        }catch(Exception e){
            System.out.println("Cannot create the record file.");
        }
        try {
            recordWriter.write("File to record the battle between Huluwa and Monsters.\n");
        }catch(IOException e){
            System.out.println("Cannot write mark msg to record file. ");
        }
    }

    public RecordBattle(){
        try{
            File record = new File("record.txt");
            record.createNewFile();
            recordWriter = new FileWriter(record);
        }catch(Exception e){
            System.out.println("Cannot create the record file.");
        }
    }

    public void writeToRecord(String str){
        try{
            recordWriter.write(str);
        }catch(IOException e){
            System.out.println("Cannot write to record.");
        }
    }

    public void writeMoveRecord(int originX, int originY, int newX, int newY){
        String str = Integer.toString(originX)+" "+Integer.toString(originY)
                +" "+Integer.toString(newX)+" "+Integer.toString(newY)+"\n";
        writeToRecord(str);
    }

    public void writeDeadRecord(int originX, int originY, int deadX, int deadY){
        String str = Integer.toString(originX)+" "+Integer.toString(originY)
                +" dead "+Integer.toString(deadX)+" "+Integer.toString(deadY)+"\n";
        writeToRecord(str);
    }

    public void close(){
        try{
            recordWriter.close();
        }catch(IOException e){
            System.out.println("Cannot close record file.");
        }
    }
}
