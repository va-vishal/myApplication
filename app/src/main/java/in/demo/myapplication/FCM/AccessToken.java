package in.demo.myapplication.FCM;



import android.util.Log;

import com.google.common.collect.Lists;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class AccessToken {

    private static final String firebaseMessagingScope="https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try{
            String  jsonString="{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"welove-8617d\",\n" +
                    "  \"private_key_id\": \"514eb1e098015e42b3ee743237e68151be1dca51\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2d1exl6C9AWpf\\nERIbVfF8Y1dKd1KJnhs1Uk+DM9xv4YfmZMkOs/AwWZhbjM0oqqU8n++wuZGBm87E\\nh8WJyh/oAl6LzFH4WHxfiLqgGezJMVv5DuHAxZ6zoxqKoSZ/g4r+xJ1xfgo0eWsn\\nAVYybmows8l1SmpjbChboiair8MWLpba95/4lhEeah+NccUmYNVjmBCV72va8b0g\\niMP7wt8QmBdPs9VrO4cn1EBw0onWfC+18gRILY8y/CEMCzA6a0OcSkW/JPtgWZv6\\niZm3jJ1ROELkYyTSz95fWfdUuDNXy5KsepOonYnr1HHyZBHSTK/O+8aRRpSsGs80\\nEmE3lJU1AgMBAAECggEAI8wnIrreOKaB8XO+aymBzocI1Z2yTjh8GwpyegmfKj4D\\ndQJ55Y4MxdeWCgQGLf/ImivAkRne/6ALQUhny2AhwVlRpjioLkJh6DZBcu6TI2ex\\nJRwz0yeSOqj5qI/0JxkuGEyHnP/+zYuyp3zeJde+sp5bHE1EsQvDoTKfRNSZwAx/\\n1sSYPjWzmFKH40Fg/dgX/xnq4ZyyqAbO8X1kmmAuXcGFGm1sRIsNSDRFW2h7QCWL\\ncrZFTNlp83AcBeMMb+/j64G1idHMKQuurLVqmuk7tqsJ/gnkHG4XlNZVGFnemPcv\\n8rxThVCzhHZrc9S3je4BRN5h1X8XBqPrH+Q+vQvJcQKBgQDo4AHojzGxe0IHmqml\\nDI2l00q2PoTYkvDgcykvGb0+ZCou18PhrMq7WqcLCJm+2Egeo+LsphNiiH797B2Z\\nM3lolkO3a2fC8TU8phu2KzbN0eeJ/w6Sg2L0yhkE5m8w9vLQ+6eO0vbCjbQmvQPT\\nP7QJs5QcIbnayA46syHusFbZmQKBgQDIleAQQaljMN4U8jV/xtDt2t5xBN2VU9xd\\n107qInJndjmgMbjitgq88W7DTlx6va1mVRfsIt6YmqOJFjXWwtgYGennXdZJyhEY\\nNiIuw4VBQh19O3c7mnRPGTFWpZ4J0XSExBURKpXjVCwQB4I0RBkEqjDEsLLVobA4\\nPe7P5ytx/QKBgHqmiLrIuF6G0ZcZsWHZg70j1MS5ltROSAme/cDt6Is0zDbRszxn\\n+nrE2xfnlro6RXnFzV3gdnM9+syYEFrBDp+0DMRNEGBPFD/2KMBAcGPkuvMmX7Fw\\n+6x7yMOY7lU2q1rAQFbr68U6RvjkMmCPu64kY8yRQsbk+QGXmTaSr5t5AoGBAMMr\\n0dD/kunI6s5GvneN5E0L4dTmfxSFfK5yiX6X9ellx7xvpQ4zQ0+FO9kdri5f0RD5\\nS0WgyCaEkQsaaViGTe1liYpua8twqiF8ytXluQs+YaMw8Vs1dmZghPliPNwgyddF\\nU5pYbW+8vwL5y18/w3gegUVhCSGFKgoPeQciT8uhAoGBAKKFVkP2JRernFBVour+\\nafwLydCbnsV0zzb2legrTQ/+oLYaZkwGn7gaQ4gRji1O4qFnhCcSS3S+jvmzWHsf\\nTRHS0Cbx8THDv+rKq6vpG6Fpkpda+v2+FkhAYr94RuqQOBOQiOC5mOK2NuM5csAM\\nxdaZIuJSI6ITAkxBecCWpjRB\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-b4zki@welove-8617d.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"117824207681607832531\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-b4zki%40welove-8617d.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream=new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList(firebaseMessagingScope));

            googleCredentials.refresh();

            return googleCredentials.getAccessToken().getTokenValue();
        }catch(IOException e)
        {
            Log.e("error",""+e.getMessage());
            return null;
        }
    }
}
