public class TestHelper {
    protected void sleep(int i) {
        try {
            Thread.sleep(i);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
