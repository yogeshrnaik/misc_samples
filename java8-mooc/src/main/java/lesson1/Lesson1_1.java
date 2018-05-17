package lesson1;

import java.util.ArrayList;
import java.util.List;

public class Lesson1_1 {

    private static class Employee {

        private final int joiningYear;
        private final int yearsOfExperience;

        public Employee(int joiningYear, int yearsOfExperience) {
            this.joiningYear = joiningYear;
            this.yearsOfExperience = yearsOfExperience;
        }

    }

    public static void main(String[] args) {
        System.out.println(findHighestYearsOfExperience(getEmployees(), 2016));
        System.out.println(findHighestYearsOfExperience(getEmployees(), 2015));
        System.out.println("*************************************************");
        System.out.println(findHighestYearsOfExperienceJava8(getEmployees(), 2016));
        System.out.println(findHighestYearsOfExperienceJava8(getEmployees(), 2015));
    }

    /**
     * - Imperative - We tell it what to do and how to do it <br/>
     * - Code controls the iteration <br/>
     * - Has Mutable state <br/>
     * - Cannot be parallelized easily due to mutable state <br/>
     */
    public static int findHighestYearsOfExperience(List<Employee> employees, int joiningYear) {
        int highestYearsOfExperience = 0;

        for (Employee e : employees) {
            if (e.joiningYear == joiningYear) {
                if (e.yearsOfExperience > highestYearsOfExperience) {
                    highestYearsOfExperience = e.yearsOfExperience;
                }
            }
        }

        return highestYearsOfExperience;
    }

    private static List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(2014, 2));
        employees.add(new Employee(2014, 5));

        employees.add(new Employee(2016, 9));
        employees.add(new Employee(2016, 4));
        employees.add(new Employee(2016, 6));
        return employees;
    }

    /**
     * - internal iteration instead of external iteration <br/>
     * - more abstract <br/>
     * - can be easily parallelized as there is no mutable state <br/>
     * - less code <br/>
     * - less error-prone <br/>
     * - declarative instead of imperative <br/>
     */
    public static int findHighestYearsOfExperienceJava8(List<Employee> employees, int joiningYear) {
        return employees.stream()
            .filter(e -> e.joiningYear == joiningYear)
            .mapToInt(e -> e.yearsOfExperience)
            .max().orElse(0);
    }
}
