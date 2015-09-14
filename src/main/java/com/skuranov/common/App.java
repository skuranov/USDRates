package com.skuranov.common;

/**
 * Created by zivert on 11.09.2015.
 */
        import org.hibernate.Session;
        import com.skuranov.persistence.HibernateUtil;
        import org.xml.sax.Attributes;
        import org.xml.sax.SAXException;
        import org.xml.sax.helpers.DefaultHandler;
        import javax.xml.parsers.SAXParser;
        import javax.xml.parsers.SAXParserFactory;
        import javax.xml.soap.*;
        import javax.xml.transform.Source;
        import javax.xml.transform.Transformer;
        import javax.xml.transform.TransformerException;
        import javax.xml.transform.TransformerFactory;
        import javax.xml.transform.stream.StreamResult;
        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.text.SimpleDateFormat;

public class App
{
    public static void main( String[] args ) throws SOAPException, TransformerException {
        SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        SOAPPart part = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        SOAPBody body = envelope.getBody();
        Name bodyName = envelope.createName("AllDataInfoXML", null, "http://web.cbr.ru/");
        body.addBodyElement(bodyName);
        soapMessage.saveChanges();
        String destination = "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx";
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();
        SOAPMessage reply = connection.call(soapMessage, destination);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = reply.getSOAPPart().getContent();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(os);
        transformer.transform(sourceContent, result);
        byte tempArr[] = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(tempArr);

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                boolean rateUSDExist = false;
                public void startElement(String uri, String localName,String qName,Attributes attributes)
                        throws SAXException{
                    if (qName.equalsIgnoreCase("USD")) {
                        rateUSDExist = true;
                        System.out.print("USD Rate: ");
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (rateUSDExist) {
                        System.out.println(new String(ch, start, length) + "    ");
                        rateUSDExist = false;
                        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()));
                        Session session = HibernateUtil.getSessionFactory().openSession();
                        session.beginTransaction();
                        Rate rate = new Rate();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
                        String curDateTime = simpleDateFormat.format(System.currentTimeMillis());
                        rate.setRateTime(curDateTime);
                        rate.setUSDRate(new String(ch, start, length) );
                        session.save(rate);
                        session.getTransaction().commit();
                    }
                }
            };

        saxParser.parse(is, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}