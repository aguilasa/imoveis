package realstate.utils;

import java.io.InputStream;

public class ResourceManager {

    private static ResourceManager instance;

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public InputStream getInputStream(String fileName) {
        InputStream input = ResourceManager.class.getResourceAsStream("/resources/" + fileName);
        if (input == null) {
            input = ResourceManager.class.getClassLoader().getResourceAsStream(fileName);
        }

        return input;
    }
}
