package pl.magzik.modules.gallery.management;

import pl.magzik.modules.gallery.table.GalleryEntry;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.modules.gallery.table.TablePropertyAccess;
import pl.magzik.modules.base.Module;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class GalleryManagementModule implements Module, GalleryManagement {

    private final GalleryTableModel galleryTableModel;

    public GalleryManagementModule() {
        galleryTableModel = new GalleryTableModel();
    }

    public TablePropertyAccess getGalleryPropertyAccess() {
        return galleryTableModel;
    }

    public GalleryTableModel getGalleryTableModel() {
        return galleryTableModel;
    }

    @Override
    public void addItems(List<File> files) {
        List<GalleryEntry> entries = files.stream()
                                    .map(File::toPath)
                                    .map(GalleryEntry::new)
                                    .toList();

        galleryTableModel.addEntries(entries);
    }

    @Override
    public List<File> removeItems(List<Integer> indexes) {
        return galleryTableModel.removeEntries(indexes).stream()
                                                        .map(GalleryEntry::getPath)
                                                        .map(Path::toFile)
                                                        .toList();
    }

    @Override
    public void removeElements(List<File> files) {
        List<Integer> indexes = files.stream()
                                        .map(File::toPath)
                                        .map(GalleryEntry::new)
                                        .map(galleryTableModel::indexOf)
                                        .toList();
        removeItems(indexes);
    }

    @Override
    public File getFile(int index) {
        return galleryTableModel.getEntry(index).getPath().toFile();
    }

    @Override
    public void addTagTo(int idx, String tagName) {
        galleryTableModel.addTag(idx, tagName);
    }

    @Override
    public void removeTagFrom(int idx, String tagName) {
        galleryTableModel.removeTag(idx, tagName);
    }

    @Override
    public List<String> getItemTags(int index) {
        return galleryTableModel.getEntry(index).getTags().stream().toList();
    }

    @Override
    public List<String> getAllTags() {
        return galleryTableModel.getEntries().stream()
                                            .map(GalleryEntry::getTags)
                                            .flatMap(Collection::stream)
                                            .distinct()
                                            .toList();
    }
}
