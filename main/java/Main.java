import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
import static org.neo4j.graphdb.Label.label;

public class Main{

    private static final File databaseDirectory = new File("target/neo4j-test-db");
    GraphDatabaseService graphDb;
    private DatabaseManagementService managementService;

    private enum RelTypes implements RelationshipType{
        IS_TYPE,
        STRONG_AGAINST,
        WEAK_AGAINST,
        RESISTANT_TO,
        WEAK_TO,
        NO_EFFECT,
        EVOLVES_INTO
    }

    public static void main(String[] args) throws IOException{
        Main testDB = new Main();
        testDB.createDb();
        //testDB.shutDown();
    }


    void createDb() throws IOException{
        FileUtils.deleteDirectory(databaseDirectory.toPath());

        managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
                .setConfig(BoltConnector.enabled, true)
                .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687))
                .build();

        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);

        //importing the CSV file and creating a node for each pokemon
        try{
            File CSV = new File("src\\main\\resources\\pokemon.csv");
            Scanner myReader = new Scanner(CSV);

            String[] headers = new String[19];
            while (myReader.hasNextLine()){
                String lineRaw = myReader.nextLine();
                String[] line = lineRaw.split(",");
                //writes the first line to an array to use the headers later
                if (line[1].equals("name")) {
                    headers = line;
                } else{
                    try (Transaction tx = graphDb.beginTx()){
                        Node node = tx.createNode(label("Pokemon"));
                        for (int i=0;i<19;i++){
                            node.setProperty(headers[i], line[i]);
                        }
                        //setting all integer and float values to not be string
                        tx.execute("match (p:Pokemon) set p.hp=toInteger(p.hp), p.attack=toInteger(p.attack), p.defense=toInteger(p.defense), p.sp_attack=toInteger(p.sp_attack), p.sp_defense=toInteger(p.sp_defense), p.speed=toInteger(p.speed), p.generation=toInteger(p.generation)");

                        tx.commit();
                    }


                }
            }
            myReader.close();

            System.out.println("DB built successfully");

            createTypeRelations();

            System.out.println("Type Relationships built");

            createEvolutionRelations();

            System.out.println("Family Relationships built");

        }catch (FileNotFoundException e){
            System.out.println("Error");
            e.printStackTrace();
        }

    }


    void createTypeRelations(){
        try (Transaction tx = graphDb.beginTx()){
            File type_chart = new File("src\\main\\resources\\type_chart.csv");
            Scanner myReader = new Scanner(type_chart);

            int lineCounter = 0;

            String[] types;

            ArrayList<Node> type_nodes = new ArrayList<Node>();

            while (myReader.hasNextLine()) {
                lineCounter ++;

                String lineRaw = myReader.nextLine();
                String[] line = lineRaw.split(",");

                if (line[0].equals("Attacking")) {
                    types = line;
                    for (int i=1;i<types.length;i++){
                        types[i] = types[i].toLowerCase();

                        Node node = tx.createNode(label("Type"));
                        node.setProperty("Type", types[i]);

                        type_nodes.add(node);
                    }

                }

                else{
                    Node node1 = type_nodes.get(lineCounter - 2);

                    for (int i = 1; i < line.length; i++) {
                        Node node2 = type_nodes.get(i-1);

                        if (line[i].equals("0")){
                            Relationship no_effect_relation = node1.createRelationshipTo(node2, RelTypes.NO_EFFECT);
                        }else if(line[i].equals("2")){
                            Relationship attack_defense1 = node1.createRelationshipTo(node2, RelTypes.STRONG_AGAINST);
                            Relationship defense_attack1 = node2.createRelationshipTo(node1, RelTypes.WEAK_TO);
                        }else if(line[i].equals("0.5")){
                            Relationship attack_defense2 = node1.createRelationshipTo(node2, RelTypes.WEAK_AGAINST);
                            Relationship defense_attack2 = node2.createRelationshipTo(node1, RelTypes.RESISTANT_TO);
                        }
                    }
                }
            }
            myReader.close();

            tx.execute("MATCH (a:Pokemon), (b:Type) WHERE a.type1 = b.Type CREATE (a)-[r:IS_TYPE]->(b)");
            tx.execute("MATCH (a:Pokemon), (b:Type) WHERE a.type2 = b.Type CREATE (a)-[r:IS_TYPE]->(b)");

            tx.commit();


        }catch (FileNotFoundException e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }


    void createEvolutionRelations(){
        try (Transaction tx = graphDb.beginTx()) {
            File evolutions_source = new File("src\\main\\resources\\evolutions_source.txt");
            Scanner myReader = new Scanner(evolutions_source);

            while (myReader.hasNextLine()) {
                String lineRaw = myReader.nextLine();
                String[] line = lineRaw.split("\\|");

                //we can derive the number of pokemon in the family by rearranging this formula:
                //length = 2+(x*3) where x is the number of pokemon in the family
                //therefore we get x=(length-2)/3
                int sizeOfFamily = (line.length-2)/3;

                if (sizeOfFamily > 1) {
                    //if there is more than one pokemon in a family, then the evolution needs to be represented
                    //by a relationship

                    try {
                        ArrayList<Integer> pokedex_nums = new ArrayList<Integer>();
                        for (int i = 3; i < line.length; i += 3) {
                            pokedex_nums.add(Integer.parseInt(line[i]));
                        }

                        switch (line[0]) {
                            case "lop/evo":
                                //all are size 2 and 3
                                if (sizeOfFamily == 2) {
                                    //create relationship for 2 family members
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(1) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[5] + "\"}]->(b)");

                                } else if (sizeOfFamily == 3) {
                                    //create relationship for 3 members
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon), (c:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(1) + "\" and c.pokedex_number = \"" + pokedex_nums.get(2) + "\" CREATE (a)-[r1:EVOLVES_INTO {REQUIREMENT: \"" + line[5] + "\"}]->(b)-[r2:EVOLVES_INTO {REQUIREMENT: \"" + line[8] + "\"}]->(c)");

                                }
                                break;

                            case "lop/evo-branch-1":
                                //case just for wurmple family, which is a unique case
                                if (line[1].equals("Wurmple")) {

                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon), (c:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(1) + "\" and c.pokedex_number = \"" + pokedex_nums.get(3) + "\" CREATE (a)-[r1:EVOLVES_INTO {REQUIREMENT: \"" + line[5] + "\"}]->(b)-[r2:EVOLVES_INTO {REQUIREMENT: \"" + line[11] + "\"}]->(c)");
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon), (c:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(2) + "\" and c.pokedex_number = \"" + pokedex_nums.get(4) + "\" CREATE (a)-[r1:EVOLVES_INTO {REQUIREMENT: \"" + line[8] + "\"}]->(b)-[r2:EVOLVES_INTO {REQUIREMENT: \"" + line[14] + "\"}]->(c)");

                                } else {
                                    //all others are size 3
                                    //make relationship between 1->2 and 1->3
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(1) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[5] + "\"}]->(b)");
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(2) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[8] + "\"}]->(b)");

                                }

                                break;

                            case "lop/evo-branch-2":
                                //all sizes are 4
                                //relationship for 1->2
                                tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(1) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[5] + "\"}]->(b)");

                                //relationships for 2->3 and 2->4
                                tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(1) + "\" and b.pokedex_number = \"" + pokedex_nums.get(2) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[8] + "\"}]->(b)");
                                tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(1) + "\" and b.pokedex_number = \"" + pokedex_nums.get(3) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[11] + "\"}]->(b)");

                                break;

                            case "lop/evo-branch-multi":
                                //System.out.println("branch multi");
                                //add code for processing branch-multi families
                                //only three families, one a size 4, one a size 9, one a size 5
                                for (int j = 1; j < pokedex_nums.size(); j++) {
                                    tx.execute("MATCH (a:Pokemon), (b:Pokemon) WHERE a.pokedex_number = \"" + pokedex_nums.get(0) + "\" and b.pokedex_number = \"" + pokedex_nums.get(j) + "\" CREATE (a)-[r:EVOLVES_INTO {REQUIREMENT: \"" + line[((j + 1) * 3) - 1] + "\"}]->(b)");
                                }

                                break;
                        }
                    }catch (Exception e){}
                }
            }

            tx.commit();
        }catch (FileNotFoundException e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }


    String Query(String input, String mode){
        try (Transaction tx = graphDb.beginTx()){
            Result result = tx.execute(input);
            String resultAsString = result.resultAsString();
            String processedResult = "";

            //Processing of output to remove useless characters
            ArrayList<String> resultArray = new ArrayList<>(Arrays.asList(resultAsString.split("\n")));
            //removing useless ASCII parts of the array
            resultArray.remove(resultArray.size() - 1);
            resultArray.remove(resultArray.size() - 1);
            resultArray.remove(2);
            resultArray.remove(1);
            resultArray.remove(0);

            //If the query doesnt return anything (searching for non existent data) then it returns this message
            if (resultArray.size() == 0){
                return "No Results found \nMake sure names are capitalised and values are within their ranges";
            }else {
                //Otherwise, continue as normal
                //Multi means that there is multiple attributes being output
                if (mode.equals("multi")) {
                    //cutting out the beginning and ending additional characters for each line
                    for (int i = 0; i < resultArray.size(); i++) {
                        int firstIndex = resultArray.get(i).indexOf("{");
                        int secondIndex = resultArray.get(i).indexOf("}");
                        resultArray.set(i, resultArray.get(i).substring(firstIndex + 1, secondIndex));

                        //reordering each line so that it is in the preferred order
                        String[] sortingArray = resultArray.get(i).split(",");
                        String[] temp = new String[sortingArray.length];
                        //The order of indexes that puts pokedex number and name at the front, and so on
                        int[] idealIndexOrder = {14, 17, 6, 3, 12, 16, 15, 18, 9, 10, 0, 5, 7, 4, 1, 11, 13, 2, 8};

                        //putting the line into order
                        for (int j = 0; j < sortingArray.length; j++) {
                            temp[j] = sortingArray[idealIndexOrder[j]];
                        }

                        //putting the array of the line back into a single string
                        resultArray.set(i, String.join(", ", temp));

                        //putting the master array back into a single string with extra line breaks to be returned
                        processedResult += resultArray.get(i).trim() + "\n \n";

                    }

                } else if (mode.equals("single")) {
                    //single means there should be just one attribute being output
                    try {
                        //STRINGS
                        //If its a string, we will cut all of the line, leaving just whats within the quote marks
                        for (int i = 0; i < resultArray.size(); i++) {
                            int firstIndex = resultArray.get(i).indexOf("\"");
                            int secondIndex = resultArray.get(i).lastIndexOf("\"");
                            resultArray.set(i, resultArray.get(i).substring(firstIndex + 1, secondIndex));

                            //putting the master array back into a single string with extra line breaks to be returned
                            processedResult += resultArray.get(i).trim() + "\n \n";
                        }

                    } catch (Exception e) {
                        //If its not a string, then its an integer. Therefore we will need to find where the digits are
                        //and cut everything else out
                        for (int i = 0; i < resultArray.size(); i++) {
                            char[] temp = resultArray.get(i).toCharArray();
                            int firstIndex = 0;
                            int secondIndex = 0;
                            //finding first integer
                            for (int j = 0; j < temp.length; j++) {
                                if (Character.isDigit(temp[j])) {
                                    firstIndex = j;
                                    break;
                                }
                            }
                            //finding last integer
                            for (int j = temp.length - 1; j > 0; j--) {
                                if (Character.isDigit(temp[j])) {
                                    secondIndex = j;
                                    break;
                                }
                            }

                            resultArray.set(i, resultArray.get(i).substring(firstIndex, secondIndex + 1));

                            //putting the master array back into a single string with extra line breaks to be returned
                            processedResult += resultArray.get(i).trim() + "\n \n";
                        }
                    }
                }
            }

            return processedResult;
        }catch (Exception e){
            //If the query isnt a valid input, this is displayed instead of an error message
            return "It looks like the query isn't valid. Change it and try again";
        }
    }

    void shutDown(){
        System.out.println();
        System.out.println( "Shutting down database ..." );
        managementService.shutdown();
    }

    private static void registerShutdownHook( final DatabaseManagementService managementService )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                managementService.shutdown();
            }
        } );
    }
}