package  org.uma.jmetalsp.application.fda.sparkutil;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.uma.jmetalsp.spark.util.spark.StreamingConfiguration;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class StreamingConfigurationFDA implements StreamingConfiguration,Serializable {
  private HashMap<String, String> kafkaParams ;
  private String kafkaTopics;
  public StreamingConfigurationFDA(){
    super();
  }
  /**
   * Create kafka configuration
   * @param kafkaServer
   * @param kafkaPort
   * @param kafkaTopics
   */
  public void initializeKafka(String kafkaServer,int kafkaPort,String kafkaTopics){
    this.kafkaTopics=kafkaTopics;
    kafkaParams = new HashMap<>();
    //kafkaParams.put("metadata.broker.list", "localhost:9092");//desde fuera de la misma maquina falla la conexion debida a esa linea
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer+":"+kafkaPort );
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "StreamingKafkaFDA");
    kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    kafkaParams.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
  }

  public HashMap<String, String> getKafkaParams() {
    return kafkaParams;
  }

  public String getKafkaTopics() {
    return kafkaTopics;
  }
}
