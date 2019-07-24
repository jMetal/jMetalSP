package org.uma.jmetalsp.flink.streamingdatasource;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalsp.flink.FlinkStreamingDataSource;
import org.uma.jmetalsp.flink.avro.SimpleAVROSchema;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.serialization.DataDeserializer;
import org.uma.jmetalsp.serialization.counter.Counter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class SimpleFlinkKafkaStreamingCounterDataSourceAVRO implements FlinkStreamingDataSource<ObservedValue<Integer>> {
    private Observable<ObservedValue<Integer>> observable;
    private Properties kafkaParams;
    private List<String> topic;
    private StreamExecutionEnvironment environment;
    private long time;
    private String pathAVRO;

    public SimpleFlinkKafkaStreamingCounterDataSourceAVRO(
            Observable<ObservedValue<Integer>> observable,
            Properties kafkaParams,String topic,String pathAVRO) {
        this.observable = observable;
        this.kafkaParams = kafkaParams;
        this.topic = new ArrayList<>();
        this.topic.add(topic);
        this.pathAVRO=pathAVRO;
    }

    public SimpleFlinkKafkaStreamingCounterDataSourceAVRO(Properties kafkaParams, String topic,String pathAVRO) {
        this(new DefaultObservable<>(),kafkaParams,topic,pathAVRO);
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
        /**
         * DataDeserializer<Counter> dataDeserializer = new DataDeserializer<>();
         *             //Object o =dataDeserializer.deserialize(value.value(),"avsc/Counter.avsc");
         *             //GenericData.Record rc=(GenericData.Record)o;
         *             Counter counter = dataDeserializer.deserialize(value.value(),"avsc/Counter.avsc");
         */

        try {
            if (data != null) {
                Iterator<String> it = DataStreamUtils.collect(data);

                while (it.hasNext()) {
                    byte [] bytes= it.next().getBytes();
                    DataDeserializer<Counter> dataDeserializer = new DataDeserializer<>();
                    Counter counter = dataDeserializer.deserialize(bytes,"avsc/Counter.avsc");
                    Integer number = (Integer) counter.get(0);
                    observable.setChanged();
                    observable.notifyObservers(new ObservedValue<Integer>(number));
                }

            }
        }catch(Exception e){
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
