package storm;
import java.util.Arrays;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
/**
 * @author KGZ
 * @date 2019/4/20 15:33
 */
public class MyKafkaTopology {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
        String zks = "172.17.11.250:2181,172.17.11.251:2181,172.17.11.252:2181";
        String topic = "Topic012";
        // String zkRoot = "/opt/modules/app/zookeeper/zkdata"; // default zookeeper root configuration for storm
        String id = "wordtest";

        BrokerHosts brokerHosts = new ZkHosts(zks);
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, "", id);
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConf.forceFromStart = false;
        spoutConf.zkServers = Arrays.asList(new String[] {"172.17.11.250", "172.17.11.251", "172.17.11.252"});
        spoutConf.zkPort = 2181;

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 2); // Kafka我们创建了一个2分区的Topic，这里并行度设置为2
        builder.setBolt("print-bolt", new PrintBolt(), 2).shuffleGrouping("kafka-reader");

        Config conf = new Config();
        String name = MyKafkaTopology.class.getSimpleName();
        if (args != null && args.length > 0) {
            // Nimbus host name passed from command line
            conf.put(Config.NIMBUS_HOST, args[0]);
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(name, conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(name, conf, builder.createTopology());
//               Thread.sleep(60000);
//               cluster.killTopology(name);
//               cluster.shutdown();

//               StormSubmitter.submitTopology(name, conf, builder.createTopology());
        }
    }
}
