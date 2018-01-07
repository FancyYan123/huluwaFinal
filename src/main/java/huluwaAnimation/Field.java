package huluwaAnimation;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFileChooser;


public class Field extends JPanel{

    public static final int fieldWidth = 15;
    public static final int fieldHeight = 10;

    //每张图片的宽度为100，长度约等于100
    public static final int OFFSET = 100;
    public static final int SPACE = 100;

    Image background;

    //用于记录阵地
    protected Thing2D[][] area;

    //记录阵亡的生物
    protected ArrayList<Thing2D> deadCreature = new ArrayList();
    protected boolean isRepeating = false;

    //用于管理线程：
    protected ArrayList<Huluwa> AllHuluwa = new ArrayList();
    protected int countHuluwa;
    protected ArrayList<Monster> AllMonster = new ArrayList();
    protected int countMonster;
    protected ExecutorService executor = Executors.newCachedThreadPool();
    boolean startAllThreads = false;
    boolean completed = false;

    //用于处理文件io：
    RecordBattle recorder;
    RepeatBattle repeater;

    //用于排列初始阵型：
    private String deploy =
                    "...............\n"+
                    "1..............\n"+
                    "2.......-.....-\n"+
                    "3........#...#.\n"+
                    "4.........-.-..\n"+
                    "5..........#...\n"+
                    "6..............\n"+
                    "7..............\n"+
                    "...............\n"+
                    "...............\n";

    public Field(){
        recorder = new RecordBattle();

        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("back.jpg");
        try {
            background = ImageIO.read(is);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public int getFieldWidth(){return fieldWidth;}
    public int getFieldHeight(){return fieldHeight;}
    public boolean getCompleted() {return completed;}
    public void setIsRepeating(boolean e) {
        isRepeating = e;
    }
    public boolean getIsRepeating() {return isRepeating; }
    public Thing2D getArea(int i, int j){ return area[i][j]; }
    public void setArea(int i, int j, Thing2D obj){
        area[i][j]=obj;
    }
    public void addDeadCreature(Thing2D deadGuy){
        deadCreature.add(deadGuy);
    }

    public final void initWorld(){
        int i=0;
        int j=0;
        isRepeating=false;
        deadCreature.clear();
        AllMonster.clear();
        AllHuluwa.clear();
        area = new Thing2D[fieldHeight][fieldWidth];

        countMonster=0; countHuluwa=0;
        for(int index=0; index<deploy.length(); index++){
            char item = deploy.charAt(index);
            if(item=='\n'){
                i++;
                j=0;
            }
            else if(item>='1'&&item<='7'){
                Huluwa newHuluwa;
                newHuluwa = new Huluwa(this, Character.getNumericValue(item), i, j);
                area[i][j] = newHuluwa;
                AllHuluwa.add(newHuluwa);
                countHuluwa++;
                j++;
            }
            else if(item=='-'){
                Monster newMonster = new Monster(this, "蛇精", i, j);
                area[i][j] = newMonster;
                AllMonster.add(newMonster);
                countMonster++;
                j++;
            }
            else if(item=='#'){
                Monster newMonster = new Monster(this, "蜈蚣精", i, j);
                area[i][j] = newMonster;
                AllMonster.add(newMonster);
                countMonster++;
                j++;
            }
            else if(item == '.'){
                area[i][j] = null;
                j++;
            }

        }
    }

    public void drawBattle(Graphics pen) {
        pen.setColor(new Color(255, 255, 255));
        pen.fillRect(0, 0, this.getFieldWidth() * OFFSET, this.getFieldHeight() * SPACE + SPACE);

        pen.drawImage(background, 0, 0, this);

        //将所有生物投射到背景上：
        for (int i = 0; i < deadCreature.size(); i++) {
            Thing2D item = deadCreature.get(i);
            pen.drawImage(item.getImage(), item.y() * OFFSET, item.x() * SPACE, this);
        }

        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                Thing2D item = area[i][j];
                if (item != null)
                    pen.drawImage(item.getImage(), item.y() * OFFSET, item.x() * SPACE, this);
            }
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        drawBattle(g);
    }
//    @Override
//    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//
//        drawBattle(g);
//    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_SPACE){
                //启动所有线程：
                if(!startAllThreads) {
                    initWorld();
                    for (int i = 0; i < AllHuluwa.size(); i++) {
                        Huluwa toExec = AllHuluwa.get(i);
                        executor.execute(toExec);
                    }
                    for (int i = 0; i < AllMonster.size(); i++) {
                        Monster toExec = AllMonster.get(i);
                        executor.execute(toExec);
                    }
                    startAllThreads=true;
                }
            }
            else if(key == KeyEvent.VK_L){
                //弹出文件框，选择文件加载重放：
                initWorld();

                if(getRepeatFile())
                    executor.execute(repeater);

            }

