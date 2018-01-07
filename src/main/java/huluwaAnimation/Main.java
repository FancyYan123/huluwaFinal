package huluwaAnimation;

import javax.swing.*;

public class Main extends JFrame{

    public Main() { InitUI();}

    public void InitUI(){
        Field field = new Field();
        add(field);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(field.getFieldWidth()*field.OFFSET,
                field.getFieldHeight()*field.SPACE+field.SPACE);
        setLocationRelativeTo(null);
        setTitle("葫芦娃大战妖怪");
    }

    public static void main(String args[]){
        Main app = new Main();
        app.setVisible(true);
    }
}
