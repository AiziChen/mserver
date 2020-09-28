import org.coqur.mserver.$;
import org.junit.jupiter.api.Test;

public class ToolsTest {
    @Test
    public void hexToStringTest() {
        int[] is = {32352, 52335634, 6, 4734, 5734734, 57634, 10, 20, 50, 115, 114, 113, 112, 0, 1, 10000, 9999};
        $ tool = org.coqur.mserver.$.newInstance();
        for (int k : is) {
            String h1 = Integer.toHexString(k);
            long j = tool.hexToLong(h1);
            assert k == j;
        }
    }
}
