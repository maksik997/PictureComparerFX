package LocationView;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class LocationView extends JPanel implements FileLoadingListener, PropertyChangeListener {
    private final JFileChooser fileChooser;
    private final JTextArea outputLog;
    JButton checkDuplicatesButton, loadFilesButton;
    private final List<ProcessingListener> listeners = new ArrayList<ProcessingListener>();
    private String path;

    private int workersHandled;

    private final SwingWorker<Void, Void> fileLoadingWorker, lookForDuplicatesWorker, moveFilesWorker;

    public LocationView(SwingWorker<Void, Void> fileLoadingWorker, SwingWorker<Void, Void> lookForDuplicatesWorker, SwingWorker<Void, Void> moveFilesWorker) {
        workersHandled = 0;
        this.fileLoadingWorker = fileLoadingWorker;
        this.lookForDuplicatesWorker = lookForDuplicatesWorker;
        this.moveFilesWorker = moveFilesWorker;
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Thousand Pictures Redundancy"),
            pathLabel = new JLabel("Path: ");

        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

        // Central Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        // PathBox Panel
        JPanel pathBox = new JPanel();
        pathBox.setLayout(new FlowLayout());
        pathBox.add(pathLabel);

        JTextField path = new JTextField(64);
        path.setEditable(false);
        pathBox.add(path);

        JButton pathButton = new JButton("Open a directory");
        pathBox.add(pathButton);

        // Progress Panel
        JPanel progressPanel = new JPanel();

        this.outputLog = new JTextArea();
        this.outputLog.setEditable(false);
        this.outputLog.setColumns(64);
        this.outputLog.setRows(13);
        progressPanel.add(outputLog);

        centerPanel.add(pathBox);
        centerPanel.add(progressPanel);

        this.add(centerPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,2));

        loadFilesButton = new JButton("Load files");
        loadFilesButton.setEnabled(false);
        bottomPanel.add(loadFilesButton);

        checkDuplicatesButton = new JButton("Check duplicates & move files");
        checkDuplicatesButton.setEnabled(false);
        bottomPanel.add(checkDuplicatesButton);

        // Action listeners for buttons

        pathButton.addActionListener(e->{
            int file = fileChooser.showOpenDialog(LocationView.this);
            if (file == JFileChooser.APPROVE_OPTION) {
                path.setText(fileChooser.getSelectedFile().getAbsolutePath());
                this.path = path.getText();
                loadFilesButton.setEnabled(true);
            }
        });
        loadFilesButton.addActionListener(e -> {
            loadFilesButton.setEnabled(false);
            callProcessingListeners();
        });
        checkDuplicatesButton.addActionListener(e->{
            checkDuplicatesButton.setEnabled(false);
            outputLog.append("Checking collection of images for duplicates...\n");
            outputLog.append("It can take awhile...\n");
            lookForDuplicatesWorker.addPropertyChangeListener(this);
            lookForDuplicatesWorker.execute();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void addProcessingListener(ProcessingListener l){
        listeners.add(l);
    }

    public void removeProcessingListener(ProcessingListener l){
        listeners.remove(l);
    }

    private void callProcessingListeners(){
        for (ProcessingListener l : listeners) {
            l.actionPerformed(new ProcessingEvent(this, path));
        }
    }

    @Override
    public void actionPerformed(EventObject e) {
        fileLoadingWorker.addPropertyChangeListener(this);
        fileLoadingWorker.execute();
        outputLog.append("Loading images... \n");
        outputLog.append("It can take awhile...\n");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (fileLoadingWorker.isDone() && workersHandled == 0) {
            outputLog.append("Completed loading images. \n");
            workersHandled++;
            setCursor(Cursor.getDefaultCursor());
            checkDuplicatesButton.setEnabled(true);
        }
        if (lookForDuplicatesWorker.isDone() && workersHandled == 1){
            outputLog.append("Completed checking for duplicates. \n");
            outputLog.append("Files transfer started. \n");
            outputLog.append("It will probably take a brief moment... \n");

            workersHandled++;
            // now third worker will start working to move files
            if(moveFilesWorker.getState() == SwingWorker.StateValue.PENDING) {
                moveFilesWorker.addPropertyChangeListener(this);
                moveFilesWorker.execute();
            }
        }
        if (moveFilesWorker.isDone() && workersHandled == 2){
            outputLog.append("Completed moving files. \n");
            setCursor(Cursor.getDefaultCursor());
            workersHandled++;
        }
    }
}