            repaint();
        }
    }



    public void killCreature(Thing2D creature){
        deadCreature.add(creature);
        if(creature instanceof Huluwa) {
            AllHuluwa.remove(creature);
            countHuluwa--;
        }
        else {
            AllMonster.remove(creature);
            countMonster--;
        }
        creature.kill();

        if(countHuluwa==0 || countMonster==0) {
            completed = true;
            recorder.close();
        }
    }

    public synchronized void moveToPos(int originX, int originY, int newX, int newY){
        if(area[originX][originY]==null)
            return;

        if(area[newX][newY]==null){
            Thing2D temp = area[originX][originY];
            temp.setX(newX);
            temp.setY(newY);
            area[originX][originY]=null;
            area[newX][newY]=temp;

            recorder.writeMoveRecord(originX, originY, newX, newY);
            return;
        }

        //当敌对的生物相遇时，随机选择一方获胜
        //死亡的生物将从area中剔除，但是位置坐标维持在死亡地点，会显示其遗像
        Thing2D creature1 = area[originX][originY];
        Thing2D creature2 = area[newX][newY];
        if((creature1 instanceof Huluwa) && (creature2 instanceof Huluwa)){
            return;
        }
        else if((creature1 instanceof Monster) && (creature2 instanceof Monster)){
            return;
        }
        else{
            Random rand = new Random();
            int i=rand.nextInt(3);
            //主动进攻者胜率更高
            switch(i){
                case 0:
                    recorder.writeDeadRecord(originX, originY, newX, newY);
                    killCreature(creature1);
                    creature1.setX(newX); creature1.setY(newY);
                    area[originX][originY] = null;
                    break;
                case 1:
                case 2:
                    recorder.writeDeadRecord(newX, newY, newX, newY);
                    recorder.writeMoveRecord(originX, originY, newX, newY);
                    killCreature(creature2);
                    creature1.setX(newX); creature1.setY(newY);
                    area[newX][newY]=creature1;
                    area[originX][originY] = null;
                    break;
            }
        }
        //绘制遇到的问题，在本线程里调用repaint不一定会重绘，重绘的步骤写在了其他线程函数里
        //repaint();
    }

    public boolean getRepeatFile(){
        JFileChooser jf = new JFileChooser();
        jf.showOpenDialog(this);//显示打开的文件对话框
        File f =  jf.getSelectedFile();//使用文件类获取选择器选择的文件
        if(f!=null) {
            String path = f.getAbsolutePath();//返回路径名
            System.out.println(path);

            repeater = new RepeatBattle(path, this);
            isRepeating = true;
            //repeater.repeatFromFile(path);
            return true;
        }
        else
            return false;
    }

    //用于获取妖怪和葫芦娃位置期望：
    public int[] getHuluwaLoc(){
        int x = 0, y=0;
        int count = 0;
        for(Huluwa each: AllHuluwa){
            count++;
            x += each.x();
            y += each.y();
        }
        return new int[]{x/count, y/count};
    }
    public int[] getMonsterLoc(){
        int x = 0, y=0;
        int count = 0;
        for(Monster each: AllMonster){
            count++;
            x += each.x();
            y += each.y();
        }
        return new int[]{x/count, y/count};
    }
}

