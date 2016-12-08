package info.androidhive.firebase;

public class UserData
{
    private String userName;    // 사용자 이름을 set/get하기 위한 클래스

    public UserData()
    {

    }

    public UserData(String userName)
    {
        this.userName = userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

}
