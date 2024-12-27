package pl.magzik.picture_comparer_fx.base.comparator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A comparator that implements natural order comparison for strings.
 * Natural order sorting ensures that numeric portions of strings
 * are compared based on their numeric value rather than lexicographically.
 * <p>
 * For example, this comparator will sort the following list of strings:
 * <pre>{@code
 *  ["image1", "image2", "image10", "image20"]
 * }</pre>
 * into:
 * <pre>{@code
 *  ["image1", "image2", "image10", "image20"]
 * }</pre>
 * rather than:
 * <pre>{@code
 *  ["image1", "image10", "image2", "image20"]
 * }</pre>
 * This makes it useful for sorting filenames, version numbers,
 * or any mixed alphanumeric strings where numeric values should
 * be treated logically.
 * </p>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * List<String> files = List.of("file1", "file20", "file3");
 * files.sort(new NaturalComparator());
 * System.out.println(files); // Output: [file1, file3, file20]
 * }</pre>
 *
 * This class is thread-safe for concurrent use.
 */
public class NaturalComparator implements Comparator<String> {

    @Override
    public int compare(@NotNull String o1, @NotNull String o2) {
        Pattern pattern = Pattern.compile("(\\d+)|(\\D+)");

        List<String> parts1 = splitIntoParts(o1, pattern);
        List<String> parts2 = splitIntoParts(o2, pattern);

        int minSize = Math.min(parts1.size(), parts2.size());
        for (int i = 0; i < minSize; i++) {
            String s1 = parts1.get(i);
            String s2 = parts2.get(i);

            int cmp;
            if (s1.matches("\\d+") && s2.matches("\\d+"))
                cmp = Long.compare(Long.parseLong(s1), Long.parseLong(s2));
            else
                cmp = s1.compareTo(s2);

            if (cmp != 0) return cmp;
        }
        return Integer.compare(o1.length(), o2.length());
    }

    private @NotNull List<String> splitIntoParts(@NotNull String input, @NotNull Pattern pattern) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            parts.add(matcher.group());
        }
        return parts;
    }

}
