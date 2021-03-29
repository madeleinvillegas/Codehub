package ph.edu.dlsu.codehub;

public class User extends Person {
    private int age;
    private Person[] listOfFriends;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person[] getListOfFriends() {
        return listOfFriends;
    }

    public void setListOfFriends(Person[] listOfFriends) {
        this.listOfFriends = listOfFriends;
    }



}
