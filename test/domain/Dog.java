package domain;

public class Dog extends Animal {
    private String name;

    public Dog(int age, String name) {
        super(age);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
