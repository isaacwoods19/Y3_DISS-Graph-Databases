import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class HomeWindow extends JFrame implements ActionListener {

    JButton simpSearch = new JButton("Simple Search");
    JButton normPok = new JButton("Normal Pokedex");
    JButton cyphPok = new JButton("Cypher Pokedex");
    JButton teamBuild = new JButton("Team Builder");

    HomeWindow(){
        simpSearch.setBounds(100,140,200,40);
        simpSearch.addActionListener(this);
        simpSearch.setFocusable(false);
        simpSearch.setFont(new Font("Ketchum", Font.PLAIN, 14));
        simpSearch.setForeground(Color.WHITE);
        simpSearch.setBackground(Color.RED);

        normPok.setBounds(100,190,200,40);
        normPok.addActionListener(this);
        normPok.setFocusable(false);
        normPok.setFont(new Font("Ketchum", Font.PLAIN, 14));
        normPok.setForeground(Color.WHITE);
        normPok.setBackground(Color.RED);

        cyphPok.setBounds(100,240,200,40);
        cyphPok.addActionListener(this);
        cyphPok.setFocusable(false);
        cyphPok.setFont(new Font("Ketchum", Font.PLAIN, 14));
        cyphPok.setForeground(Color.WHITE);
        cyphPok.setBackground(Color.RED);

        teamBuild.setBounds(100,290,200,40);
        teamBuild.addActionListener(this);
        teamBuild.setFocusable(false);
        teamBuild.setFont(new Font("Ketchum", Font.PLAIN, 14));
        teamBuild.setForeground(Color.WHITE);
        teamBuild.setBackground(Color.RED);

        //fetches the pokemon font i found online at https://www.dafont.com/ketchum.font?psize=l&text=Graph+Pok%E9dex
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src\\main\\resources\\Ketchum.otf")));
        } catch (IOException |FontFormatException e) {
            e.printStackTrace();
        }

        JLabel label = new JLabel();
        label.setText("Graph Pokédex");
        label.setForeground(Color.RED);
        label.setFont(new Font("Ketchum", Font.PLAIN, 40));
        label.setBounds(60,20,400,100);

        this.add(simpSearch);
        this.add(normPok);
        this.add(cyphPok);
        this.add(teamBuild);
        this.add(label);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(420,420);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==simpSearch){
            this.setVisible(false);
            GUI.simplesearchwindow.setVisible(true);
        }

        if(e.getSource()==cyphPok){
            this.setVisible(false);
            GUI.cypherWindow.setVisible(true);
        }

        if(e.getSource()==normPok){
            this.setVisible(false);
            GUI.normalDexWindow.setVisible(true);
        }

        if(e.getSource()==teamBuild){
            this.setVisible(false);
            GUI.teamBuilderWindow.setVisible(true);
        }
    }
}
