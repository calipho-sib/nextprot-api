package org.nextprot.api.commons.utils;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ExpectedElementTesterTest {

    @Test
    public void foundValidElement() throws Exception {

        ExpectedElementTester<Person, String> tester = new ExpectedElementTester<Person, String>() {

            @Override
            protected Function<Person, String> createElementToKeyFunc() {
                return new Function<Person, String>() {
                    @Override
                    public String apply(Person person) {
                        return person.getName()+person.getAge();
                    }
                };
            }

            @Override
            protected boolean isValidContent(Person element, Map<String, Object> expectedElementValues) {

                return expectedElementValues.get("name").equals(element.getName()) && expectedElementValues.get("age").equals(element.getAge());
            }
        };

        List<Person> persons = Arrays.asList(new Person("bob", 23), new Person("jon", 29), new Person("loki", 450));
        Map<String, Object> expectedPersonProps = new HashMap<>();
        expectedPersonProps.put("name", "jon");
        expectedPersonProps.put("age", 29);

        Assert.assertTrue(tester.testElement(persons, "jon29", expectedPersonProps));
    }

    private static class Person {

        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        int getAge() {
            return age;
        }
    }
}