package view;

import controller.Controller;
import model.Model;
import util.DefaultInputs;
import view.util.JFilePicker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class GUIView extends JFrame implements ModelObserver {

    private final Controller controller;
    private JLabel lbl;
    private JButton startBtn;

    public GUIView(final Controller controller){
        super("GUIView");
        this.controller = controller;
        this.lbl = new JLabel("<html><br/><html>");
        JPanel mainPanel = this.createInputPanel();

        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Most frequent words");
        this.add(mainPanel, BorderLayout.NORTH);
        this.pack();
    }

    @Override
    public void modelUpdated(Model model) {
        try {
            System.out.println("[View] model updated => updating the view");
            SwingUtilities.invokeLater(() -> {
                AtomicInteger countWords = new AtomicInteger();
                lbl.setText("<html><br/>");
                lbl.setText(lbl.getText() + model.getTotWords() + " words analysed <br/>");
                model.getSortedWordCount().forEach((s, i) -> {
                    if (countWords.get() % 5 == 0){
                        lbl.setText(lbl.getText() + "<br/>");
                    }
                    lbl.setText(lbl.getText() +  s + " (" + i + " times) &nbsp; &nbsp;");
                    countWords.getAndIncrement();
                });
                if (model.isCompleted()) {
                    lbl.setText(lbl.getText() + "<br/><br/>COUNT COMPLETED");
                    startBtn.setEnabled(true);
                    controller.stop();
                }
                if (model.isStopped()) {
                    lbl.setText(lbl.getText() + "<br/><br/>COUNT STOPPED");
                    startBtn.setEnabled(true);
                }
                lbl.setText(lbl.getText() + "</html>");
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private JPanel createInputPanel(){
        JFilePicker directory;
        JFilePicker file;
        JTextField numberText;
        JButton stopBtn;
        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        final JPanel firstColumnPanel = new JPanel();
        firstColumnPanel.setLayout(new BoxLayout(firstColumnPanel, BoxLayout.Y_AXIS));

        /* ELEM 1 */
        String d = DefaultInputs.AbsolutePath + DefaultInputs.DirectoryPath;
        directory = new JFilePicker("Directory: ", "Browse", new File(d));
        directory.getFileChooser().setCurrentDirectory(new File(d));
        directory.getFileChooser().setSelectedFile(new File(d));
        directory.setTextField(d);
        directory.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directory.setMode(JFilePicker.MODE_OPEN);
        firstColumnPanel.add(directory);

        /* ELEM 2 */
        JPanel filePanel = new JPanel();
        filePanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 10));
        filePanel.setLayout(new GridLayout(1, 1, 10, 10));
        String f = DefaultInputs.AbsolutePath + DefaultInputs.IgnoreFilePath;
        file = new JFilePicker("File: ", "Browse", new File(f));
        file.getFileChooser().setCurrentDirectory(new File(f));
        file.getFileChooser().setSelectedFile(new File(f));
        file.setTextField(f);
        file.setMode(JFilePicker.MODE_OPEN);
        filePanel.add(file);
        firstColumnPanel.add(filePanel);

        /* ELEM 3 */
        JPanel thirdLinePanel = new JPanel();
        thirdLinePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        thirdLinePanel.setLayout(new BoxLayout(thirdLinePanel, BoxLayout.X_AXIS));
        numberText = new JTextField(Integer.toString(DefaultInputs.N), 2);
        JLabel numberLabel = new JLabel("Number of words to show:  ");
        thirdLinePanel.add(numberLabel);
        thirdLinePanel.add(numberText);

        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");

        startBtn.addActionListener(e -> {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            System.out.println(file.getSelectedFilePath());
            System.out.println(directory.getSelectedFilePath());
            controller.start(
                    (file.getSelectedFilePath().isEmpty()) ? f : file.getSelectedFilePath(),
                    (directory.getSelectedFilePath().isEmpty()) ? d : directory.getSelectedFilePath(),
                    Integer.parseInt((numberText.getText().isEmpty()) ? Integer.toString(DefaultInputs.N) : numberText.getText()));
        });
        startBtn.setEnabled(true);
        thirdLinePanel.add(startBtn);

        stopBtn.addActionListener(e -> {
            stopBtn.setEnabled(false);
            controller.stop();
        });
        stopBtn.setEnabled(false);
        thirdLinePanel.add(stopBtn);
        firstColumnPanel.add(thirdLinePanel);
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());
        firstColumnPanel.add(new JPanel());

        JPanel secondColumnPanel = new JPanel();
        secondColumnPanel.setLayout(new BoxLayout(secondColumnPanel, BoxLayout.Y_AXIS));
        secondColumnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        secondColumnPanel.setSize(new Dimension(400, 800));

        secondColumnPanel.add(BorderLayout.NORTH, new JLabel("---------------------------- Most Frequent Words ----------------------------"));
        secondColumnPanel.add(lbl);
        secondColumnPanel.add(Box.createVerticalGlue());

        panel.add(firstColumnPanel);
        panel.add(secondColumnPanel);
        return panel;
    }
}
