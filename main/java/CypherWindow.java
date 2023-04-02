import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CypherWindow extends JFrame implements ActionListener {
    JButton button = new JButton("Back");
    JButton button3 = new JButton("Query");
    JTextArea TextArea = new JTextArea();
    JScrollPane scroll = new JScrollPane (TextArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    JTextField textField = new JTextField();

    CypherWindow(){
        //fetches the pokemon font i found online at https://www.dafont.com/ketchum.font?psize=l&text=Graph+Pok%E9dex
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src\\main\\resources\\Ketchum.otf")));
        } catch (IOException |FontFormatException e) {
            e.printStackTrace();
        }

        button.setBounds(20,20,200,40);
        button.addActionListener(this);
        button.setFocusable(false);
        button.setFont(new Font("Ketchum", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.RED);

        button3.setBounds(20,140,200,40);
        button3.addActionListener(this);
        button3.setFocusable(false);
        button3.setEnabled(false);
        button3.setFont(new Font("Ketchum", Font.PLAIN, 14));
        button3.setForeground(Color.WHITE);
        button3.setBackground(Color.RED);

        scroll.setBounds(20,200,900,300);

        TextArea.setBounds(20,200,900,300);
        TextArea.setFont(new Font("Ketchum", Font.PLAIN, 16));
        TextArea.setFocusable(false);
        TextArea.setText("Building Database...");

        textField.setPreferredSize(new Dimension(250,40));
        textField.setBounds(240,140,650,40);
        textField.setFont(new Font("Courier New", Font.BOLD, 14));

        JLabel label = new JLabel();
        label.setText("Graph Pokédex");
        label.setForeground(Color.RED);
        label.setFont(new Font("Ketchum", Font.PLAIN, 20));
        label.setBounds(770,0,150,50);

        this.add(button);
        this.add(button3);
        this.add(scroll);
        this.add(textField);
        this.add(label);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(960,600);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==button){
            this.setVisible(false);

            GUI.homeWindow.setVisible(true);
        }

        if(e.getSource()==button3){
            try {
                String mode = "";
                //finding what is wanting to be returned to determine the formatting
                String input = textField.getText();

                //finding the 'return' part of the query
                int returnIndex = -1;
                String[] returnOptions = {"return ", "Return ", "RETURN "};
                for (int i = 0; i < returnOptions.length; i++) {
                    if (returnIndex == -1) {
                        returnIndex = input.lastIndexOf(returnOptions[i]);
                    } else {
                        break;
                    }
                }

                int returnedValueIndex = returnIndex + 7;
                String returnedValue = input.substring(returnedValueIndex);

                if (returnedValue.length() == 1) {
                    mode = "multi";
                } else {
                    mode = "single";
                }

                TextArea.setText(GUI.DBinstance.Query(input, mode));
            }catch (Exception ex){
                TextArea.setText("It looks like the query isn't valid. Change it and try again");
            }
        }
    }
}