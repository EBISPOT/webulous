package uk.ac.ebi.spot.webulous.config;

import com.mongodb.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoFactoryBean;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 13/04/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Configuration
public class MongoAppConfig {

    @Value("${webulous.mongo.readpreference:}")
    String readPreference;

    @Value("${webulous.mongo.seedlist:}")
    String seedList;

    @Autowired
    MongoProperties properties;

/*
* Factory bean that creates the com.mongodb.Mongo instance
* Checks supplied properties for a seed list and read preference
*
* todo update to     MongoClientFactoryBean when spring mongo data 1.7 is available for spring boot
*/

    @Bean
    MongoFactoryBean mongoFactory() throws UnknownHostException {


        MongoFactoryBean bean = new MongoFactoryBean();

        if (StringUtils.isNoneEmpty(readPreference, seedList)) {
            List<ServerAddress> seedListArray = new ArrayList<ServerAddress>();

            for (String seed : seedList.split(",")) {
                seedListArray.add(new ServerAddress(seed));
            }

            bean.setReplicaSetSeeds(seedListArray.toArray(new ServerAddress[seedListArray.size()]));
            ReadPreference preference = ReadPreference.valueOf(readPreference);
            if (preference != null) {

                // use a mongo client options builder when 1.7 is available
                //MongoClientOptions.Builder clientOptions = new MongoClientOptions.Builder();
                //clientOptions.readPreference(preference);
                MongoOptions options = new MongoOptions();
                options.setReadPreference(preference);
                bean.setMongoOptions(options);
            }
        }
        else {
            bean.setHost(properties.getHost());
            if (properties.getPort() != null) {
                bean.setPort(properties.getPort());
            }
        }
        return bean;

    }
}
