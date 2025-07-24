package inc.yowyob.rental_api_reactive.persistence.config;

import com.fasterxml.jackson.databind.ObjectMapper; // <<< NOUVEAU
import inc.yowyob.rental_api_reactive.persistence.converter.JsonStringToMapConverter;
import inc.yowyob.rental_api_reactive.persistence.converter.JsonToWorkingHoursConverter;
import inc.yowyob.rental_api_reactive.persistence.converter.MapToJsonStringConverter;
import inc.yowyob.rental_api_reactive.persistence.converter.MoneyToStringConverter;
import inc.yowyob.rental_api_reactive.persistence.converter.StringToMoneyConverter;
import inc.yowyob.rental_api_reactive.persistence.converter.WorkingHoursToJsonConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableReactiveCassandraRepositories(basePackages = "inc.yowyob.rental_api_reactive.persistence.repository")
public class CassandraReactiveConfig extends AbstractReactiveCassandraConfiguration {

    private final String keyspaceName;
    private final String contactPoints;
    private final int port;
    private final String localDataCenter;
    private final ObjectMapper objectMapper;

    // <<< NOUVEAU : Injection par constructeur, c'est une meilleure pratique
    public CassandraReactiveConfig(
            @Value("${spring.cassandra.keyspace-name}") String keyspaceName,
            @Value("${spring.cassandra.contact-points}") String contactPoints,
            @Value("${spring.cassandra.port}") int port,
            @Value("${spring.cassandra.local-datacenter}") String localDataCenter,
            ObjectMapper objectMapper) {
        this.keyspaceName = keyspaceName;
        this.contactPoints = contactPoints;
        this.port = port;
        this.localDataCenter = localDataCenter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    protected String getContactPoints() {
        return contactPoints;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return localDataCenter;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(
                CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
                        .ifNotExists()
                        .with(KeyspaceOption.DURABLE_WRITES, true)
                        .withSimpleReplication(1L)
        );
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"inc.yowyob.rental_api_reactive.persistence.entity"};
    }

    // On surcharge la méthode pour fournir nos convertisseurs personnalisés
    @Override
    public CassandraCustomConversions customConversions() {
        return new CassandraCustomConversions(List.of(
                new MapToJsonStringConverter(objectMapper),
                new JsonStringToMapConverter(objectMapper),
                new MoneyToStringConverter(),
                new StringToMoneyConverter(),
                new WorkingHoursToJsonConverter(),
                new JsonToWorkingHoursConverter()
        ));
    }

}
