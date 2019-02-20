package org.uma.jmetalsp.flink.streamingdatasource;

import org.apache.flink.api.common.io.FilePathFilter;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.fs.Path;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.flink.FlinkStreamingDataSource;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

public class SimpleFlinkKafkaStreamingCounterDataSource implements FlinkStreamingDataSource<ObservedValue<Integer>> {
    private Observable<ObservedValue<Integer>> observable;
    private Properties kafkaParams;
    private List<String> topic;
    private StreamExecutionEnvironment environment;
    private long time;

    public SimpleFlinkKafkaStreamingCounterDataSource(
            Observable<ObservedValue<Integer>> observable,
            Properties kafkaParams,String topic) {
        this.observable = observable;
        this.kafkaParams = kafkaParams;
        this.topic = new ArrayList<>();
        this.topic.add(topic);
    }

    public SimpleFlinkKafkaStreamingCounterDataSource(Properties kafkaParams,String topic) {
        this(new DefaultObservable<>(),kafkaParams,topic);
    }
    @Override
    public void setExecutionEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public void run() {

        JMetalLogger.logger.info("Run Fink method in the streaming data source invoked") ;
        JMetalLogger.logger.info("TOPIC: " + topic) ;

       // environment.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(1,0));
        //environment.enableCheckpointing(10);

        DataStream<String> data= environment.addSource(
                new FlinkKafkaConsumer010<String>(topic, new SimpleStringSchema(),kafkaParams));




        try {
            Iterator<String> it=DataStreamUtils.collect(data);
            while (it.hasNext()){
                Integer number = Integer.parseInt(it.next());
                //Integer number = it.next();
                observable.setChanged();
                observable.notifyObservers(new ObservedValue<Integer>(number));
            }

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public Observable<ObservedValue<Integer>> getObservable() {
        return this.observable;
    }

    /*public class IntegerDeserializationSchema  implements DeserializationSchema<Integer>, SerializationSchema<Integer>, Serializable {


        public IntegerDeserializationSchema(){
            super();
        }
        @Override
        public Integer deserialize(byte[] bytes) throws IOException {
            return Integer.parseInt(new String(bytes,"UTF-8"));
        }

        @Override
        public boolean isEndOfStream(Integer integer) {
            return false;
        }

        @Override
        public TypeInformation<Integer> getProducedType() {
            final TypeInformation<Integer> result=TypeExtractor.getForClass(Integer.class);
            return result;
        }

        @Override
        public byte[] serialize(Integer integer) {
             //byte [] result = null;
             final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final DataOutputStream dos = new DataOutputStream(bos);
            try {
                dos.writeInt(integer);
                dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  bos.toByteArray();
        }
    }*/
}
