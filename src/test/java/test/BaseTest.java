package test;

import org.junit.Test;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.NotValidSObjSyntaxExcption;
import test.domain.Glasses;
import test.domain.Goods;
import test.domain.User;

import java.util.Date;

public class BaseTest {
    @Test
    public void baseTest() throws NotValidSObjSyntaxExcption {
        Glasses glasses = new Glasses(1, 203.3, "RED-BLACK");
        Goods[] goodss = {
                new Goods("火龙果", 2.3F, false),
                new Goods("雪梨", 3.2F, false),
                new Goods("西红柿子", 2.5F, true)
        };
        String[] behaviors = new String[]{"Shopping", "Running", "Football"};
        User u1 = new User(1, "DavidChen", 23, new Date(), glasses, 167.3, goodss, behaviors);

        // Parse to list
        String u1Lsp = SObjParser.fromObject(u1);
        System.out.println(u1Lsp);

//        // Print the result object
        User result = SObjParser.toObject(u1Lsp, User.class);
        System.out.println("u1 = " + result);

        // Check parse 9999s user object
        long before = System.currentTimeMillis();
        for (int i = 0; i < 9999; ++i) {
            SObjParser.toObject(u1Lsp, User.class);
        }
        long after = System.currentTimeMillis();
        System.out.println("9999 objects total time: " + (after - before) + "ms");
    }
}
