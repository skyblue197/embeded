package info.androidhive.firebase;


public class ChatData
{
    private String userName;
    private String message;
    private String filepath;
    private String time;


    public ChatData() { }



    public ChatData(String userName, String message, String time)
    {
        this.userName = userName;
        this.message = message;
        this.time = time;
    }

    public ChatData(String filepath)
    {
        this.filepath = filepath;
    }

    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    public String getFilepath()
    {
        return filepath;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setTime(String time) {this.time = time;}

    public String getTime()
    {
        return time;
    }
}
