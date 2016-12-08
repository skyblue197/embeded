package info.androidhive.firebase;


public class ChatData   //채팅방에서 이용되는 메시지 내용, 사용자 이름, 시간 기록등을 set/get하기 위한 클래스
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

    public String getUserName()
    {
        return userName;
    }

    public String getMessage()
    {
        return message;
    }

    public String getTime()
    {
        return time;
    }
}
