package pingy;

import java.io.File;
import java.text.ParseException;

public class Main extends Thread {
    private final File file;

    public Main(File file) {
        this.file = file;
    }

    public void run() {
        Accessor.getInstance().clearTable();
        Pool.getInstance().clearPools();
        Divider source = new Divider(file.getPath());
        Register engine = new Register(source.getCodeBase());
        try {
            engine.start();
        } catch (InterruptedException e) {
            Pool.getInstance().printToErrorStream("InterruptError: Session terminated!");
            throw new RuntimeException(e);
        }
    }
}