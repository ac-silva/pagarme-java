package pagarme.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pagarme.api.model.Card;
import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardHashKey {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("date_created")
    private Date dateCreated;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("ip")
    private String ip;

    public String generate(Client client, Card card, String encryptionKey) throws JsonParseException,
            JsonMappingException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, CertificateException {
        //Obtendo publickey
        List<NameValuePair> encReq = new ArrayList<>();
        encReq.add(new BasicNameValuePair("encryption_key", encryptionKey));
        CardHashKey gen = client.get(encReq, CardHashKey.class);
        //Criando queryString
        String queryString = getQueryString(card);
        //Obtendo a chave pública
        String publickey = removeConfigKeysInPublicKey(gen.getPublicKey());
        //Criptografando
        Cipher cipher = encrypt(publickey);
        return toBase64(gen, cipher, queryString);
    }
    private Cipher encrypt(String publickey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException{
        BASE64Decoder b64       = new BASE64Decoder();
        byte[] decoded          = b64.decodeBuffer(publickey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        Cipher cipher           = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyFactory kf           = KeyFactory.getInstance("RSA");
        PublicKey key           = kf.generatePublic(spec);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }
    private String toBase64(CardHashKey gen, Cipher cipher, String queryString) throws IllegalBlockSizeException, BadPaddingException{
        String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(queryString.getBytes()));
        return String.valueOf(gen.getId()).concat("_").concat(encrypted);
    }
    private String getQueryString(Card card){
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("card_number", card.getCardNumber()));
        params.add(new BasicNameValuePair("card_holder_name", card.getHolderName()));
        params.add(new BasicNameValuePair("card_expiration_date", card.getExpirationDate()));
        params.add(new BasicNameValuePair("card_cvv", card.getCvv()));
        return URLEncodedUtils.format(params, "UTF-8");
    }
    private String removeConfigKeysInPublicKey(String publickey){
        return publickey.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", "");
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public String getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
