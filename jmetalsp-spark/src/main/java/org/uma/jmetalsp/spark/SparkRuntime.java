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

package org.uma.jmetalsp.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.StreamingRuntime;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SparkRuntime implements StreamingRuntime {
  private SparkConf sparkConf ;
  private JavaStreamingContext streamingContext ;
  private int duration ;

  public SparkRuntime(int duration, SparkConf sparkConf) {
    this.sparkConf = sparkConf ;
    this.duration = duration ;
    streamingContext = new JavaStreamingContext(this.sparkConf, Durations.seconds(this.duration)) ;
    //streamingContext.sparkContext().setLogLevel("ALL");
  }

  @Override
  public void startStreamingDataSources(List<StreamingDataSource<?>> streamingDataSourceList) {
    for (StreamingDataSource<?> streamingDataSource : streamingDataSourceList) {
      ((SparkStreamingDataSource)streamingDataSource).setStreamingContext(streamingContext);
      streamingDataSource.run();
    }

    streamingContext.start();
    try {
      streamingContext.awaitTermination();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
