package  org.uma.jmetalsp.application.fda.streamingDataSource;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.uma.jmetal.solution.DoubleSolution;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import kafka.serializer.StringDecoder;
import org.uma.jmetalsp.application.fda.problem.FDAUpdateData;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import scala.Tuple2;


/**
 * Created by cris on 20/07/2016.
 */
public class StreamingKafkaFDA implements StreamingDataSource<FDAUpdateData>,Serializable {
  private HashMap<String, String> kafkaParams ;
  private HashSet<String> topicsSet;
  private StreamingConfigurationFDA streamingConfigurationFDA;
  private DynamicProblem<DoubleSolution,FDAUpdateData> problem ;
  public StreamingKafkaFDA(StreamingConfigurationFDA streamingConfigurationFDA ){
    this.streamingConfigurationFDA= streamingConfigurationFDA;
    this.kafkaParams=streamingConfigurationFDA.getKafkaParams();
    this.topicsSet = new HashSet<>(Arrays.asList(streamingConfigurationFDA.getKafkaTopics().split(",")));
    Logger.getGlobal().info("StramingKafka---Constructor ---------------");
  }
  @Override
  public void setProblem(DynamicProblem<?, FDAUpdateData> problem) {
    this.problem= (DynamicProblem<DoubleSolution,FDAUpdateData>)problem;
  }

  @Override
  public void start(JavaStreamingContext context) {
    Logger.getGlobal().info("StramingKafka---Star ---------------");
    JavaPairInputDStream<String, String> messages;
    messages = KafkaUtils.createDirectStream(
            context,
            String.class,
            String.class,
            StringDecoder.class,
            StringDecoder.class,
            kafkaParams,
            topicsSet
    );
    JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
      @Override
      public String call(Tuple2<String, String> tuple2) {
        Logger.getGlobal().info("StramingKafka---messages ---------------tuple2._2()--> "+tuple2._2());
        return tuple2._2();
      }
    });

    JavaDStream<FDAUpdateData> routeUpdates =
            lines.map(new Function<String, FDAUpdateData>() {
              @Override
              public FDAUpdateData call(String s) throws Exception {
                Logger.getGlobal().info("StramingKafka---routeUpdates ---------------time--> "+Integer.valueOf(s));
                FDAUpdateData data = new FDAUpdateData( Integer.valueOf(s));
                return data;
              }
            });

    routeUpdates.foreachRDD(new VoidFunction<JavaRDD<FDAUpdateData>>() {
      @Override
      public void call(JavaRDD<FDAUpdateData> mapJavaRDD) throws Exception {
        List<FDAUpdateData> dataList = mapJavaRDD.collect();
        //Logger.getGlobal().info("StreaminfKafkaFDA---DataList -----------------> "+dataList.size());
        for (FDAUpdateData data : dataList) {
          Logger.getGlobal().info("StreaminfKafkaFDA---DataList -----------------> "+data.getTime());
          problem.update(data);
        }
      }
    });
  }
}
