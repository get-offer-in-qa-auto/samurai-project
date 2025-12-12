package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {

    private RandomData() {
    }

    public static String getProjectName() {
        return "Project" + RandomStringUtils.randomAlphanumeric(8);
    }

    public static String getUserName() {
        return "user" + RandomStringUtils.randomAlphanumeric(8);
    }

    public static String getUserPassword() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }

    public static String getTokenName() {
        return "token" + RandomStringUtils.randomAlphanumeric(8);
    }

    public static String getProjectId(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static int getId() {
        return ThreadLocalRandom.current().nextInt(500, 1000);
    }
}
