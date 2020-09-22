package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.NotValidSObjSyntaxException;
import test.domain.Glasses;
import test.domain.Goods;
import test.domain.User;

import java.util.Date;

public class BaseTest {
    Glasses glasses = new Glasses(1, 203.3, "RED-BLACK");
    Goods[] goodss = {
            new Goods("火龙果", 2.3F, false),
            new Goods("雪梨", 3.2F, false),
            new Goods("西红柿", 2.5F, true)
    };
    String[] behaviors = new String[]{"Shopping", "Running", "Football"};
    User u1 = new User(1, "DavidChen", 25, new Date(), glasses, 167.3, goodss, behaviors);

    @Test
    public void fromObjectTest() {
        // Parse to list
        String u1SObj = SObjParser.fromObject(u1);
        System.out.println(u1SObj);
    }

    @Test
    public void fromObjectPerformanceTest() {
        // Check from 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.fromObject(u1);
        }
        long after = System.currentTimeMillis();
        System.out.println("From 9999 SObj total time: " + (after - before) + "ms");
    }


    @Test
    public void toObjectTest() throws NotValidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Print the result object
        User result = SObjParser.toObject(u1SObj, User.class);
        System.out.println("u1 = " + result);
    }

    @Test
    public void toObjectPerformanceTest() throws NotValidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Check parse 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.toObject(u1SObj, User.class);
        }
        long after = System.currentTimeMillis();
        System.out.println("Parse 9999 objects total time: " + (after - before) + "ms");
    }
}
