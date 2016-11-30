package info.androidhive.firebase;

public class UserData
{
    private String userName;

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
