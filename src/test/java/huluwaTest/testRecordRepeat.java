package huluwaTest;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import huluwaAnimation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testRecordRepeat {

    RepeatBattle repeater;
    RecordBattle recorder;
    Field field;

    @Test
    public void testRecorder() throws Exception{
        recorder = new RecordBattle();
        recorder.writeMoveRecord(1,0, 1, 3);
        recorder.writeDeadRecord(1, 3, 1, 3);
        recorder.close();

        FileReader recordReader;
        BufferedReader br;
        try{
            File record = new File("record.txt");
            recordReader = new FileReader(record);
            br = new BufferedReader(recordReader);
            assertEquals(br.readLine(), "1 0 1 3");
            assertEquals(br.readLine(), "1 3 dead 1 3");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testRepeater() throws Exception{
        field = new Field();
        recorder = new RecordBattle();
        recorder.writeMoveRecord(1,0, 1, 3);
        recorder.writeDeadRecord(1, 3, 1, 3);
        recorder.close();

        field.setIsRepeating(true);
        System.out.println(field.getIsRepeating());
        repeater = new RepeatBattle("record.txt", field);
        Thread t = new Thread(repeater);
        t.start();
        t.join();


        assertEquals(field.getArea(1,0), null);
        assertEquals(field.getArea(1,3), null);

    }
}
