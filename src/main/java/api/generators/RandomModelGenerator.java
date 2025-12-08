package api.generators;

import api.models.project.Property;
import api.models.project.VcsRoot;
import api.models.project.VcsRootEntry;
import com.github.curiousoddman.rgxgen.RgxGen;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class RandomModelGenerator {

    private static final Random random = new Random();

    public static <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);
                Object value;

                GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
                if (rule != null) {
                    value = generateFromRegex(rule.regex(), field.getType());
                } else {
                    value = generateRandomValue(field);
                }

                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate entity for " + clazz.getName(), e);
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Object generateRandomValue(Field field) {
        Class<?> type = field.getType();

        if (field.getName().equals("vcsRootEntries")
                && List.class.isAssignableFrom(type)
                && field.getGenericType() instanceof ParameterizedType pt
                && pt.getActualTypeArguments()[0] == VcsRootEntry.class) {
            return generateVcsRootEntries();
        }

        if (type.equals(String.class)) {
            return UUID.randomUUID().toString().substring(0, 8);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return random.nextInt(1000);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return random.nextDouble() * 100;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(Date.class)) {
            return new Date(System.currentTimeMillis() - random.nextInt(1_000_000_000));
        } else if (List.class.isAssignableFrom(type)) {
            return generateRandomList(field);
        } else {
            return generate(type);
        }
    }

    private static Object generateFromRegex(String regex, Class<?> type) {
        RgxGen rgxGen = new RgxGen(regex);
        String result = rgxGen.generate();
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(result);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(result);
        } else {
            return result;
        }
    }

    private static List<Object> generateRandomList(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType pt) {
            Type actualType = pt.getActualTypeArguments()[0];
            List<Object> list = new ArrayList<>();
            int size = random.nextInt(3) + 1; // 1–3 элемента

            for (int i = 0; i < size; i++) {
                if (actualType == String.class) {
                    list.add(UUID.randomUUID().toString().substring(0, 5));
                } else if (actualType instanceof Class<?> clazz) {
                    list.add(generate(clazz));
                } else {
                    list.add(null);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }


    private static List<VcsRootEntry> generateVcsRootEntries() {
        VcsRootEntry entry = VcsRootEntry.builder()
                .vcsRoot(
                        new VcsRoot(
                                RandomData.getProjectName(),
                                "jetbrains.git",
                                List.of(
                                        new Property("url", "https://github.com/filangelin/repository-for-teamcity"),
                                        new Property("branch", "refs/heads/main"),
                                        new Property("authMethod", "ANONYMOUS")
                                )
                        )
                )
                .build();

        return List.of(entry);
    }
}