package server.gui;

import imageapi.ImageAPI;
import server.SynchSeater;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ServerGUI extends JFrame {

    /**
     * panels  - Each panel represents a student and consists of an image, name and rollNo.
     * images  - This stores all the images.
     * rollNos - This stores the roll numbers.
     * names   - This stores the names.
     * ip      - This is the image api that is to be used.
     */
    private JPanel[] panels;
    private JLabel[] names;
    private JLabel[] rollNos;
    private JLabel[] images;
    private ImageAPI ip;
    private SynchSeater synchSeater;

    public ServerGUI(int size, SynchSeater seater) {
        super("Server");
        Font font = new Font("Georgia", Font.ITALIC | Font.BOLD, 17);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        this.synchSeater = seater;
        Border b = new EmptyBorder(10, 2, 10, 2);
        Border panelBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        Border labelBorder = new EmptyBorder(10, 0, 10, 0);
        panels = new JPanel[size];
        names = new JLabel[size];
        JLabel[] seats = new JLabel[size];
        rollNos = new JLabel[size];
        images = new JLabel[size];
        ip = new ImageAPI("images");

        for (int i = 0; i < size; i++) {
            panels[i] = new JPanel();
            names[i] = new JLabel("N/A");
            seats[i] = new JLabel(String.valueOf(i));
            rollNos[i] = new JLabel("N/A");
            images[i] = new JLabel();

            images[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            images[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            rollNos[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            rollNos[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            names[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            names[i].setAlignmentY(Component.CENTER_ALIGNMENT);
            seats[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            seats[i].setAlignmentY(Component.CENTER_ALIGNMENT);

//            rollNos[i].setForeground(Color.white);
//            names[i].setForeground(Color.white);
//            seats[i].setForeground(Color.white);

            images[i].setBorder(b);
            rollNos[i].setBorder(labelBorder);
            names[i].setBorder(labelBorder);
            seats[i].setBorder(labelBorder);

            BoxLayout layout = new BoxLayout(panels[i], BoxLayout.PAGE_AXIS);
            panels[i].setLayout(layout);
            images[i].setIcon(new ImageIcon(ImageAPI.defaultImage));

            panels[i].add(images[i]);
            panels[i].add(rollNos[i]);
            panels[i].add(names[i]);
            panels[i].add(seats[i]);
            panels[i].setBorder(panelBorder);
            panels[i].setBackground(Color.white);
        }
    }

    public void createGUI() {
        JPanel rootPanel = new JPanel();
        int numCols = Toolkit.getDefaultToolkit().getScreenSize().width / (ImageAPI.w + 150);
        GridLayout gl = new GridLayout(0, numCols, 50, 50);
        rootPanel.setLayout(gl);
        for (JPanel panel : panels)
            rootPanel.add(panel);

        JPanel subPanel = new JPanel();
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        JButton rawGenerate = new JButton("Raw layout generator");
        JButton threadGenerate = new JButton("Thread layout generator");
        JButton forkJoinGenerate = new JButton("Fork Join layout generator");
        JButton useDefaults = new JButton("Use defaults");
        BoxLayout bl = new BoxLayout(subPanel, BoxLayout.PAGE_AXIS);

        // Use RawLayoutGenerator when "rawGenerate" is clicked.
        rawGenerate.addActionListener((ae) -> SwingUtilities.invokeLater(RawLayoutGenerator::new));
        // Use ThreadLayoutGenerator when "threadGenerate" is clicked.
        threadGenerate.addActionListener((ae) -> SwingUtilities.invokeLater(ThreadLayoutGenerator::new));
        // Use ForkJoinLayoutGenerator when "forkJoinGenerate" is clicked.
        forkJoinGenerate.addActionListener((ae) -> SwingUtilities.invokeLater(ForkJoinLayoutGenerator::new));
        useDefaults.addActionListener((ae) -> synchSeater.fillDefaults());

        buttonsPanel.add(rawGenerate);
        buttonsPanel.add(threadGenerate);
        buttonsPanel.add(forkJoinGenerate);
        buttonsPanel.add(useDefaults);
        subPanel.setBorder(new EmptyBorder(0, 10, 0, 15));
        subPanel.setLayout(bl);
        subPanel.add(buttonsPanel);
        subPanel.add(Box.createRigidArea(new Dimension(5, 30)));
        subPanel.add(rootPanel);

        JScrollPane scrollPane = new JScrollPane(subPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBorder(new EmptyBorder(50, 20, 50, 50));
        getContentPane().add(scrollPane);

        try {
            setIconImage(ImageIO.read(new File("C:/Users/yb/IdeaProjects/PL2/src/icons/app.png")));
        } catch (Exception e) {
            System.out.println("Unable to load icon");
        }
        setSize(700, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * This class generates the layout naively. It does not use threads. It iterates over
     * seat position and retrieves the image and plugs the image, rollNo and name.
     * Prints the time taken to generate the layout at the end.
     */
    private class RawLayoutGenerator {
        RawLayoutGenerator() {
            long startTime = System.currentTimeMillis();
            int size = synchSeater.getSize();
            for (int i = 0; i < size; i++) {
                Student cur = synchSeater.getStudent(i);
                if (cur != null) {
                    byte[] image = ip.getImage(cur.rollNo);
                    images[i].setIcon(new ImageIcon(image));
                    names[i].setText(cur.name);
                    rollNos[i].setText(cur.rollNo);
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Time required using RawLayout is: " + (endTime - startTime) + " ms");
        }
    }

    /**
     * This class generates using threads. A thread pool manages the threads. Each job submitted
     * to this thread pool updates a single seat in the layout.
     */
    private class ThreadLayoutGenerator {

        /**
         * threadPool - This manages the threads.
         * counter    - This maintains the count of the number of seats updated.
         */
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        SynchCounter counter;

        /**
         * The counter is decremented after each seat is updated. Finally, the counter prints the
         * time taken to generate the layout using this class.
         */
        ThreadLayoutGenerator() {
            int size = synchSeater.getSize();
            counter = new SynchCounter(synchSeater.occSeats(), "Thread Layout Generator Counter");
            for (int i = 0; i < size; i++) {
                Student cur = synchSeater.getStudent(i);
                if (cur != null) {
                    int idx = i;
                    threadPool.execute(() -> {
                        updateStudent(idx, cur.rollNo, cur.name, ip.getImage(cur.rollNo));
                        counter.decrement();
                    });
                }
            }
        }

        /**
         * @param i      - The seat number to update.
         * @param rollNo - The roll number of the student.
         * @param name   - The name of the student.
         * @param image  - The image to use.
         */
        void updateStudent(int i, String rollNo, String name, byte[] image) {
            SwingUtilities.invokeLater(() -> {
                images[i].setIcon(new ImageIcon(image));
                rollNos[i].setText(rollNo);
                names[i].setText(name);
            });
        }
    }

    /**
     * This class uses the fork-join framework for generating the layout.
     */
    private class ForkJoinLayoutGenerator {
        /**
         * fp      - The fork join pool. The initial job is submitted to this pool.
         * counter - This maintains track of the number of seats already filled.
         */
        ForkJoinPool fp = new ForkJoinPool(8);
        SynchCounter counter;

        ForkJoinLayoutGenerator() {
            counter = new SynchCounter(synchSeater.occSeats(), "Fork Join Layout counter");
            fp.invoke(new ForkJoinAction(0, synchSeater.getSize()));
        }

        /**
         * This class does the actual layout generation. (l, h) represents the range of seats
         * to fill. If l == h, it fills the seat. Else, it recursively fills the left half and
         * the right half.
         */
        private class ForkJoinAction extends RecursiveAction {
            int l, h;

            ForkJoinAction(int l, int h) {
                this.l = l;
                this.h = h;
            }

            /**
             * The counter is decremented after each seat is updated. Finally, the counter prints the
             * time taken to generate the layout using this class.
             */
            @Override
            protected void compute() {
                if (l > h)
                    return;
                if (l == h) {
                    Student cur = synchSeater.getStudent(l);
                    if (cur != null) {
                        updateStudent(l, cur.rollNo, cur.name, ip.getImage(cur.rollNo));
                        counter.decrement();
                    }
                    return;
                }
                int mid = (l + h) / 2;
                invokeAll(new ForkJoinAction(l, mid), new ForkJoinAction(mid + 1, h));
            }

            void updateStudent(int i, String rollNo, String name, byte[] image) {
                SwingUtilities.invokeLater(() -> {
                    images[i].setIcon(new ImageIcon(image));
                    rollNos[i].setText(rollNo);
                    names[i].setText(name);
                });
            }
        }
    }

    /**
     * This class is a synchronous counter. It prints the time taken for the counter
     * to reach after the counter reaches zero.
     */
    private class SynchCounter {
        private volatile long counter;
        private String counterName;
        private long startTime = System.currentTimeMillis();

        SynchCounter(long n, String counterName) {
            this.counter = n;
            this.counterName = counterName;
        }

        /**
         * Decrement the counter by 1. Print the time taken if the counter reaches.
         */
        synchronized void decrement() {
            this.counter--;
            if (this.counter == 0) {
                System.out.println(counterName + " reached zero.");
                System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }
}
