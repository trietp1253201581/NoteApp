package com.noteapp.user.service.security;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.noteapp.user.model.Email;
import java.util.List;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Sử dụng dịch vụ Mailjet để gửi email 
 * @author Nhóm 17
 */
public class MailjetSevice implements MailService {
    private static final String API_KEY_PUBLIC = "00c053f4acc4c436209908626c532494";
    private static final String API_KEY_SECRET = "69bea19a95cc697c427dd60569d3e26f";
    private static final String BASE_URL = "https://api.mailjet.com";
    private static final ClientOptions CLIENT_OPTIONS;
    protected static final String EMAIL_ADDRESS = "trietkd1475369@gmail.com";
    protected static final String EMAIL_NAME = "NoteApp Support";
    protected Email fromEmail;
    
    //Khởi tạo các biến hằng cần thiết
    static {
        ClientOptions.ClientOptionsBuilder builder = ClientOptions.builder();
        builder.apiKey(API_KEY_PUBLIC);
        builder.apiSecretKey(API_KEY_SECRET);
        builder.baseUrl(BASE_URL);
        builder.okHttpClient(new OkHttpClient());
        CLIENT_OPTIONS = builder.build();
    }
    
    /**
     * Khởi tạo Mailjet cùng với địa chỉ gửi có sẵn
     */
    public MailjetSevice() {
        fromEmail = new Email();
        fromEmail.setAddress(EMAIL_ADDRESS);
        fromEmail.setName(EMAIL_NAME);
    }
    
    /**
     * Chuyển một địa chỉ email về {@link JSONObject}
     * @param email Đối tượng chứa địa chỉ Email cần chuyển
     * @return Một đối tượng Json chứa các thuộc tính về địa chỉ
     * và tên email
     */
    protected static JSONObject createEmailJSONObject(Email email) {
        JSONObject emailJSONObj = new JSONObject();
        emailJSONObj.put("Email", email.getAddress());
        emailJSONObj.put("Name", email.getName());
        return emailJSONObj;
    }
    
    @Override
    public boolean sendMail(Email toEmail, String subject, String content) {       
        //Tạo JSONObject cho địa chỉ gửi
        JSONObject fromEmailJSONObject = createEmailJSONObject(fromEmail);
        //Tạo JSONObject cho địa chỉ nhận
        JSONArray toEmailsJSONArray = new JSONArray();
        toEmailsJSONArray.put(createEmailJSONObject(toEmail));
        //Tạo JSONArray để cấu hình email gửi đi
        JSONArray messagesJSONArray = new JSONArray();
        JSONObject messageJSONObject = new JSONObject();
        messageJSONObject.put(Emailv31.Message.FROM, fromEmailJSONObject);
        messageJSONObject.put(Emailv31.Message.TO, toEmailsJSONArray);
        messageJSONObject.put(Emailv31.Message.SUBJECT, subject);
        messageJSONObject.put(Emailv31.Message.TEXTPART, content);
        messagesJSONArray.put(messageJSONObject);
        //Gửi email
        MailjetClient client = new MailjetClient(CLIENT_OPTIONS);
        MailjetRequest request = new MailjetRequest(Emailv31.resource);
        request.property(Emailv31.MESSAGES, messagesJSONArray);
        try {
            client.post(request);
            return true;
        } catch (MailjetException ex) {
            return false;
        }
    }
    
    @Override
    public boolean sendMail(List<Email> toEmails, String subject, String content) {       
        //Tạo JSONObject cho địa chỉ gửi
        JSONObject fromEmailJSONObject = createEmailJSONObject(fromEmail);
        //Tạo JSONArray cho các địa chỉ nhận
        JSONArray toEmailsJSONArray = new JSONArray();
        for(Email toEmail: toEmails) {
            toEmailsJSONArray.put(createEmailJSONObject(toEmail));
        }
        //Tạo JSONArray để cấu hình email gửi đi
        JSONArray messagesJSONArray = new JSONArray();
        JSONObject messageJSONObject = new JSONObject();
        messageJSONObject.put(Emailv31.Message.FROM, fromEmailJSONObject);
        messageJSONObject.put(Emailv31.Message.TO, toEmailsJSONArray);
        messageJSONObject.put(Emailv31.Message.SUBJECT, subject);
        messageJSONObject.put(Emailv31.Message.TEXTPART, content);
        messagesJSONArray.put(messageJSONObject);
        //Gửi email
        MailjetClient client = new MailjetClient(CLIENT_OPTIONS);
        MailjetRequest request = new MailjetRequest(Emailv31.resource);
        request.property(Emailv31.MESSAGES, messagesJSONArray);
        try {
            client.post(request);
            return true;
        } catch (MailjetException ex) {
            return false;
        }
    }
}
