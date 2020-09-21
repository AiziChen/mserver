import org.junit.Test;

import static org.quanye.sobj.tools.S$.isValidSexp;
import static org.quanye.sobj.tools.S$.removeBoilerplateEmptyCode;

public class SExpTest {

    @Test
    public void removeBoilerplateEmptyCodeTest() {
        // example 1: simple
        String s = "(  a  (  b    c  )  )";
        s = removeBoilerplateEmptyCode(s);
        assert s.equals("(a(b c))");
        // example 2: complex and contains comments
        String s2 = "(sobj  ;; listen port\n" +
                "    (listenPort 7878)\r\n" +
                "    ;; destinations\n" +
                "    (destinations\r\n" +
                "     (list\n" +
                "        (sobj (port 8081) (paths (list \"/\")))\n" +
                "        (sobj (port 8080) (paths (list \"/service/\" \"/serve/\")))))\n" +
                "    ;; others\n" +
                ")\n";
        s2 = removeBoilerplateEmptyCode(s2);
        assert s2.equals("(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))");
    }

    @Test
    public void isValidSexpTest() {
        String s = "(  a  (  b    c  )  )";
        assert isValidSexp(s);
        String s2 = "(obj (a b )";
        assert !isValidSexp(s2);
    }
}
