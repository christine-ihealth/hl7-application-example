package send;

import ca.uhn.hl7v2.hoh.api.*;
import ca.uhn.hl7v2.hoh.auth.SingleCredentialClientCallback;
import ca.uhn.hl7v2.hoh.raw.api.RawSendable;
import ca.uhn.hl7v2.hoh.raw.client.HohRawClientSimple;
import ca.uhn.hl7v2.hoh.sockets.TlsSocketFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.IOException;

public class SendPatientRaw {

    public static void send(String url, int port, String username, String password) throws Exception {
        String host;
        if(url.startsWith("https://")) {
            host = url.substring(8);
        }else if(url.startsWith("http://")){
            host = url.substring(7);
        }else{
            host = url;
        }
        String uri = "/hl7/nonHapi";

// Create a parser
        Parser parser = PipeParser.getInstanceWithNoValidation();

// Create a client
        HohRawClientSimple client = new HohRawClientSimple(host, port, uri);
        if (url.startsWith("https://")) {
            client.setSocketFactory(new TlsSocketFactory());
        }

// Optionally, if credentials should be sent, they
// can be provided using a credential callback
        IAuthorizationClientCallback authCallback = new SingleCredentialClientCallback(username, password);
        client.setAuthorizationCallback(authCallback);

// The ISendable defines the object that provides the actual
// message to send
        String message =
                "MSH|^~\\&#|||||20210825144232.713+0800||REF^I12^REF_I12|15701|blahblah|2.8.1\r" +
                        "PID|||123||Jia^Xi||20210825163701.895+0800|^M|||||(003)060-0974||^EL||||||||||||||||||20210825163701.898+0800\r";

        ISendable<String> sendable = new RawSendable(message);

        try {
            // sendAndReceive actually sends the message
            IReceivable<String> receivable = client.sendAndReceive(sendable);

            // receivavle.getRawMessage() provides the response
            System.out.println("Response was:\n" + receivable.getMessage());

            /*
             * Note that the client may be reused as many times as you like,
             * by calling sendAndReceiveMessage repeatedly
             */

        } catch (DecodeException | IOException | EncodeException e) {
            // Thrown if the response can't be read
            e.printStackTrace();
        } // Thrown if communication fails
        // Thrown if the message can't be encoded (generally a programming bug)

    }
}
