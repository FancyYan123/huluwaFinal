package huluwaAnimation;

import java.awt.Image;

public abstract class Thing2D {
    //用于表示对象在阵地中的位置，不是具体的像素坐标
    protected int x;
    protected int y;
    protected Image liveImage;
    protected Image deadImage;
    protected boolean isDead = false;
    protected Field field;
    protected String name;

    public Thing2D(Field field, int x, int y){
        this.x = x;
        this.y = y;
        this.field = field;
    }

    public final String getName(){
        return name;
    }

    public final Image getImage(){
        if(!isDead)
            return this.liveImage;
        else
            return this.deadImage;
    }

    public final void setliveImage(Image img) {liveImage = img;}

    public final void setdeadImage(Image img) {deadImage = img;}

    public int x() {return x;}
    public int y() {return y;}
    public void setX(int _x) {this.x = _x;}
    public void setY(int _y) {this.y = _y;}

    public boolean getIsDead() {return isDead;}

    public abstract void kill();
    public abstract void moveToPos(int x, int y);
//    {
//        assert(x>=0&&x<Field.fieldWidth);
//        assert(y>=0&&y<Field.fieldHeight);
//        field.moveToPos(this.x,this.y, x, y);
//    }
}
