package pl.magzik.picture_comparer_fx.base.comparator;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * A comparator for comparing file sizes represented as strings.
 * This comparator correctly handles sizes with units such as "KB", "MB", and "GB",
 * ensuring that the comparison is based on the actual byte values rather than
 * lexicographical ordering.
 *
 * <p>
 * For example, this comparator will correctly sort:
 * <pre>{@code
 * ["1 KB", "1 MB", "500 KB", "2 GB"]
 * }</pre>
 * into:
 * <pre>{@code
 * ["1 KB", "500 KB", "1 MB", "2 GB"]
 * }</pre>
 * </p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * List<String> sizes = List.of("1 KB", "500 KB", "2 GB", "1 MB");
 * sizes.sort(new FileSizeComparator());
 * System.out.println(sizes); // Output: [1 KB, 500 KB, 1 MB, 2 GB]
 * }</pre>
 *
 * <p>
 * The comparator assumes the format is "{size} {unit}", where:
 * <ul>
 * <li><strong>KB</strong> - Kilobytes</li>
 * <li><strong>MB</strong> - Megabytes</li>
 * <li><strong>GB</strong> - Gigabytes</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note: Decimal values can use either commas or periods.
 * Example: "1,5 MB" or "1.5 MB" are both valid.
 * </p>
 */
public class FileSizeComparator implements Comparator<String> {

    @Override
    public int compare(@NotNull String o1, @NotNull String o2) {
        double size1 = convertToKilobytes(o1);
        double size2 = convertToKilobytes(o2);

        return Double.compare(size1, size2);
    }

    private double convertToKilobytes(@NotNull String size) {
        String[] parts = size.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid size format: " + size);
        }

        double value = Double.parseDouble(parts[0].replace(',', '.'));
        String unit = parts[1].toUpperCase();

        return switch (unit) {
            case "KB" -> value;
            case "MB" -> value * 1024;
            case "GB" -> value * 1024 * 1024;
            default -> throw new IllegalArgumentException("Unknown unit: " + unit);
        };
    }
}
