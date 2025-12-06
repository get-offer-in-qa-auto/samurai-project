package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {

    private RandomData() {
    }

    public static String getProjectName() {
        return "Project" + RandomStringUtils.randomAlphanumeric(8);
    }

    public static String getUserName() {
        return "user" + RandomStringUtils.randomAlphanumeric(8);
    }
}
