import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TeamBuilderWindow extends JFrame implements ActionListener {
    JButton backButton = new JButton("Back");
    JTextArea TextArea = new JTextArea();
    JScrollPane scroll = new JScrollPane(TextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    JScrollPane[] scrolls = new JScrollPane[6];
    JTextArea[] slots = new JTextArea[6];
    JButton[] buttons = new JButton[12];

    ArrayList<String> pokemonTeam = new ArrayList<String>(Arrays.asList("","","","","",""));

    TeamBuilderWindow(){
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

        scroll.setBounds(20,520,900,300);

        TextArea.setBounds(20,520,900,300);
        TextArea.setFont(new Font("Ketchum", Font.PLAIN, 16));
        TextArea.setFocusable(false);
        TextArea.setText("Building Database...");


        int xCalculator = 0;
        int yCalculator = 0;
        //Team slot formatting
        for (int i=0;i<slots.length;i++){
            xCalculator = ((i * 280) % 840) + 120;
            yCalculator = 100;
            if (i>=3){
                yCalculator = 300;
            }

            slots[i] = new JTextArea();
            slots[i].setFont(new Font("Ketchum", Font.PLAIN, 14));
            slots[i].setFocusable(false);

            scrolls[i] = new JScrollPane(slots[i], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrolls[i].setBounds(xCalculator, yCalculator, 180, 150);

            this.add(scrolls[i]);
        }

        //buttons for the team slots
        for (int i=0;i<buttons.length;i+=2){
            xCalculator = (((i / 2) * 280) % 840) + 30;
            yCalculator = 140;
            if (i>=6){
                yCalculator = 340;
            }

            buttons[i] = new JButton();
            buttons[i].setBounds(xCalculator,yCalculator,80,30);
            buttons[i].setText("Add");
            buttons[i].setFont(new Font("Ketchum", Font.PLAIN, 14));
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBackground(Color.RED);
            buttons[i].addActionListener(this);
            buttons[i].setEnabled(false);
            this.add(buttons[i]);

            buttons[i+1] = new JButton();
            buttons[i+1].setBounds(xCalculator - 2,yCalculator + 40,85,30);
            buttons[i+1].setText("Remove");
            buttons[i+1].setFont(new Font("Ketchum", Font.PLAIN, 14));
            buttons[i+1].setForeground(Color.WHITE);
            buttons[i+1].setBackground(Color.RED);
            buttons[i+1].addActionListener(this);
            this.add(buttons[i+1]);
        }


        JLabel label = new JLabel();
        label.setText("Graph Pokédex");
        label.setForeground(Color.RED);
        label.setFont(new Font("Ketchum", Font.PLAIN, 20));
        label.setBounds(770,0,150,50);

        this.add(scroll);
        this.add(backButton);
        this.add(label);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(960,880);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }

    public String calcVulnerablities(ArrayList<String> pokemonTeam){
        Set<String> vulnerabilities = new HashSet<String>();

        //Finds all weaknesses of the team
        for (int i=0;i<pokemonTeam.size();i++){
            if (! pokemonTeam.get(i).equals("")){
                String query = "MATCH (p:Pokemon)-[r1:IS_TYPE]->(t1:Type)-[r2:WEAK_TO]->(t2:Type) WHERE p.name=\"" + pokemonTeam.get(i) + "\" RETURN t2";
                String result = GUI.DBinstance.Query(query, "single");
                String[] splitResult = result.split("\n \n");

                vulnerabilities.addAll(Arrays.asList(splitResult));
            }
        }

        Set<String> resistances = new HashSet<String>();

        //Finds all types the team is resistant to, and removes any weaknesses if a member is resistant to them
        for (int i=0;i<pokemonTeam.size();i++){
            if (! pokemonTeam.get(i).equals("")){
                String query = "MATCH (p:Pokemon)-[r1:IS_TYPE]->(t1:Type)-[r2:RESISTANT_TO]->(t2:Type) WHERE p.name=\"" + pokemonTeam.get(i) + "\" RETURN t2";
                String result = GUI.DBinstance.Query(query, "single");
                String[] splitResult = result.split("\n \n");

                resistances.addAll(Arrays.asList(splitResult));
            }
        }

        //removes all weaknesses that at least one team member is resistant to
        vulnerabilities.removeAll(resistances);

        return vulnerabilities.toString();
    }

    public String calcWeaknesses(ArrayList<String> pokemonTeam){
        Set<String> weaknesses = new HashSet<String>();

        //Finds all weaknesses of the team
        for (int i=0;i<pokemonTeam.size();i++){
            if (! pokemonTeam.get(i).equals("")){
                String query = "MATCH (p:Pokemon)-[r1:IS_TYPE]->(t1:Type)-[r2:WEAK_AGAINST]->(t2:Type) WHERE p.name=\"" + pokemonTeam.get(i) + "\" RETURN t2";
                String result = GUI.DBinstance.Query(query, "single");
                String[] splitResult = result.split("\n \n");

                weaknesses.addAll(Arrays.asList(splitResult));
            }
        }

        Set<String> strengths = new HashSet<String>();

        //Finds all types the team is resistant to, and removes any weaknesses if a member is resistant to them
        for (int i=0;i<pokemonTeam.size();i++){
            if (! pokemonTeam.get(i).equals("")){
                String query = "MATCH (p:Pokemon)-[r1:IS_TYPE]->(t1:Type)-[r2:STRONG_AGAINST]->(t2:Type) WHERE p.name=\"" + pokemonTeam.get(i) + "\" RETURN t2";
                String result = GUI.DBinstance.Query(query, "single");
                String[] splitResult = result.split("\n \n");

                strengths.addAll(Arrays.asList(splitResult));
            }
        }

        //removes all weaknesses that at least one team member is resistant to
        weaknesses.removeAll(strengths);

        return weaknesses.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==backButton){
            this.setVisible(false);
            GUI.homeWindow.setVisible(true);
        }

        int selectedSlot = -1;
        for (int i=0;i<buttons.length;i++){
            if (e.getSource()==buttons[i]){
                selectedSlot = i/2;
                //Add or Remove
                if (i % 2 == 0){
                    //if the index is even then its the add button
                    String input = JOptionPane.showInputDialog(this, "Please input the name of the Pokemon you wish to add");
                    String query = "MATCH (p:Pokemon) WHERE p.name=\"" + input + "\" RETURN p";

                    String result = GUI.DBinstance.Query(query, "multi");

                    if (! result.contains("No Results found")){
                        //adding the new pokemon's name to the array of the current team
                        pokemonTeam.set(selectedSlot, input);
                    }

                    String[] splitResult = result.split(", ");

                    slots[selectedSlot].setText("");
                    for (int j=0;j<splitResult.length;j++){
                        slots[selectedSlot].append(splitResult[j] + "\n");
                    }

                }else{
                    //index is odd then its the remove button
                    //remove pokemon from the window and team
                    slots[selectedSlot].setText("");
                    pokemonTeam.set(selectedSlot, "");
                }
                break;
            }
        }

        //updating the main text area every time something changes
        TextArea.setText("Your team: " + pokemonTeam.toString());
        TextArea.append("\nTeam is vulnerable to: " + calcVulnerablities(pokemonTeam));
        TextArea.append("\nIf a team member is resistant to a team vulnerability, it gets removed!");
        TextArea.append("\n\nTeam is weak against: " + calcWeaknesses(pokemonTeam));
        TextArea.append("\nIf a team member is strong against a team weakness, it gets removed!");
    }
}
