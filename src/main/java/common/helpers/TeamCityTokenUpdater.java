package common.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamCityTokenUpdater {


    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("Super user authentication token:\\s*(\\d+)");

    /**
     * Обновляет admin.password в config.properties из TeamCity лога
     *
     * @param relativeLogPath   относительный путь к teamcity-server.log
     * @param relativePropsPath относительный путь к config.properties
     */
    public static void updateAdminPassword(String relativeLogPath, String relativePropsPath) {
        Path logPath = Paths.get(relativeLogPath).toAbsolutePath().normalize();
        Path propsPath = Paths.get(relativePropsPath).toAbsolutePath().normalize();

        System.out.println("TeamCity log: " + logPath);
        System.out.println("Config properties: " + propsPath);


        String token = extractToken(logPath);


        writeTokenToProperties(propsPath, token);

        System.out.println("Admin password updated successfully.");
    }

    private static String extractToken(Path logPath) {
        try {
            for (String line : Files.readAllLines(logPath)) {
                Matcher matcher = TOKEN_PATTERN.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read TeamCity log: " + logPath, e);
        }
        throw new RuntimeException("Super user token not found in TeamCity log");
    }

    private static void writeTokenToProperties(Path propsPath, String token) {
        Properties properties = new Properties();


        try (FileInputStream in = new FileInputStream(propsPath.toFile())) {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + propsPath, e);
        }


        properties.setProperty("admin.password", token);


        try (FileOutputStream out = new FileOutputStream(propsPath.toFile())) {
            properties.store(out, "Updated admin.password from TeamCity log");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config.properties: " + propsPath, e);
        }
    }
}