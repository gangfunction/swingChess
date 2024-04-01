package game.login;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class MessageBuilder{
    private Map<String, Object> messageData;
    public MessageBuilder(){
        messageData = new HashMap<>();
    }
    public MessageBuilder add(String key, Object value){
        messageData.put(key, value);
        return this;
    }
    public String buildJson() {
        return new Gson().toJson(messageData);
    }
    
}
