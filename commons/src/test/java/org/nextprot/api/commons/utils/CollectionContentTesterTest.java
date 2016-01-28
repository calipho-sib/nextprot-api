package org.nextprot.api.commons.utils;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CollectionContentTesterTest {

    @Test
    public void foundValidElement() throws Exception {

        List<Person> persons = Arrays.asList(new Person("bob", 23), new Person("jon", 29), new Person("loki", 450));

        CollectionContentTester<Person, String> tester = new CollectionContentTester<Person, String>(persons) {

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
            protected boolean hasExpectedContent(Person element, Map<String, Object> expectedElementValues) {

                return expectedElementValues.get("name").equals(element.getName()) && expectedElementValues.get("age").equals(element.getAge());
            }
        };

        Map<String, Object> expectedPersonProps = new HashMap<>();
        expectedPersonProps.put("name", "jon");
        expectedPersonProps.put("age", 29);

        Assert.assertTrue(tester.hasElementWithContent("jon29", expectedPersonProps));
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