import java.io.IOException;

public class GUI{
    static CypherWindow cypherWindow;
    static HomeWindow homeWindow;
    static SimpleSearchWindow simplesearchwindow;
    static NormalDexWindow normalDexWindow;
    static TeamBuilderWindow teamBuilderWindow;
    static Main DBinstance;

    public static void main(String[] args){
        //MyFrame myFrame = new MyFrame();

        homeWindow = new HomeWindow();
        simplesearchwindow = new SimpleSearchWindow();
        simplesearchwindow.setVisible(false);
        cypherWindow = new CypherWindow();
        cypherWindow.setVisible(false);
        normalDexWindow = new NormalDexWindow();
        normalDexWindow.setVisible(false);
        teamBuilderWindow = new TeamBuilderWindow();
        teamBuilderWindow.setVisible(false);


        DBinstance = new Main();
        try{
            DBinstance.createDb();
            simplesearchwindow.searchButton.setEnabled(true);
            simplesearchwindow.TextArea.setText("Database Built.");
            cypherWindow.button3.setEnabled(true);
            cypherWindow.TextArea.setText("Database Built.");
            normalDexWindow.queryButton.setEnabled(true);
            normalDexWindow.TextArea.setText("Database Built.");
            for (int i=0;i<teamBuilderWindow.buttons.length;i++){
                teamBuilderWindow.buttons[i].setEnabled(true);
            }
            teamBuilderWindow.TextArea.setText("Database Built.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}