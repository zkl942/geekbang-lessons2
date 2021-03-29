package org.geektimes.projects.user.demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemo {
    private static Employee[] arrayOfEmps = {
            new Employee(1, "Jeff Bezos", 100000.0),
            new Employee(2, "Bill Gates", 200000.0),
            new Employee(3, "Mark Zuckerberg", 300000.0)
    };

    private static List<Employee> empList = Arrays.asList(arrayOfEmps);

    public static void main(String[] args) {
        // simple stream creation
        Stream.of(arrayOfEmps);
        empList.stream();

        // stream creation using builder
        Stream.Builder<Employee> empStreamBuilder = Stream.builder();

        empStreamBuilder.accept(arrayOfEmps[0]);
        empStreamBuilder.accept(arrayOfEmps[1]);
        empStreamBuilder.accept(arrayOfEmps[2]);

        Stream<Employee> empStream = empStreamBuilder.build();

        /**
         * forEach() is a terminal operation, which means that, after the operation is performed,
         * the stream pipeline is considered consumed, and can no longer be used.
         */
        System.out.println("forEaching...");
        empList.stream().forEach(employee -> employee.salaryIncrement(10.0));
        empList.stream().forEach(employee -> System.out.println(employee));

        // map
        Integer[] empIds = { 1, 2, 3 };

        /**
         * collect:
         * one of the common ways to get stuff out of the stream once we are done with all the processing:
         */
        System.out.println("collecting...");
        List<Employee> employees = empList.stream().collect(Collectors.toList());
        employees.stream().forEach(employee -> System.out.println(employee));

        /**
         * filter:
         * produces a new stream that contains elements of the original stream that
         * pass a given test (specified by a Predicate).
         */
        System.out.println("filtering...");
        empList.stream().filter(employee -> employee.getSalary() > 200000).forEach(employee -> System.out.println(employee));

        /**
         * findFirst
         * 注意这个orElse(null)
         */
        Employee e = empList.stream().filter(employee -> employee.getSalary() > 200000).findFirst().orElse(null);
        System.out.println(e.getName() + " " + e.getSalary());

    }
}
