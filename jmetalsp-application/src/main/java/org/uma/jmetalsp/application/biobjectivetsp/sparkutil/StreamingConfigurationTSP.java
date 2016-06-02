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
package org.uma.jmetalsp.application.biobjectivetsp.sparkutil;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.uma.jmetalsp.util.spark.StreamingConfiguration;

import java.io.Serializable;
import java.util.HashMap;

import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * StreamingConfigurationTSP class for initialize  hdfs, kafka and twitter streaming
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class StreamingConfigurationTSP implements StreamingConfiguration,Serializable {
    private Authorization twitterAuth;
    private HashMap<String, String> kafkaParams ;
    private String kafkaTopics;
    private String twitterFilter;
    private String dataDirectoryName;
    public StreamingConfigurationTSP(){
       super();
    }

  /**
   * Set directory to be read with streaming
   * @param dataDirectoryName
   */
    public void initializeDirectoryTSP(String dataDirectoryName){
        this.dataDirectoryName=dataDirectoryName;
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
        // kafkaParams.put("metadata.broker.list", brokers);//desde fuera de la misma maquina falla la conexion debida a esa linea
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer+":"+kafkaPort );//"master.bd.khaos.uma.es:6667"
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "StreamingKafkaTSP");
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        kafkaParams.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    }

  /**
   * Create Twitter configuration
   * @param consumerKey
   * @param consumerSecret
   * @param accessToken
   * @param accessTokenSecret
   * @param proxyHost
   * @param proxyPort
   * @param twitterFilter
   */
    public void initializeTwitter(String consumerKey,String consumerSecret,String accessToken,String accessTokenSecret,String proxyHost,int proxyPort,String twitterFilter){
        this.twitterFilter=twitterFilter;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        if (proxyHost!=null) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", String.valueOf(proxyPort));
        }
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(
                        consumerSecret)
                .setOAuthAccessToken(
                        accessToken)
                .setOAuthAccessTokenSecret(
                        accessTokenSecret);
        Configuration twitterConf = cb.build();

        twitterAuth = AuthorizationFactory.getInstance(twitterConf);
    }

    public Authorization getTwitterAuth() {
        return twitterAuth;
    }

    public HashMap<String, String> getKafkaParams() {
        return kafkaParams;
    }

    public String getKafkaTopics() {
        return kafkaTopics;
    }

    public String getTwitterFilter() {
        return twitterFilter;
    }

    public String getDataDirectoryName() {
        return dataDirectoryName;
    }
}
