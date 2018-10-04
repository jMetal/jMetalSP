package org.uma.jmetalsp.streamingdatasource;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.uma.jmetalsp.KafkaStreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.tsp.TSPMatrixData;
import org.uma.jmetalsp.problem.tsp.data.GoogleDecode;
import org.uma.jmetalsp.problem.tsp.data.ParsedNode;

import java.io.Serializable;
import java.util.*;

public class SimpleKafkaStreamingTSPDataSource implements
        KafkaStreamingDataSource<ObservedValue<TSPMatrixData>> , Serializable {
    private Observable<ObservedValue<TSPMatrixData>> observable;
    private String topic;
    private StreamsBuilder streamingBuilder;
    private KStream<Integer,String> tsper;
    private Map<Integer,ParsedNode> hashNodes;
    public static final double JOIN_DISTANCE = 0.001;

    public SimpleKafkaStreamingTSPDataSource(Observable<ObservedValue<TSPMatrixData>> observable){
        this.observable = observable;
        hashNodes = new HashMap<>();
    }
    public SimpleKafkaStreamingTSPDataSource(){
        this(new DefaultObservable<>()) ;
    }

    @Override
    public void setStreamingBuilder(StreamsBuilder streamingBuilder) {
        this.streamingBuilder = streamingBuilder;
    }

    @Override
    public void setTopic(String topic) {
        this.topic=topic;

    }

    @Override
    public void run() {
        tsper = streamingBuilder.stream(topic);
        final Map<Integer, Integer> nodeDistances = this.createCachedDistances();
        tsper.foreach((key, value) -> {

            List<ParsedNode> pNodes= new ArrayList<>();
            try{
            final JSONArray parser = new JSONArray(value);
            for (int i = 0; i < parser.length(); i++) {

                    final JSONObject object =  parser.getJSONObject(i);
                    ParsedNode pNode = new ParsedNode(
                            object.getInt("id"),
                            object.getDouble("speed"),
                            object.getInt("travel_time"),
                            (object.getDouble("status")==1.0d),
                            object.getString("encoded_poly_line"),
                            object.getString("link_name"),
                            GoogleDecode.decode(object.getString("encoded_poly_line"))
                    );

                    if(nodeDistances.containsKey(pNode.getId())){
                        pNode.setDistance(nodeDistances.get(pNode.getId()));
                    }
                    pNodes.add(pNode);
                    hashNodes.put(pNode.getId(),pNode);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            generateGraph(pNodes);
            int removed=0;
            do {
                removed = removeIsolatedNodes(pNodes);
            }while (removed != 0);

            generatePositionGraph(pNodes);
            addManualEdges();


            for (ParsedNode node: pNodes) {
                if(hashNodes.get(node.getId())!=null){
                    ParsedNode nodeAux = hashNodes.get(node.getId());
                    if (nodeAux.isStatus()!=node.isStatus()){
                        if(node.isStatus()) {
                            nodeAux.setDistance(Integer.MAX_VALUE);
                            nodeAux.setTravelTime(Integer.MAX_VALUE);
                            node.setDistance(Integer.MAX_VALUE);
                            node.setTravelTime(Integer.MAX_VALUE);
                        }else  if(nodeDistances.containsKey(node.getId())){
                            node.setDistance(nodeDistances.get(node.getId()));
                        }
                        node.setDistanceUpdated(true);
                        node.setCostUpdated(true);
                        nodeAux.setStatus(node.isStatus());
                    }
                    if(node.getTravelTime()!=nodeAux.getTravelTime()){
                        nodeAux.setTravelTime(node.getTravelTime());
                        node.setCostUpdated(true);
                    }
                }
                if(node.isCostUpdated() || node.isDistanceUpdated()){
                    System.out.println("Updated "+ node.getId()+ " : "+ node.getDistance() +" , "+ node.getTravelTime());
                }
                TSPMatrixData matrix = generateMatrix(node);
                if(matrix!=null) {
                    observable.setChanged();
                    observable.notifyObservers(new ObservedValue<>(matrix));
                }
            }

            //observable.setChanged();
            //observable.notifyObservers(new ObservedValue<Integer>(value));
        });

    }

    private void addManualEdges() {
        int[][] addnodes = new int[][]{ {450, 338},
                {385, 417},
                {298, 126},
                {129, 168}};

        for (int[] node : addnodes) {
            if(hashNodes.get(node[0])!=null && hashNodes.get(node[1])!=null) {
                hashNodes.get(node[0]).addNode(hashNodes.get(node[1]));
                hashNodes.get(node[1]).addNode(hashNodes.get(node[0]));
            }
        }
    }
    @Override
    public Observable<ObservedValue<TSPMatrixData>> getObservable() {
        return observable;
    }


    private TSPMatrixData generateMatrix(ParsedNode node){
        TSPMatrixData result = null;
        if(node.isCostUpdated()||node.isDistanceUpdated()) {
            for (ParsedNode edge : node.getNodes()) {
                String type ="COST";
                double value=Double.MAX_VALUE;
                if(node.isDistanceUpdated()){
                    type="VALUE";
                    value = node.getDistance();
                }else{
                    type="COST";
                    value =node.getTravelTime();
                }
                int x= node.getPosition();
                int y= edge.getPosition();
                result = new TSPMatrixData();
                result.put(0,type);
                result.put(1,x);
                result.put(2,y);
                result.put(3,value);
            }
        }
        return result;
    }
    private void generateGraph(List<ParsedNode> pNodes) {
        for (ParsedNode pnode : pNodes) {
            for (ParsedNode p : pNodes) {
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
    private int removeIsolatedNodes(List<ParsedNode> pNodes) {
        Iterator<ParsedNode> itr = pNodes.iterator();
        int count = 0;
        while (itr.hasNext()) {
            ParsedNode node = itr.next();
            if (node.getNodes().size() < 2) {
                removeEdgesFor(node,pNodes);
                count++;
                itr.remove();
            }
        }
        return count;
    }
    private void generatePositionGraph(List<ParsedNode> pNodes) {
        int i = 0;
        for (ParsedNode node : pNodes) {
            node.setPosition(i);
            i++;
        }
    }
    private void removeEdgesFor(ParsedNode node,List<ParsedNode> pNodes) {
        for (ParsedNode pnode : pNodes) {
            pnode.getNodes().remove(node);
        }
    }

    // This method has all the distances cached, to avoid making petitions to Google service
    private Map<Integer, Integer> createCachedDistances(){
        Map<Integer, Integer>  nodeDistances = new HashMap<>();
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
        return nodeDistances;
    }

}
