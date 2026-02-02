package ru.vyarus.spock.jupiter.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Junit behavior is as a reference for spock emulation. So storing original junit results and compare
 * them with spock results.
 * <p>
 * If a junit result changes - throw error to update and commit an updated file manually (otherwise spock could run
 * with the old file and not detect behavior change).
 *
 * @author Vyacheslav Rusakov
 * @since 01.02.2026
 */
public class JupiterResultFile {

    public static final String DELIMITER = "\n";

    public static void store(List<String> state, Class<?>... tests) {
        String result = String.join(DELIMITER, state);
        File target = getTargetFile(tests);
        List<String> current;
        try {
            if (target.exists()) {
                current = Files.readAllLines(target.toPath(), StandardCharsets.UTF_8);

                if (!current.equals(state)) {
                    // throw error to not hide updates - if spock tests run before then such a change would not
                    // be notified otherwise
                    throw new IllegalStateException("Jupiter result override: \n\tStored: "
                            + current + "\n\tActual: " + state
                            + "\n\tRemove current file, re-run test and commit updated file: " + target);
                } else {
                    // no need to override
                    return;
                }
            }

            Files.write(target.toPath(), result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write jupiter result file: " + target, e);
        }
    }

    // spock test method name could not be the same as junit one, so replacements are used

    public static List<String> load(Class<?> test, String... replacements) {
        return load(new Class<?>[]{test}, replacements);
    }

    public static List<String> load(Class<?>[] tests, String... replacements) {
        File target = getTargetFile(tests);
        if (!target.exists()) {
            throw new IllegalStateException("No result stored for jupiter tests: " + Arrays.toString(tests));
        }
        try {
            List<String> res = Files.readAllLines(target.toPath(), StandardCharsets.UTF_8);
            return applyReplacements(res, replacements);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read jupiter result file: " + target, e);
        }
    }

    private static File getTargetFile(Class<?>... test) {
        if (test.length == 0) {
            throw new IllegalArgumentException("Test class not specified");
        }
        String name = test[0].getPackage().getName();
        for (Class<?> aClass : test) {
            name += "." + aClass.getSimpleName();
        }
        return new File("src/test/resources/ru/vyarus/spock/jupiter/" + name);
    }

    private static List<String> applyReplacements(List<String> res, String... replacements) {
        if (replacements.length == 0) {
            return res;
        }
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be pairs");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < replacements.length; i += 2) {
            map.put(replacements[i], replacements[i + 1]);
        }
        Set<String> used = new HashSet<>(map.size());

        for (int i = 0; i < res.size(); i++) {
            String value = res.get(i);
            if (map.containsKey(value)) {
                res.set(i, map.get(value));
                used.add(value);
            }
        }

        if (used.size() != map.size()) {
            final Set<String> remain = map.keySet();
            remain.removeAll(used);
            System.err.println("The following replacements were not used: \n" + String.join("\n", remain));
        }

        return res;
    }
}
