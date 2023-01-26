import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Repository {
    Properties properties = new Properties();

    public Repository() throws IOException {
        properties.load(new FileInputStream("src/database.properties"));
    }
}
