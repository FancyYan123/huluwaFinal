package huluwaTest;
import huluwaAnimation.*;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class testField {
    static Field field;
    @BeforeClass
    public static void preparedForTest(){
        field = new Field();
    }

    @Test
    public void testFieldMov() throws Exception {
        field.moveToPos(1,0, 2,0);
        String name1 = field.getArea(2,0).getName();
        assertEquals(name1, "橙娃");

        String name2 = field.getArea(1,0).getName();
        assertEquals(name2, "红娃");

        field.moveToPos(1,0, 1, 3);
        Thing2D temp = field.getArea(1,0);
        assertEquals(temp, null);
        String name3 = field.getArea(1,3).getName();
        assertEquals(name3, "红娃");

    }

    @Test
    public void testFieldKill() throws Exception{
        assertEquals(field.getArea(3, 0).getName(), "黄娃");

        field.killCreature(field.getArea(3,0));
        Thing2D temp = field.getArea(3,0);
        assertEquals(temp.getIsDead(), true);
    }

}
