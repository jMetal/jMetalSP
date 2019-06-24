package org.uma.jmetalsp.serialization.algorithmdata;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.util.Utf8;
import org.uma.jmetalsp.ObservedData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmData extends SpecificRecordBase implements SpecificRecord, ObservedData<Map<String, Object>> {
    protected List<List<Double>> objectives;
    protected List<List<Double>> variables;
    protected int numberOfIterations;
    protected String algorithmName;
    protected String problemName;
    protected int numberOfObjectives;
    protected List <Double>referencePoints;
    //private Map<String, Object> map;

    public AlgorithmData(){
        //map = new HashMap<>();
    }

    public List<List<Double>> getVariables() {
        return variables;
    }

    public void setVariables(List<List<Double>> variables) {
        this.variables = variables;
    }

    public List<List<Double>> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<List<Double>> objectives) {
        this.objectives = objectives;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    public void setNumberOfObjectives(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    public List<Double> getReferencePoints() {
        return referencePoints;
    }

    public void setReferencePoints(List<Double> referencePoints) {
        this.referencePoints = referencePoints;
    }

   // public Map<String, Object> getMap() {
  //      return map;
 //   }

 //   public void setMap(Map<String, Object> map) {
  //      this.map = map;
  //  }

    @Override
    public Schema getSchema() {
        File file = new File("src/main/resources/AlgorithmData.avsc");
        Schema schema = null;
        try{
            schema=new Schema.Parser().parse(file);
        }catch (Exception ex){

        }
        return schema;
    }

    @Override
    public Object get(int i) {
        switch (i){
            case 0:return  objectives;
            case 1: return  variables;
            case 2: return referencePoints;
            case 3: return  numberOfIterations;
            case 4: return  algorithmName;
            case 5: return problemName;
            case 6: return numberOfObjectives;
            default:return  objectives;
        }

    }

    @Override
    public void put(int i, Object o) {
        switch (i){
            case 0:objectives=(List<List<Double>>)o;
               // map.put("objectives",objectives);
            break;
            case 1: variables =(List<List<Double>>)o;
             //   map.put("variables",variables);
            break;
            case 2: referencePoints =(List<Double>)o;
              //  map.put("referencePoints",referencePoints);
                break;
            case 3: numberOfIterations=(Integer)o;
             //   map.put("numberOfIterations",numberOfIterations);
            break;
            case 4: algorithmName =((Utf8)o).toString();
              //  map.put("algorithmName",algorithmName);
            break;
            case 5: problemName = ((Utf8)o).toString();
              //  map.put("problemName",problemName);
            break;
            case 6:numberOfObjectives =(Integer)o;
               // map.put("numberOfObjectives",numberOfObjectives);
                break;
        }

    }

    /*Solution solution;

        public Solution getSolution() {
            return solution;
        }

        public void setSolution(Solution solution) {
            this.solution = solution;
        }

        @Override
        public String toString() {
            String res ="";
            for (double d:solution.getObjectives()) {
                res+=" "+d;
            }
            return res;
        }*/
    @Override
    public String toString() {
        String res ="\n Objectieves\n ";
        for (int i=0;i<objectives.size();i++) {
            for (double d : objectives.get(i)) {
                res += " " + d;
            }
            res+="\n";
        }
        res+="\n Variables\n";
        for (int i=0;i<variables.size();i++) {
            for (double d : variables.get(i)) {
                res += " " + d;
            }
            res+="\n";
        }
        res+= "\n Number of iterations\n "+numberOfIterations;
        res+="\n Algorithm Name\n " + algorithmName;
        res+="\n Problem name \n "+ problemName;
        res+="\n Number of objectives \n "+ numberOfObjectives;
        return res;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public ObservedData fromJson(String jsonString) {
        return null;
    }

    @Override
    public Map<String, Object> getData() {

        return null;
    }
}
