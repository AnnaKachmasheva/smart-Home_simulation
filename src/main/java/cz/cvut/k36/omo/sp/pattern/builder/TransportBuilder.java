package cz.cvut.k36.omo.sp.pattern.builder;

import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.transport.Transport;
import cz.cvut.k36.omo.sp.model.transport.TypeTransport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransportBuilder {

    private static final Logger LOGGER = LogManager.getLogger(TransportBuilder.class.getName());

    private String name;
    private TypeTransport type;
    private Person currentUser;

    public TransportBuilder() {
    }

    public TransportBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TransportBuilder withType(String type) {
        this.type = getTypeTransport(type);
        return this;
    }

    /**
     * @param type String transport's type
     * @return TypeTransport equivalent type
     */
    private TypeTransport getTypeTransport(String type) {
        return switch (type) {
            case "BICYCLE" -> TypeTransport.BICYCLE;
            case "SKIS" -> TypeTransport.SKIS;
            case "AUTO" -> TypeTransport.AUTO;
            default -> null;
        };
    }

    public TransportBuilder withCurrentUser(Person person) {
        this.currentUser = person;
        return this;
    }

    public Transport build() {
        if (this.name == null || this.type == null) {
            LOGGER.warn("Some required arguments are not provided: name, type!");
            return null;
        }
        Transport transport = new Transport(this.name, this.type);
        transport.setCurrentUser(this.currentUser);
        return transport;
    }
}