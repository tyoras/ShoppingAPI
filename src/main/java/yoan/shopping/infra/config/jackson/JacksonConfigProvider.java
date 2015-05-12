/**
 * 
 */
package yoan.shopping.infra.config.jackson;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * Jackson configuration provider
 * @author yoan
 */
@Provider
@Produces("application/json")
public class JacksonConfigProvider implements ContextResolver<ObjectMapper> {
    private final ObjectMapper objectMapper;

    public JacksonConfigProvider() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.getSerializationConfig().withSerializationInclusion(Include.NON_NULL);
        // Currently it is not possible to configure the LocaleDate serializationformat with JSR310Module
//        objectMapper.getSerializationConfig().with(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"));
//        objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"));
//        objectMapper.getDeserializationConfig().with(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"));
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
 }
