//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource;

import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;

import java.util.List;

/**
 * StreamingFileTSP class for get streaming kafka data for TSP problem
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class StreamingDirectoryTSP implements StreamingDataSource<MultiobjectiveTSPUpdateData> {
  private static final int DISTANCE = 0 ;
  private static final int COST = 1 ;

  private DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData> problem ;
  private String dataDirectoryName ;
  private StreamingConfigurationTSP streamingConfigurationTSP;

  public StreamingDirectoryTSP(StreamingConfigurationTSP streamingConfigurationTSP) {
   this.streamingConfigurationTSP=streamingConfigurationTSP;
   this.dataDirectoryName=streamingConfigurationTSP.getDataDirectoryName();
  }

  /**
   * Add problem to update
   * @param problem
   */
  @Override
  public void setProblem(DynamicProblem<?, MultiobjectiveTSPUpdateData> problem) {
    this.problem = (DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData>) problem;
  }

  /**
   * Create a MultiobjectiveTSPUpdateData from each column of a file and add into a Map
   * For each element in the Map, update the problem
   * @param streamingContext
   */
  @Override
  public void start(JavaStreamingContext streamingContext) {
    JavaDStream<String> lines = streamingContext.textFileStream(dataDirectoryName);

    JavaDStream<MultiobjectiveTSPUpdateData> routeUpdates =
        lines.map(s -> {
          String[] split = s.split(" ");
          MultiobjectiveTSPUpdateData data =
              new MultiobjectiveTSPUpdateData(
                  split[0].equals("c") ? COST : DISTANCE,
              Integer.valueOf(split[1]),
              Integer.valueOf(split[2]),
              Integer.valueOf(split[3])) ;

          return data;
        });

    routeUpdates.foreachRDD(mapJavaRDD -> {
      List<MultiobjectiveTSPUpdateData> dataList = mapJavaRDD.collect();
      for (MultiobjectiveTSPUpdateData data : dataList) {
        problem.update(data);
      }
    });
  }
}
