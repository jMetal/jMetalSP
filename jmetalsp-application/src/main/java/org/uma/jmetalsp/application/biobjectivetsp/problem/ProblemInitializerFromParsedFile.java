package org.uma.jmetalsp.application.biobjectivetsp.problem;

import org.uma.jmetalsp.problem.ProblemBuilder;

import java.io.IOException;

/**
 * This interface creates an instance of a dynamic TSP problem
 * 
 * @author Jose Andres Cordero
 */
public class ProblemInitializerFromParsedFile implements ProblemBuilder {

  private String inputFileName ;
  private String hdfsIp;
  private int hdfsPort;
  private boolean isHDFS;

  public ProblemInitializerFromParsedFile(String inputFileName) {
    this.inputFileName = inputFileName ;
    isHDFS=false;
  }
  public ProblemInitializerFromParsedFile(String inputFileName, String hdfsIp, int hdfsPort) {
    this.inputFileName = inputFileName ;
    this.hdfsIp=hdfsIp;
    this.hdfsPort=hdfsPort;
    isHDFS=true;
  }
  @Override
  public DynamicMultiobjectiveTSP build() throws IOException {
    MultiobjectiveTSPBuilderParsed tspInitializer = null;
    if(!isHDFS) {
      tspInitializer= new MultiobjectiveTSPBuilderParsed(inputFileName);
    }else{
      tspInitializer= new MultiobjectiveTSPBuilderParsed(hdfsIp,hdfsPort,inputFileName);
    }
    return tspInitializer.initializeProblem() ;
  }
}
