package uk.ac.ebi.spot.webulous.config;

import com.mongodb.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

import java.net.UnknownHostException;
import java.util.ArrayList;
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
*/

    @Bean
    MongoClientFactoryBean mongoFactory() throws UnknownHostException {

        MongoClientFactoryBean mongoClientFactoryBean = new MongoClientFactoryBean();

        if (StringUtils.isNoneEmpty(readPreference, seedList)) {
            List<ServerAddress> seedListArray = new ArrayList<ServerAddress>();

            for (String seed : seedList.split(",")) {
                seedListArray.add(new ServerAddress(seed));
            }

            mongoClientFactoryBean.setReplicaSetSeeds(seedListArray.toArray(new ServerAddress[seedListArray.size()]));
            ReadPreference preference = ReadPreference.valueOf(readPreference);
            if (preference != null) {
                MongoClientOptions.Builder clientOptions = new MongoClientOptions.Builder();
                clientOptions.readPreference(preference);
                mongoClientFactoryBean.setMongoClientOptions(clientOptions.build());
            }
        }
        else {
            mongoClientFactoryBean.setHost(properties.getHost());
            if (properties.getPort() != null) {
                mongoClientFactoryBean.setPort(properties.getPort());
            }
        }
        return mongoClientFactoryBean;

    }
}
