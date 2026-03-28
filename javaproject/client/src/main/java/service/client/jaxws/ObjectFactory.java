
package service.client.jaxws;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the service.client.jaxws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetFeuxTemp_QNAME = new QName("http://Services.hub.service/", "getFeuxTemp");
    private final static QName _GetFeuxTempResponse_QNAME = new QName("http://Services.hub.service/", "getFeuxTempResponse");
    private final static QName _SetFeuxTemp_QNAME = new QName("http://Services.hub.service/", "setFeuxTemp");
    private final static QName _SetFeuxTempResponse_QNAME = new QName("http://Services.hub.service/", "setFeuxTempResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: service.client.jaxws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFeuxTemp }
     * 
     */
    public GetFeuxTemp createGetFeuxTemp() {
        return new GetFeuxTemp();
    }

    /**
     * Create an instance of {@link GetFeuxTempResponse }
     * 
     */
    public GetFeuxTempResponse createGetFeuxTempResponse() {
        return new GetFeuxTempResponse();
    }

    /**
     * Create an instance of {@link SetFeuxTemp }
     * 
     */
    public SetFeuxTemp createSetFeuxTemp() {
        return new SetFeuxTemp();
    }

    /**
     * Create an instance of {@link SetFeuxTempResponse }
     * 
     */
    public SetFeuxTempResponse createSetFeuxTempResponse() {
        return new SetFeuxTempResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeuxTemp }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetFeuxTemp }{@code >}
     */
    @XmlElementDecl(namespace = "http://Services.hub.service/", name = "getFeuxTemp")
    public JAXBElement<GetFeuxTemp> createGetFeuxTemp(GetFeuxTemp value) {
        return new JAXBElement<GetFeuxTemp>(_GetFeuxTemp_QNAME, GetFeuxTemp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeuxTempResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetFeuxTempResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://Services.hub.service/", name = "getFeuxTempResponse")
    public JAXBElement<GetFeuxTempResponse> createGetFeuxTempResponse(GetFeuxTempResponse value) {
        return new JAXBElement<GetFeuxTempResponse>(_GetFeuxTempResponse_QNAME, GetFeuxTempResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetFeuxTemp }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SetFeuxTemp }{@code >}
     */
    @XmlElementDecl(namespace = "http://Services.hub.service/", name = "setFeuxTemp")
    public JAXBElement<SetFeuxTemp> createSetFeuxTemp(SetFeuxTemp value) {
        return new JAXBElement<SetFeuxTemp>(_SetFeuxTemp_QNAME, SetFeuxTemp.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetFeuxTempResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SetFeuxTempResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://Services.hub.service/", name = "setFeuxTempResponse")
    public JAXBElement<SetFeuxTempResponse> createSetFeuxTempResponse(SetFeuxTempResponse value) {
        return new JAXBElement<SetFeuxTempResponse>(_SetFeuxTempResponse_QNAME, SetFeuxTempResponse.class, null, value);
    }

}
