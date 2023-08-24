package Gooloom_CGV_V1.jdbc.connection;

//추상 클래스인 ConnectionConst 를 상속받는 자식 클래스들은
//부모 클래스인 ConnectionConst 의 메소드를 꼭 재정의/오버라이딩 해야 인스턴스를 생성하고 사용할 수 있다.
//ConnectionConst 는 인스턴스 생성 불가능
public abstract class ConnectionConst {
    //외부에서 사용해야 하므로 public 사용
    public static final String URL = "jdbc:mysql://localhost:3306/gooloom";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "qwer1234";
}
