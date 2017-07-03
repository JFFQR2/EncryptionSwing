import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by quite on 05.05.17.
 */
    public class Main extends JFrame {
        public Main(){
            setSize(565,100);
            setTitle("Encipher");
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                add(new MyPanel());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            setVisible(true);
        }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

        private class MyPanel extends JPanel {
            private ArrayList<Integer> key = new ArrayList<>();
            private SecureRandom random = new SecureRandom();
            private JFileChooser chooser;
            private byte[] data = new byte[0];
            private JTextField fieldEnc,fieldDec,fieldEncRange,fieldDevideRange,fieldKey;
            private JButton buttonEnc, buttonChooserEnc,buttonDec,buttonChooserDec,buttonChooserKey;
            private short encRange=128;
            MyPanel() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
                javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                setLayout(null);
            fieldEnc = new JTextField();
            buttonEnc = new JButton("Encrypt");
            buttonChooserEnc = new JButton("Choose file");
            buttonDec = new JButton("Decrypt");
            fieldDec = new JTextField();
            buttonChooserDec = new JButton("Choose File");
            fieldEncRange = new JTextField();
            fieldKey = new JTextField("Path to key");
            buttonChooserKey = new JButton("Choose key");
            buttonChooserKey.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooser(chooser,fieldKey);
                }
            });
            buttonDec.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (fieldDec.getText().isEmpty()){
                        JOptionPane.showMessageDialog(MyPanel.this,"No path to File!","ERROR",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (fieldKey.getText().isEmpty()||fieldKey.getText().equals("Path to key")) {
                        JOptionPane.showMessageDialog(MyPanel.this,"No key","ERROR",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JFrame frame = new JFrame();
                    progress(frame);
                    buttonDec.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            decrypting(fieldDec.getText());
                            buttonDec.setEnabled(true);
                            frame.dispose();
                        }
                    }).start();
                }
            });
            buttonChooserEnc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooser(chooser,fieldEnc);
                }
            });
            buttonEnc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (fieldEnc.getText().isEmpty()){
                        JOptionPane.showMessageDialog(MyPanel.this,"No path to File!","ERROR",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (fieldKey.getText().isEmpty()||fieldKey.getText().equals("Path to key")){
                        JOptionPane.showMessageDialog(MyPanel.this,"Missing path to key!","ERROR",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (fieldEncRange.getText().isEmpty()){
                        encRange=128;
                    } else {
                        encRange=Short.parseShort(fieldEnc.getText());
                    }
                    JFrame frame = new JFrame();
                    progress(frame);
                    buttonEnc.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            encrypting(fieldEnc.getText());
                            buttonEnc.setEnabled(true);
                            frame.dispose();
                        }
                    }).start();
                }
            });
            buttonChooserDec.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooser(chooser,fieldDec);
                }
            });
            fieldEnc.setBounds(10,0,320, 25);
            buttonChooserEnc.setBounds(340,0,100,25);
            buttonEnc.setBounds(450,0,100,25);
            fieldDec.setBounds(10,40,320,25);
            buttonChooserDec.setBounds(340,40,100,25);
            buttonDec.setBounds(450,40,100,25);
            fieldEncRange.setBounds(10,70,180,25);
            fieldKey.setBounds(200,70,230,25);
            buttonChooserKey.setBounds(440,70,100,25);
            add(fieldEnc);
            add(buttonChooserEnc);
            add(buttonEnc);
            add(fieldDec);
            add(buttonChooserDec);
            add(buttonDec);
            add(fieldEncRange);
            add(fieldKey);
            add(buttonChooserKey);
        }

        private void encrypting(String path) {
            try {
                data = Files.readAllBytes(new File(path).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int x;
            for (int i = 0; i < data.length; i++) {
                x = random.nextInt(encRange) + 1 * (int) new Date().getTime();
                while (x==0){
                    x = random.nextInt(encRange) + 1 * (int) new Date().getTime();
                }
                key.add(x);
                data[i] += key.get(i);
            }
            JOptionPane.showMessageDialog(MyPanel.this,"Done!!!","File encoded",JOptionPane.INFORMATION_MESSAGE);

            try (BufferedWriter br = new BufferedWriter(new FileWriter(fieldKey.getText()))) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < key.size(); i++) {
                    builder.append(key.get(i) + " ");
                }
                br.write(builder.toString());

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MyPanel.this,"Failed to write key","Failed :-(",JOptionPane.ERROR_MESSAGE);
            }
            try (FileOutputStream fos = new FileOutputStream(path + " Encrypted")) {
                fos.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MyPanel.this,"Failed to write file","Failed :-(",JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(MyPanel.this,"File written!","Success :-)",JOptionPane.INFORMATION_MESSAGE);
        }

        private void decrypting(String path){
            StringBuilder sb = new StringBuilder();
            String text;
            String[] array;
            try(BufferedReader br = new BufferedReader(new
                    FileReader(fieldKey.getText()))){
                while((text=br.readLine())!=null){
                    sb.append(text);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MyPanel.this,"File not found","Failed :-(",JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MyPanel.this,"File not found","Failed :-(",JOptionPane.ERROR_MESSAGE);
            }
            array=sb.toString().split(" ");
            try {
                data = Files.readAllBytes(new File(path).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i=0;i<data.length;i++){
                data[i]-=Integer.parseInt(array[i]);
            }
            try (FileOutputStream fos = new FileOutputStream(path+" Decrypted")){
                fos.write(data,0,data.length);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MyPanel.this,"Failed to write file","Failed :-(",JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(MyPanel.this,"File write","Success :-)",JOptionPane.INFORMATION_MESSAGE);
        }

        private void chooser(JFileChooser chooser,JTextField field){
            if (chooser==null) {
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
            }
            chooser.setDialogTitle("Choose a file");
            chooser.setApproveButtonText("Choose");

            switch (chooser.showOpenDialog(this)) {
                case JFileChooser.APPROVE_OPTION:
                    field.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void progress(JFrame frame){
            frame.setTitle("Waiting");
            ImageIcon icon = new ImageIcon(this.getClass().getResource("progress.gif"));
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(new Dimension(150,150));
            frame.setLayout(new FlowLayout());
            JLabel jLabel = new JLabel(icon);
            frame.add(jLabel);
            frame.setResizable(false);
            frame.setVisible(true);
        }
    }
}
