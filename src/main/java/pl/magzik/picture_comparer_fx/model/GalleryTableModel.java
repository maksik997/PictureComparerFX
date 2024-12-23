package pl.magzik.picture_comparer_fx.model;

import javafx.beans.property.*;

import java.io.File;
import java.util.Objects;

/**
 * The {@code GalleryTableModel} class represents a single entry in the gallery table, encapsulating information
 * about a file in the gallery, including its selection state, filename, file size, and last modified date.
 * <p>
 * This model is designed to be used with JavaFX data binding, with {@link BooleanProperty} and {@link StringProperty}
 * allowing automatic updates to the user interface when the properties change.
 * </p>
 */
public class GalleryTableModel {

    private final File file;

    private final BooleanProperty selected;
    private final StringProperty filename;
    private final StringProperty fileSize;
    private final StringProperty modifiedDate;

    /**
     * Constructs a new {@code GalleryTableModel} instance with the specified file information.
     *
     * @param file the {@link File} representing the image file
     * @param filename the name of the file
     * @param fileSize the size of the file (as a human-readable string)
     * @param modifiedDate the last modified date of the file (as a string)
     */
    public GalleryTableModel(File file, String filename, String fileSize, String modifiedDate) {
        this.file = file;
        this.selected = new SimpleBooleanProperty(false);
        this.filename = new SimpleStringProperty(filename);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.modifiedDate = new SimpleStringProperty(modifiedDate);
    }

    /**
     * Returns the {@link File} object associated with this gallery entry.
     *
     * @return the file represented by this model
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the {@link BooleanProperty} representing the selection state of this gallery entry.
     * <p>
     * The selection state can be bound to UI components such as checkboxes or table selection models.
     * </p>
     *
     * @return the BooleanProperty for the selection state
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Returns the {@link StringProperty} representing the filename of the gallery entry.
     * <p>
     * This property can be used to display the filename in a user interface and supports data binding.
     * </p>
     *
     * @return the StringProperty for the filename
     */
    public StringProperty filenameProperty() {
        return filename;
    }

    /**
     * Returns the {@link StringProperty} representing the file size of the gallery entry.
     * <p>
     * This property can be used to display the file size in a user interface and supports data binding.
     * </p>
     *
     * @return the StringProperty for the file size
     */
    public StringProperty fileSizeProperty() {
        return fileSize;
    }

    /**
     * Returns the {@link StringProperty} representing the last modified date of the gallery entry.
     * <p>
     * This property can be used to display the modified date in a user interface and supports data binding.
     * </p>
     *
     * @return the StringProperty for the last modified date
     */
    public StringProperty modifiedDateProperty() {
        return modifiedDate;
    }

    /**
     * Compares this {@code GalleryTableModel} instance to another object for equality.
     * <p>
     * Two {@code GalleryTableModel} instances are considered equal if their {@link File} objects are equal.
     * </p>
     *
     * @param o the object to compare this instance to
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GalleryTableModel that = (GalleryTableModel) o;
        return Objects.equals(file, that.file);
    }

    /**
     * Returns the hash code of this {@code GalleryTableModel}.
     * <p>
     * The hash code is computed based on the {@link File} object associated with this model.
     * </p>
     *
     * @return the hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }
}
