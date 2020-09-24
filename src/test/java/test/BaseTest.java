package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InValidSObjSyntaxException;
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
    public void toObjectTest() throws InValidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Print the result object
        User result = SObjParser.toObject(u1SObj, User.class);
        System.out.println("u1 = " + result);
    }

    @Test
    public void toObjectPerformanceTest() throws InValidSObjSyntaxException {
        String u1SObj = SObjParser.fromObject(u1);
        // Check parse 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.toObject(u1SObj, User.class);
        }
        long after = System.currentTimeMillis();
        System.out.println("Parse 9999 objects total time: " + (after - before) + "ms");
    }


    @Test
    public void lessVariableTest() throws InValidSObjSyntaxException {
        String sobj1 = "(*obj(id 1)(uid 0)(name \"DavidChen\")(age 25)(birth \"2020-09-24 09:50,07\")(glasses (*obj(price 115.5)(id 1)(degree 203.3)(color \"RED-BLACK\")))(height 167.3)(goods (*list(*obj(name \"火龙果\")(price 2.3)(isVegetable #f))(*obj(name \"雪梨\")(price 3.2)(isVegetable #f))(*obj(name \"西红柿\")(price 2.5)(isVegetable #t))))(behaviors (*list\"Shopping\"\"Running\"\"Football\")))";
        // Converting successful required
        User lessVariableUser = SObjParser.toObject(sobj1, User.class);
        System.out.println(lessVariableUser);
    }
}
