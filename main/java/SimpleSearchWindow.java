import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SimpleSearchWindow extends JFrame implements ActionListener {
    private JButton backButton = new JButton("Back");
    JButton searchButton = new JButton("Search");
    JTextArea TextArea = new JTextArea();
    JScrollPane scroll = new JScrollPane (TextArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    //the attributes that i want to be allowed to query - i thought that there's no point letting the user search for pokemon by their base happiness if there are more complex querying sections
    String[] attributes = {"pokedex_number","name","type1","type2","height_m","hp","attack","defense","sp_attack","sp_defense","speed","generation","is_legendary"};
    JComboBox attributeDropDown = new JComboBox(attributes);
    private JTextField textField = new JTextField();

    SimpleSearchWindow(){
        //fetches the pokemon font i found online at https://www.dafont.com/ketchum.font?psize=l&text=Graph+Pok%E9dex
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src\\main\\resources\\Ketchum.otf")));
        } catch (IOException |FontFormatException e) {
            e.printStackTrace();
        }

        backButton.setBounds(20,20,200,40);
        backButton.addActionListener(this);
        backButton.setFocusable(false);
        backButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.RED);

        attributeDropDown.setBounds(20, 140, 200, 40);
        attributeDropDown.addActionListener(this);
        attributeDropDown.setFocusable(false);
        attributeDropDown.setFont(new Font("Ketchum", Font.PLAIN, 14));
        attributeDropDown.setForeground(Color.WHITE);
        attributeDropDown.setBackground(Color.RED);
        attributeDropDown.setSelectedIndex(0);

        searchButton.setBounds(700,140,200,40);
        searchButton.addActionListener(this);
        searchButton.setFocusable(false);
        searchButton.setEnabled(false);
        searchButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(Color.RED);

        textField.setPreferredSize(new Dimension(250,40));
        textField.setBounds(240,140,450,40);
        textField.setFont(new Font("Courier New", Font.BOLD, 14));

        scroll.setBounds(20,200,900,300);

        TextArea.setBounds(20,200,900,300);
        TextArea.setFont(new Font("Ketchum", Font.PLAIN, 16));
        TextArea.setFocusable(false);
        TextArea.setText("Building Database...");

        JLabel label = new JLabel();
        label.setText("Graph Pokédex");
        label.setForeground(Color.RED);
        label.setFont(new Font("Ketchum", Font.PLAIN, 20));
        label.setBounds(770,0,150,50);

        this.add(attributeDropDown);
        this.add(backButton);
        this.add(searchButton);
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
        if(e.getSource()==backButton){
            this.setVisible(false);

            GUI.homeWindow.setVisible(true);
        }

        if(e.getSource()==attributeDropDown){
            int selectedAtt = attributeDropDown.getSelectedIndex();
            if (selectedAtt>=4 && selectedAtt<=10){
                textField.setText(">");
            }else{
                textField.setText("=");
            }
        }

        //if someone searches
        if(e.getSource()==searchButton){

            //adding quote marks for the string inputs, its not needed for the integer inputs
            String userInput = textField.getText();
            userInput = userInput.substring(0,1) + "\"" + userInput.substring(1) + "\"";

            //default mode for what we're using
            String mode = "multi";

            //grab the attribute they have selected
            int attributeIndex = attributeDropDown.getSelectedIndex();
            //these cases are split so i can format the output more specifically later on
            if (attributeIndex>4 && attributeIndex<12){
                //removing the quote marks for integer attributes
                userInput = textField.getText();
            }

            String queryInput = "MATCH (p:Pokemon) WHERE p." +
                    attributeDropDown.getItemAt(attributeIndex) + userInput + " RETURN p";
            TextArea.setText(GUI.DBinstance.Query(queryInput, mode));


        }
    }
}