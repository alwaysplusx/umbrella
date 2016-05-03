 com.harmony.umbrella.example.ws;

import java.util.ServiceLoader;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.stream.XMLInputFactory;

import com.harmony.umbrella.example.entity.Bar;
import com.harmony.umbrella.example.entity.Foo;
import com.harmony.umbrella.mapper.BeanMapper;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.ws.ser.Message;
import com.harmony.umbrella.ws.ser.ServerSupport;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "MapperBean")
@WebService(serviceName = "MapperBean")
public class MapperBean extends ServerSupport {

    public Message mapper(Foo foo) {
        BeanMapper mapper = BeanMapper.getInstance("mapping.xml");
        Bar bar = mapper.mapper(foo, Bar.class, "foo2bar");
        System.out.println(bar);
        return success();
    }

    public String getXMLInputFactoryName() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        boolean result = setRestrictionProperties(factory);

        LOG.info("default XMLInputFactory {} set properties result {}", factory, result);

        factory = XMLInputFactory.newFactory(XMLInputFactory.class.getName(), ClassUtils.getDefaultClassLoader());

        result = setRestrictionProperties(factory);

        LOG.info("current context classLoader XMLInputFactory {} set properties result {}", factory, result);

        factory = new com.ctc.wstx.stax.WstxInputFactory();

        result = setRestrictionProperties(factory);

        LOG.info("use new XMLInputFactory {} set properties result {}", factory, result);

        ServiceLoader<XMLInputFactory> loader = ServiceLoader.load(XMLInputFactory.class);
        loader.reload();

        for (XMLInputFactory provider : loader) {
            System.out.println(provider);
        }

        return factory == null ? "" : factory.getClass().getName();
    }

    private static boolean setRestrictionProperties(XMLInputFactory factory) {
        return setProperty(factory, "com.ctc.wstx.maxAttributesPerElement", 1)//
                && setProperty(factory, "com.ctc.wstx.maxAttributeSize", 1)//
                && setProperty(factory, "com.ctc.wstx.maxChildrenPerElement", 1)//
                && setProperty(factory, "com.ctc.wstx.maxElementCount", 1)//
                && setProperty(factory, "com.ctc.wstx.maxElementDepth", 1)//
                && setProperty(factory, "com.ctc.wstx.maxCharacters", 1) //
                && setProperty(factory, "com.ctc.wstx.maxTextLength", 1);
    }

    private static boolean setProperty(XMLInputFactory f, String p, Object o) {
        try {
            f.setProperty(p, o);
            return true;
        } catch (Throwable t) {
            // ignore
        }
        return false;
    }
}
