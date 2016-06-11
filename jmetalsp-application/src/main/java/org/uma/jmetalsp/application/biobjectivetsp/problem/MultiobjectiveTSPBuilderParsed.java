package org.uma.jmetalsp.application.biobjectivetsp.problem;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalsp.problem.ProblemBuilder;
import org.uma.jmetalsp.util.HDFSUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class for initializing a dynamic multiobjective TSP from parsed data files
 *
 * @author Jose Andres Cordero
 */
public class MultiobjectiveTSPBuilderParsed implements ProblemBuilder {
  
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;
  private String hdfsIp;
  private int port;
  private HDFSUtil hdfsUtil=null;
  private boolean isHDFS;
  private String fileName;
  public MultiobjectiveTSPBuilderParsed(String fileName){
    this.isHDFS=false;
    this.fileName= fileName;
  }
  public MultiobjectiveTSPBuilderParsed(String hdfsIp, int port, String fileName){
    this.hdfsIp = hdfsIp;
    this.port = port;
    this.hdfsUtil= HDFSUtil.getInstance(hdfsIp,port);
    this.isHDFS=true;
    this.fileName=fileName;
  }
  public DynamicMultiobjectiveTSP initializeProblem() throws IOException {
    
    readProblem(fileName) ;

    DynamicMultiobjectiveTSP problem = new DynamicMultiobjectiveTSP(numberOfCities, distanceMatrix, costMatrix);

    return problem ;
  }

  private void readProblem(String file) throws IOException {
    try {
      BufferedReader br = null;
      if(!isHDFS) {
        br = new BufferedReader(new FileReader(file));
      }else{
        br = hdfsUtil.getBufferedReaderHDFS(file);
      }
      numberOfCities = Integer.parseInt(br.readLine());
      
      distanceMatrix = new double [numberOfCities][numberOfCities];
      costMatrix = new double [numberOfCities][numberOfCities];
      
      for (int i = 0; i < numberOfCities; i++) {
        for (int j = 0; j < numberOfCities; j++) {
            distanceMatrix[i][j] = Double.POSITIVE_INFINITY;
            costMatrix[i][j] = Double.POSITIVE_INFINITY;
        }
      } 
      
      for(String line; (line = br.readLine()) != null; ) {
        String[] tokens = line.split(" ");
        int origin = Integer.parseInt(tokens[0]);
        int destin = Integer.parseInt(tokens[1]);
        int distan = Integer.parseInt(tokens[2]);
        double time = Double.parseDouble(tokens[3]);
        int nodeid = Integer.parseInt(tokens[4]);
        distanceMatrix[origin][destin] = distan;
        costMatrix[origin][destin] = time;
      }
    } catch (Exception e) {
      new JMetalException("MultiobjectiveTSPInitializer.readProblem(): error when reading data file " + e);
    }
  }

    @Override
    public DynamicMultiobjectiveTSP build() throws IOException {
        return this.initializeProblem();
    }
}
