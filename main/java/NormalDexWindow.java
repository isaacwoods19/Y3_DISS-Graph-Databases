import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NormalDexWindow extends JFrame implements ActionListener {
    private JButton backButton = new JButton("Back");
    JButton queryButton = new JButton("Query");
    JTextArea TextArea = new JTextArea();
    JScrollPane scroll = new JScrollPane (TextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    JTextField queryText = new JTextField();
    JLabel step1Label = new JLabel();
    JButton pokeButton = new JButton("Pokemon");
    JButton typeButton = new JButton("Pokemon Types");
    JLabel step2Label = new JLabel();
    String[] attributes = {"pokedex_number","name","type1","type2","height_m","hp","attack","defense","sp_attack","sp_defense","speed","generation","is_legendary"};
    JComboBox attributeDropDown = new JComboBox(attributes);
    JLabel equalsSign = new JLabel("=");
    JTextField attributeInputField = new JTextField();
    JButton nextButton = new JButton("Next");
    JLabel step3Label = new JLabel();
    String[] attributes2 = {"Everything","pokedex_number","name","type1","type2","height_m","hp","attack","defense","sp_attack","sp_defense","speed","generation","is_legendary"};
    JComboBox attributeDropDown2 = new JComboBox(attributes2);
    //TYPES SIDE VARIABLES
    JLabel step2TypesLabel = new JLabel();
    String[] relationships = {"STRONG_AGAINST", "WEAK_AGAINST", "RESISTANT_TO", "WEAK_TO", "NO_EFFECT"};
    JComboBox relationshipsDropDown = new JComboBox(relationships);
    JTextField relationshipsInputField = new JTextField();
    JButton updateButton = new JButton("Update");

    //section markers
    Boolean pokemonSection = false;
    Boolean typesSection = false;

    String querySave = new String();

    NormalDexWindow(){
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

        queryText.setBounds(20,80,900,40);
        queryText.setFont(new Font("Courier New", Font.BOLD, 14));
        queryText.setHorizontalAlignment(JTextField.CENTER);

        //STEP 1 - CHOOSE BETWEEN POKEMON AND TYPES
        step1Label.setBounds(80, 140, 900, 40);
        step1Label.setFont(new Font("Ketchum", Font.PLAIN, 18));
        step1Label.setFocusable(false);
        step1Label.setText("Step 1:             What are you interested in searching for?");

        pokeButton.setBounds(200,200,200, 40);
        pokeButton.addActionListener(this);
        pokeButton.setFocusable(false);
        pokeButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        pokeButton.setForeground(Color.WHITE);
        pokeButton.setBackground(Color.RED);

        typeButton.setBounds(500,200,200, 40);
        typeButton.addActionListener(this);
        typeButton.setFocusable(false);
        typeButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        typeButton.setForeground(Color.WHITE);
        typeButton.setBackground(Color.RED);

        //STEP 2 - POKEMON
        step2Label.setBounds(80, 260, 900, 40);
        step2Label.setFont(new Font("Ketchum", Font.PLAIN, 18));
        step2Label.setFocusable(false);
        step2Label.setText("Step 2:             What do you specifically want to find?");
        step2Label.setVisible(false);

        attributeDropDown.setBounds(150, 320, 200, 40);
        attributeDropDown.addActionListener(this);
        attributeDropDown.setFocusable(false);
        attributeDropDown.setFont(new Font("Ketchum", Font.PLAIN, 14));
        attributeDropDown.setForeground(Color.WHITE);
        attributeDropDown.setBackground(Color.RED);
        attributeDropDown.setVisible(false);

        equalsSign.setBounds(370,320,50,40);
        equalsSign.setFont(new Font("Ketchum", Font.PLAIN, 22));
        equalsSign.setVisible(false);

        attributeInputField.setBounds(400,320,200,40);
        attributeInputField.setFont(new Font("Courier New", Font.BOLD, 14));
        attributeInputField.setVisible(false);

        nextButton.setBounds(650,320,100,40);
        nextButton.addActionListener(this);
        nextButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(Color.RED);
        nextButton.setVisible(false);

        //STEP 3 - POKEMON
        step3Label.setBounds(80, 380, 900, 40);
        step3Label.setFont(new Font("Ketchum", Font.PLAIN, 18));
        step3Label.setFocusable(false);
        step3Label.setText("Step 3:             What do you want to see displayed?");
        step3Label.setVisible(false);

        attributeDropDown2.setBounds(260, 440, 200, 40);
        attributeDropDown2.addActionListener(this);
        attributeDropDown2.setFocusable(false);
        attributeDropDown2.setFont(new Font("Ketchum", Font.PLAIN, 14));
        attributeDropDown2.setForeground(Color.WHITE);
        attributeDropDown2.setBackground(Color.RED);
        attributeDropDown2.setVisible(false);

        queryButton.setBounds(480,440,200,40);
        queryButton.addActionListener(this);
        queryButton.setFocusable(false);
        queryButton.setEnabled(false);
        queryButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        queryButton.setForeground(Color.WHITE);
        queryButton.setBackground(Color.RED);
        queryButton.setVisible(false);


        //STEP 2 - TYPES
        step2TypesLabel.setBounds(80, 260, 900, 40);
        step2TypesLabel.setFont(new Font("Ketchum", Font.PLAIN, 18));
        step2TypesLabel.setFocusable(false);
        step2TypesLabel.setText("Step 2:             What do you want to know about this type?");
        step2TypesLabel.setVisible(false);

        relationshipsInputField.setBounds(150,320,200,40);
        relationshipsInputField.setFont(new Font("Courier New", Font.BOLD, 14));
        relationshipsInputField.setVisible(false);

        relationshipsDropDown.setBounds(400, 320, 200, 40);
        relationshipsDropDown.addActionListener(this);
        relationshipsDropDown.setFocusable(false);
        relationshipsDropDown.setFont(new Font("Ketchum", Font.PLAIN, 14));
        relationshipsDropDown.setForeground(Color.WHITE);
        relationshipsDropDown.setBackground(Color.RED);
        relationshipsDropDown.setVisible(false);

        updateButton.setBounds(650,320,100,40);
        updateButton.addActionListener(this);
        updateButton.setFont(new Font("Ketchum", Font.PLAIN, 14));
        updateButton.setForeground(Color.WHITE);
        updateButton.setBackground(Color.RED);
        updateButton.setVisible(false);


        scroll.setBounds(20,520,900,300);

        TextArea.setBounds(20,520,900,300);
        TextArea.setFont(new Font("Ketchum", Font.PLAIN, 16));
        TextArea.setFocusable(false);
        TextArea.setText("Building Database...");

        JLabel label = new JLabel();
        label.setText("Graph Pokédex");
        label.setForeground(Color.RED);
        label.setFont(new Font("Ketchum", Font.PLAIN, 20));
        label.setBounds(770,0,150,50);

        this.add(backButton);
        this.add(queryButton);
        this.add(step1Label);
        this.add(pokeButton);
        this.add(typeButton);
        this.add(step2Label);
        this.add(attributeDropDown);
        this.add(equalsSign);
        this.add(attributeInputField);
        this.add(nextButton);
        this.add(step3Label);
        this.add(attributeDropDown2);

        this.add(step2TypesLabel);
        this.add(relationshipsDropDown);
        this.add(relationshipsInputField);
        this.add(updateButton);

        this.add(scroll);
        this.add(queryText);
        this.add(label);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(960,880);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==backButton){
            this.setVisible(false);

            GUI.homeWindow.setVisible(true);
        }

        if(e.getSource()==pokeButton){
            pokemonSection = true;
            typesSection = false;
            //sets the query text
            queryText.setText("MATCH (p:Pokemon) ");
            //hides any of the other section that is visible
            step2TypesLabel.setVisible(false);
            relationshipsInputField.setVisible(false);
            relationshipsDropDown.setVisible(false);
            updateButton.setVisible(false);
            step3Label.setVisible(false);
            attributeDropDown2.setVisible(false);
            queryButton.setVisible(false);
            //sets the label to be correct to the section
            step3Label.setText("Step 3:             What do you want to see displayed?");
            //shows the next step
            step2Label.setVisible(true);
            attributeDropDown.setVisible(true);
            equalsSign.setVisible(true);
            attributeInputField.setVisible(true);
            nextButton.setVisible(true);
        }

        if(e.getSource()==nextButton){
            //adding quote marks for the string inputs, its not needed for the integer inputs
            String userInput = attributeInputField.getText();

            int attributeIndex = attributeDropDown.getSelectedIndex();
            //adds quote marks if its not an integer attribute
            if (!(attributeIndex>4 && attributeIndex<=11)){
                userInput = "\"" + userInput + "\"";
            }

            //appends to the query text
            queryText.setText("MATCH (p:Pokemon) WHERE p." + attributeDropDown.getSelectedItem() + "=" + userInput);
            //saves the current query for the next step
            querySave = queryText.getText();


            //shows the next step
            step3Label.setVisible(true);
            attributeDropDown2.setSelectedIndex(0);
            attributeDropDown2.setVisible(true);
            queryButton.setVisible(true);
        }

        if(e.getSource()==attributeDropDown2){
            //gets the selected item
            String selectedAttribute = (String)attributeDropDown2.getSelectedItem();
            if (selectedAttribute.equals("Everything")){
                selectedAttribute = "p";
            }else{
                selectedAttribute = "p." + selectedAttribute;
            }
            //appends to the query
            queryText.setText(querySave + " RETURN " + selectedAttribute);
        }


        //TYPES SECTION
        if(e.getSource()==typeButton){
            pokemonSection = false;
            typesSection = true;
            //sets the query text
            queryText.setText("MATCH (t1:Type)");
            //hides any components from the other section
            step2Label.setVisible(false);
            attributeDropDown.setVisible(false);
            equalsSign.setVisible(false);
            attributeInputField.setVisible(false);
            nextButton.setVisible(false);
            step3Label.setVisible(false);
            attributeDropDown2.setVisible(false);
            queryButton.setVisible(false);
            //shows the next step
            step2TypesLabel.setVisible(true);
            relationshipsDropDown.setVisible(true);
            relationshipsDropDown.setSelectedIndex(0);
            relationshipsInputField.setVisible(true);
            updateButton.setVisible(true);
            //sets a label to be relevant to the section
            step3Label.setText("Step 3:     Make sure the query at the top is up to date, and then click \"Query\"");
        }

        if(e.getSource()==relationshipsDropDown){
            //gets the selected item and updates the query
            queryText.setText("MATCH (t1:Type)-[r:" + relationshipsDropDown.getSelectedItem() + "]->(t2:Type) ");
            //saves the query for adding more below
            querySave = queryText.getText();
        }

        if(e.getSource()==updateButton){
            //appends to  the query box to include the input type
            queryText.setText(querySave + "WHERE t1.Type=\"" + relationshipsInputField.getText() + "\" RETURN t2");
            //shows the query button
            queryButton.setVisible(true);
            step3Label.setVisible(true);
        }



        if(e.getSource()==queryButton){
            String mode = "";

            //if its the pokemon section
            if (pokemonSection) {
                if (attributeDropDown2.getSelectedIndex() == 0) {
                    mode = "multi";
                } else {
                    mode = "single";
                }
            }else if (typesSection){
                //if its the types section
                mode = "single";
            }

            TextArea.setText(GUI.DBinstance.Query(queryText.getText(), mode));
        }
    }
}