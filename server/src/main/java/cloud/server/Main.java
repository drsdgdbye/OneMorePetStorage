package cloud.server;

public class Main {
    public static void main(String[] args) {
        try {
            new Server().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
