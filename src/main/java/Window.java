import com.google.api.client.util.ArrayMap;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class Window extends JFrame {
    int WIDTH = 900;
    int HEIGHT = WIDTH;
    JLabel currentUser = new JLabel("");
    JLabel currentFile = new JLabel("");
    JPanel mailListContent;
    JPanel mailViewContent;

    public void refreshMails(){
        mailListContent.removeAll();
        for(Map.Entry<String,String> me : App.rows.entrySet()){
            JButton jl = new JButton(me.getKey());
            jl.setOpaque(false);
            jl.setContentAreaFilled(false);
            jl.setBorderPainted(false);
            jl.addActionListener(e -> {
                mailViewContent.removeAll();
                Font font = new Font("LucidaSans", Font.PLAIN, 12);
                JTextArea ta = new JTextArea("T: "+App.subject+"\n---\n\n"+me.getValue());
                ta.setFont(font);
                ta.setEditable(false);
                mailViewContent.add(ta);
                this.invalidate();
                this.revalidate();
                this.repaint();
            });
            mailListContent.add(jl);
        }
        this.invalidate();
        this.revalidate();
        this.repaint();
    }
    public void setCurrentUser(String s){
        if(s.length()>30) {
            s = "..." + s.substring(s.length() - 30);
        }
        currentUser.setText("Jesteś zalogowana/y, jako: '"+s+"'\t\t\t");
    };
    public void setCurrentFile(String s){
        if(s.length()>30) {
            s = "..." + s.substring(s.length() - 30);
        }
        currentFile.setText("Wybrany plik: " + s + "\t\t\t");
    };

    public Window() throws IOException {
        super("MassMailer");

        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH,HEIGHT);
        this.setMinimumSize(new Dimension(WIDTH,HEIGHT));
        this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource("gmailIcon.png")));

        //top account options line
        JButton logout = new JButton("Zmień konto");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    App.refresh();
                } catch (GeneralSecurityException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        currentUser.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        currentFile.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        JPanel jpName = new JPanel();
        jpName.setLayout(new BoxLayout(jpName, BoxLayout.LINE_AXIS));
        jpName.add(currentUser,BorderLayout.CENTER);
        jpName.add(logout,BorderLayout.CENTER);

        //file picker
        JPanel jpFile = new JPanel();
        jpFile.setLayout(new BoxLayout(jpFile, BoxLayout.LINE_AXIS));
        jpFile.add(currentFile,BorderLayout.CENTER);
        JFileChooser filePicker = new JFileChooser();
        JButton filePickerActivator = new JButton("Wybierz Plik");
        filePickerActivator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = filePicker.showOpenDialog(getParent());
                if (response == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = filePicker.getSelectedFile();
                    setCurrentFile(selectedFile.getAbsolutePath());
                    try {
                        App.setCurrentFile(selectedFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        jpFile.add(filePickerActivator, BorderLayout.CENTER);

        //view
        JPanel jpView = new JPanel();
        jpView.setLayout(new BoxLayout(jpView, BoxLayout.LINE_AXIS));
        jpView.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

        JPanel mailListPane = new JPanel();
        JPanel mailViewPane = new JPanel();
        mailListPane.setLayout(new BoxLayout(mailListPane, BoxLayout.Y_AXIS));mailListPane.setPreferredSize(new Dimension(WIDTH/2,HEIGHT*4/5));mailListPane.setMaximumSize(new Dimension(WIDTH/2,HEIGHT*4/5));mailListPane.setMinimumSize(new Dimension(WIDTH/2,HEIGHT*4/5));
        mailViewPane.setLayout(new BoxLayout(mailViewPane, BoxLayout.Y_AXIS));mailViewPane.setPreferredSize(new Dimension(WIDTH/2,HEIGHT*4/5)); mailViewPane.setMaximumSize(new Dimension(WIDTH/2,HEIGHT*4/5));mailViewPane.setMinimumSize(new Dimension(WIDTH/2,HEIGHT*4/5));
        JLabel mails = new JLabel("Lista adresów e-mail:");
        JLabel views = new JLabel("Podgląd wiadomości:");
        mailListContent = new JPanel(); mailListContent.setLayout(new BoxLayout(mailListContent, BoxLayout.Y_AXIS));
        mailViewContent = new JPanel(); mailViewContent.setLayout(new BoxLayout(mailViewContent, BoxLayout.Y_AXIS));
        JScrollPane mailList = new JScrollPane(); mailList.setViewportView(mailListContent);
        JScrollPane mailView = new JScrollPane(); mailView.setViewportView(mailViewContent);
        mailListPane.add(mails);mailListPane.add(mailList);
        mailViewPane.add(views);mailViewPane.add(mailView);
        jpView.add(mailListPane);
        jpView.add(mailViewPane);

        //SendButton
        JButton sendButton = new JButton("Wyślij");
        sendButton.setMinimumSize(new Dimension(300,50));
        sendButton.setSize(new Dimension(300,50));
        sendButton.setPreferredSize(new Dimension(300,50));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(App.rows.size()>0) {
                    try {
                        MessageHandler.sendToAll();
                    } catch (MessagingException | IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JPanel sendPanel = new JPanel();
        sendPanel.add(sendButton);
        //Packing Everything
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        jp.add(jpName, BorderLayout.NORTH);
        jp.add(jpFile, BorderLayout.NORTH);
        jp.add(jpView, BorderLayout.NORTH);
        jp.add(sendPanel,BorderLayout.SOUTH);
        this.add(jp);

        this.pack();
        this.setVisible(true);
    }
}
