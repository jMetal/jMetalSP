package org.uma.jmetalsp.application.biobjectivetsp.runner.newyorktraffic;

import org.uma.jmetalsp.application.biobjectivetsp.runner.newyorktraffic.data.GoogleDecode;
import org.uma.jmetalsp.application.biobjectivetsp.runner.newyorktraffic.data.ParsedNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jose Andres
 */
public class ParseLinkSpeedQuery {
    
    public static final int FIELD_ID = 0;
    public static final int FIELD_SPEED = 1;
    public static final int FIELD_TRAVELTIME = 2;
    public static final int FIELD_STATUS = 3;
    public static final int FIELD_DATE = 4;
    public static final int FIELD_POLYLINE = 7;
    public static final int FIELD_NAME = 12;
    public static final double JOIN_DISTANCE = 0.001;
    public static final String NY_LINK_CAMS_URL = "http://207.251.86.229/nyc-links-cams/LinkSpeedQuery.txt";
    private List<ParsedNode> pnodes;
    private Map<Integer, ParsedNode> hashnodes;
    private Map<Integer, Integer> nodeDistances;
    
    /**
     * “NY Traffic client
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        if (args == null || args.length < 2) {
            System.out.println("Provide the path to generate the output files:");
            System.out.println("    ParseLinkSpeedQuery <initial file path> <updates file path>");
            System.out.println("    ParseLinkSpeedQuery -print <solution>");
            return;
        }
        ParseLinkSpeedQuery parser = new ParseLinkSpeedQuery();
        parser.initialize();
        parser.generateOutput();
        
        if (args[0].equalsIgnoreCase("-print")) {
            // Example
            //parser.printCoordinatesForSolution("12 32 30 73 69 10 92 53 56 57 5 27 48 51 46 79 75 15 13 70 21 8 9 83 22 42 20 19 88 1 84 82 29 38 41 36 39 2 11 91 16 14 37 18 17 59 90 60 61 67 40 66 65 24 25 62 64 63 74 89 26 31 28 33 34 7 3 4 87 86 47 23 35 76 6 50 44 43 45 68 72 71 0 85 80 55 52 78 54 77 58 49 81");
            parser.printCoordinatesForSolution(args[1]);
            return;
        }
        
        parser.generateOutputFile(args[0]);
        
        int update = 0;
        
        while (true) {
            Thread.sleep(30000);
            System.out.println("Doing update!");
            int updates = parser.update();
            System.out.println(updates + " nodes updated!");
            parser.generateUpdateFile(args[1], update);
            update++;
        }
    }
    
    public List<ParsedNode> initialize() {
        try {
            createCachedDistances();
            readAndParseData();
            addManualEdges();
            generateGraph();
            int removed;
            do {
                removed = removeIsolatedNodes();
            } while (removed != 0);
            generatePositionGraph();
            return pnodes;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public int update() {
        try {
            URL linkquery = new URL(NY_LINK_CAMS_URL);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(linkquery.openStream()));
            int updates = 0;
            String inputLine = in.readLine(); // Ignore first line (headers)
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.charAt(inputLine.length()-1) != '\"') {
                    inputLine += in.readLine();
                }
                inputLine = inputLine.replace("\"", "");
                String[] fields = inputLine.split("\t");
                
                try {
                    int id = Integer.parseInt(fields[FIELD_ID]);
                    if (hashnodes.containsKey(id)) {
                        ParsedNode node = hashnodes.get(id);
                        int newtime = Integer.parseInt(fields[FIELD_TRAVELTIME]);
                        boolean newstatus = fields[FIELD_STATUS].equals("1");
                        if (node.isStatus() != newstatus) {
                            if (node.isStatus()) {
                                node.setDistance(Integer.MAX_VALUE);
                                node.setTravelTime(Integer.MAX_VALUE);
                            } else {
                                node.setDistance(nodeDistances.get(id));
                                node.setTravelTime(newtime);
                            }
                            node.setDistanceUpdated(true);
                            node.setCostUpdated(true);
                            node.setStatus(newstatus);
                        }
                        
                        if (node.getTravelTime() != newtime) {
                            node.setTravelTime(newtime);
                            node.setCostUpdated(true);
                        }
                        
                        if (node.isCostUpdated() || node.isDistanceUpdated()) {
                            System.out.println("Updated " + node.getId() + ": " + node.getDistance() + "," + newtime);
                            updates++;
                        }
                    }
                }
                catch (Exception ex) {
                    System.err.println("Ignored line " + fields[0] + " cause an error in parsing.");
                }
            }
            in.close();
            return updates;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
    
    private void addManualEdges() {
        int[][] addnodes = new int[][]{ {450, 338},
            {385, 417},
            {298, 126},
            {129, 168}};
        
        for (int[] node : addnodes) {
            hashnodes.get(node[0]).addNode(hashnodes.get(node[1]));
            hashnodes.get(node[1]).addNode(hashnodes.get(node[0]));
        }
    }
    
    private void readAndParseData() throws Exception {
        URL linkquery = new URL(NY_LINK_CAMS_URL);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(linkquery.openStream()));
        pnodes = new ArrayList<>();
        hashnodes = new HashMap<>();
        
        String inputLine = in.readLine(); // Ignore first line (headers)
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.charAt(inputLine.length()-1) != '\"') {
                inputLine += in.readLine();
            }
            inputLine = inputLine.replace("\"", "");
            String[] fields = inputLine.split("\t");
            
            try {
                ParsedNode pnode = new ParsedNode(
                    Integer.parseInt(fields[FIELD_ID]),
                    Double.parseDouble(fields[FIELD_SPEED]),
                    Integer.parseInt(fields[FIELD_TRAVELTIME]),
                    fields[FIELD_STATUS].equals("1"),
                    fields[FIELD_POLYLINE],
                    fields[FIELD_NAME],
                    GoogleDecode.decode(fields[FIELD_POLYLINE]));
                
                // If distances are not cached, call the Google Service
                if (nodeDistances == null) {
                    Integer dist1 = GoogleDecode.getDistance(pnode.getCoords().get(0), pnode.getCoords().get(pnode.getCoords().size()-1));
                    Integer dist2 = GoogleDecode.getDistance(pnode.getCoords().get(pnode.getCoords().size()-1), pnode.getCoords().get(0));
                    pnode.setDistance(min(dist1, dist2));
                } else {
                    if (nodeDistances.containsKey(pnode.getId())) {
                        pnode.setDistance(nodeDistances.get(pnode.getId()));
                    }
                }
                
                pnodes.add(pnode);
                hashnodes.put(pnode.getId(), pnode);
            }
            catch (Exception ex) {
                System.err.println("Ignored line " + fields[0] + " cause an error in parsing.");
            }
        }
        in.close();
    }
    private Integer min(Integer data1,Integer data2){
        Integer result=null;
        if(data1!=null &&data2!=null){
            int aux1=data1.intValue();
            int aux2=data2.intValue();
            if(aux1<=aux2){
                result= data1;
            }else{
                result=data2;
            }
        }
        return result;
    }
    
    private void generateGraph() {
        for (ParsedNode pnode : pnodes) {
            for (ParsedNode p : pnodes) {
                if (!pnode.getId().equals(p.getId())) {
                    
                    double dist1 = pnode.getCoords().get(pnode.getCoords().size()-1).distance(p.getCoords().get(0));
                    double dist2 = pnode.getCoords().get(0).distance(p.getCoords().get(p.getCoords().size()-1));
                    
                    if (dist1 < JOIN_DISTANCE || dist2 < JOIN_DISTANCE) {
                        pnode.addNode(p);
                        p.addNode(pnode);
                    }
                }
            }
        }
    }
    
    private int removeIsolatedNodes() {
        Iterator<ParsedNode> itr = pnodes.iterator();
        int count = 0;
        while (itr.hasNext()) {
            ParsedNode node = itr.next();
            if (node.getNodes().size() < 2) {
                removeEdgesFor(node);
                count++;
                itr.remove();
            }
        }
        return count;
    }
    
    private void removeEdgesFor(ParsedNode node) {
        for (ParsedNode pnode : pnodes) {
            pnode.getNodes().remove(node);
        }
    }
    
    private void generatePositionGraph() {
        int i = 0;
        for (ParsedNode node : pnodes) {
            node.setPosition(i);
            i++;
        }
    }
    
    private void generateOutput() {
        // First line is the total number of cities
        System.out.println(pnodes.size());
        for (ParsedNode node : pnodes) {
            int nodePosition = node.getPosition();
            for (ParsedNode edge : node.getNodes()) {
                int edgePosition = edge.getPosition();
                System.out.println(nodePosition + " " + edgePosition + " " + node.getDistance() + " " + node.getSpeed() + " " + node.getId());
            }
        }
    }
    
    private void generateOutputFile(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // First line is the total number of cities
            writer.write(pnodes.size() + "\n");
            for (ParsedNode node : pnodes) {
                int nodePosition = node.getPosition();
                for (ParsedNode edge : node.getNodes()) {
                    int edgePosition = edge.getPosition();
                    writer.write(nodePosition + " " + edgePosition + " " + node.getDistance() + " " + node.getTravelTime()+ " " + node.getId() + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void generateUpdateFile(String path, int update) {
        path = path.replace("?", String.valueOf(update));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (ParsedNode node : pnodes) {
                if (node.isCostUpdated() || node.isDistanceUpdated()) {
                    int nodePosition = node.getPosition();
                    for (ParsedNode edge : node.getNodes()) {
                        int edgePosition = edge.getPosition();
                        if (node.isCostUpdated()) {
                            writer.write("c " + nodePosition + " " + edgePosition + " " + node.getTravelTime() + "\n");
                            node.setCostUpdated(false);
                        }
                        if (node.isDistanceUpdated()) {
                            writer.write("d " + nodePosition + " " + edgePosition + " " + node.getDistance() + "\n");
                            node.setDistanceUpdated(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void printCoordinatesForSolution(String solution) {
        System.out.println("Coordinates for solution: ");
        System.out.println(solution);
        for (String sol : solution.split(" ")) {
            int id = Integer.parseInt(sol);
            System.out.println(pnodes.get(id).getCoords().get(0).getX() + "," + pnodes.get(id).getCoords().get(0).getY());
        }
    }
    
    // This method has all the distances cached, to avoid making petitions to Google service
    private void createCachedDistances(){
        nodeDistances = new HashMap<>();
        nodeDistances.put(1, 2397);
        nodeDistances.put(2, 1467);
        nodeDistances.put(3, 2958);
        nodeDistances.put(4, 1398);
        nodeDistances.put(106, 974);
        nodeDistances.put(107, 4771);
        nodeDistances.put(108, 2758);
        nodeDistances.put(110, 3390);
        nodeDistances.put(119, 3160);
        nodeDistances.put(122, 2248);
        nodeDistances.put(123, 512);
        nodeDistances.put(124, 3065);
        nodeDistances.put(129, 2077);
        nodeDistances.put(137, 2079);
        nodeDistances.put(145, 1384);
        nodeDistances.put(148, 7716);
        nodeDistances.put(149, 3266);
        nodeDistances.put(153, 3161);
        nodeDistances.put(154, 1891);
        nodeDistances.put(155, 3137);
        nodeDistances.put(157, 6792);
        nodeDistances.put(164, 1877);
        nodeDistances.put(165, 1156);
        nodeDistances.put(167, 3414);
        nodeDistances.put(168, 1877);
        nodeDistances.put(169, 4147);
        nodeDistances.put(170, 2037);
        nodeDistances.put(171, 5384);
        nodeDistances.put(199, 6195);
        nodeDistances.put(204, 5498);
        nodeDistances.put(207, 3430);
        nodeDistances.put(208, 4511);
        nodeDistances.put(215, 3094);
        nodeDistances.put(217, 4505);
        nodeDistances.put(221, 4498);
        nodeDistances.put(222, 3079);
        nodeDistances.put(223, 320);
        nodeDistances.put(224, 1670);
        nodeDistances.put(225, 835);
        nodeDistances.put(257, 5717);
        nodeDistances.put(258, 2114);
        nodeDistances.put(259, 1900);
        nodeDistances.put(261, 2091);
        nodeDistances.put(262, 5761);
        nodeDistances.put(263, 1494);
        nodeDistances.put(264, 1508);
        nodeDistances.put(295, 1841);
        nodeDistances.put(298, 1151);
        nodeDistances.put(311, 2346);
        nodeDistances.put(313, 6984);
        nodeDistances.put(315, 2361);
        nodeDistances.put(316, 6980);
        nodeDistances.put(331, 3279);
        nodeDistances.put(332, 4147);
        nodeDistances.put(349, 27970);
        nodeDistances.put(350, 2967);
        nodeDistances.put(351, 2337);
        nodeDistances.put(364, 2018);
        nodeDistances.put(365, 2154);
        nodeDistances.put(369, 27655);
        nodeDistances.put(375, 2968);
        nodeDistances.put(377, 1695);
        nodeDistances.put(378, 1183);
        nodeDistances.put(381, 744);
        nodeDistances.put(382, 4196);
        nodeDistances.put(384, 2789);
        nodeDistances.put(385, 746);
        nodeDistances.put(388, 1502);
        nodeDistances.put(389, 4076);
        nodeDistances.put(390, 1704);
        nodeDistances.put(402, 2674);
        nodeDistances.put(405, 1645);
        nodeDistances.put(406, 2661);
        nodeDistances.put(410, 1202);
        nodeDistances.put(411, 7612);
        nodeDistances.put(412, 1532);
        nodeDistances.put(413, 1537);
        nodeDistances.put(416, 1193);
        nodeDistances.put(417, 7388);
        nodeDistances.put(422, 3241);
        nodeDistances.put(423, 4060);
        nodeDistances.put(424, 1154);
        nodeDistances.put(425, 4840);
        nodeDistances.put(426, 3397);
        nodeDistances.put(427, 3279);
        nodeDistances.put(428, 4068);
        nodeDistances.put(430, 3588);
        nodeDistances.put(431, 2300);
        nodeDistances.put(432, 1040);
        nodeDistances.put(433, 3820);
        nodeDistances.put(434, 1426);
        nodeDistances.put(435, 2746);
        nodeDistances.put(436, 2288);
        nodeDistances.put(437, 2883);
        nodeDistances.put(439, 1447);
        nodeDistances.put(440, 3935);
        nodeDistances.put(441, 3543);
        nodeDistances.put(442, 2836);
        nodeDistances.put(443, 2672);
        nodeDistances.put(444, 2351);
        nodeDistances.put(445, 5334);
        nodeDistances.put(446, 2442);
        nodeDistances.put(447, 1827);
        nodeDistances.put(448, 10301);
        nodeDistances.put(450, 9815);
        nodeDistances.put(451, 3444);
        nodeDistances.put(453, 1187);
        nodeDistances.put(202, 1427);
        nodeDistances.put(126, 3790);
        nodeDistances.put(338, 6250);
    }
}



