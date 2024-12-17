package pl.magzik._new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.controller.base.PanelController;
import pl.magzik._new.model.GalleryModel;
import pl.magzik._new.model.GalleryTableModel;
import pl.magzik._new.service.GalleryService;


public class GalleryController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(GalleryController.class);

    private final GalleryModel model;

    private final GalleryService service;

    public GalleryController() {
        this.model = getModel().getGalleryModel();
        this.service = new GalleryService(model);
    }

    @FXML
    private TableColumn<GalleryTableModel, Boolean> selectColumn;

    @FXML
    private TableColumn<GalleryTableModel, Image> iconColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> nameColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> sizeColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> dateColumn;

    @FXML
    private TableView<GalleryTableModel> galleryTable;

    public void initialize() {
        galleryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        galleryTable.setSelectionModel(null);
        galleryTable.setEditable(true);

        selectColumn.setCellValueFactory(p -> p.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        addSelectAllCheckbox();

        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconColumn.setCellFactory(c -> new TableCellWithImage());

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));

        galleryTable.setItems(model.getGalleryData());
    }

    private void addSelectAllCheckbox() {
        CheckBox checkBox = new CheckBox();
        selectColumn.setGraphic(checkBox);
        selectColumn.setSortable(false);

        checkBox.setOnAction(e -> {
            boolean isSelected = checkBox.isSelected();
            for (GalleryTableModel item : model.getGalleryData()) { // TODO: CHANGE FOR MODEL
                item.selectedProperty().setValue(isSelected);
            }
        });
    }

    private static class TableCellWithImage extends TableCell<GalleryTableModel, Image> {
        private final ImageView imageView = new ImageView();

        @Override
        protected void updateItem(Image item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                imageView.setImage(item);
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                setGraphic(imageView);
            }
        }
    }
}
