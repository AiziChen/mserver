import org.junit.Test;
import org.quanye.sobj.tools.S$;

public class CarCdrTest {
    @Test
    public void carTest() {
        String sexp = "(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))";
        String first = S$.car(sexp);
        assert first.equals("sobj");

        sexp = "((name \"David\"))";
        first = S$.car(sexp);
        assert first.equals("(name \"David\")");

        sexp = ("(name \"David Chen\")");
        first = S$.car(sexp);
        assert first.equals("name");

        sexp = ("((name))");
        first = S$.car(sexp);
        assert first.equals("(name)");

        sexp = ("(\"name\")");
        first = S$.car(sexp);
        assert first.equals("\"name\"");

        sexp = ("(\"na me\")");
        first = S$.car(sexp);
        assert first.equals("\"na me\"");

        sexp = ("((\"na me\"))");
        first = S$.car(sexp);
        assert first.equals("(\"na me\")");

        sexp = ("('('name))");
        first = S$.car(sexp);
        assert first.equals("'('name)");

        sexp = ("('('name)(a b))");
        first = S$.car(sexp);
        assert first.equals("'('name)");

        sexp = "(*list\"Shopping\"\"Running\"\"Football\")";
        first = S$.car(sexp);
        assert first.equals("*list");

        sexp = "(*list \"Shopping\" \"Running\" \"Football\")";
        first = S$.car(sexp);
        assert first.equals("*list");
    }

    @Test
    public void cdrTest() {
        String sexp = "(sobj(listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))";
        String left = S$.cdr(sexp);
        assert left.equals("((listenPort 7878)(destinations(list(sobj(port 8081)(paths(list \"/\")))(sobj(port 8080)(paths(list \"/service/\" \"/serve/\"))))))");

        sexp = ("((a b c)(name \"DavidChen\"))");
        left = S$.cdr(sexp);
        assert left.equals("((name \"DavidChen\"))");

        sexp = ("((name \"DavidChen\"))");
        left = S$.cdr(sexp);
        assert left.equals("()");

        sexp = ("(name \"DavidChen\")");
        left = S$.cdr(sexp);
        assert left.equals("(\"DavidChen\")");

    }
}
