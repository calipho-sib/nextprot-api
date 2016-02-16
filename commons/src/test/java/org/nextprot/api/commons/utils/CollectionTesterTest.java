package org.nextprot.api.commons.utils;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

public class CollectionTesterTest {

    @Test
    public void foundValidElement() throws Exception {

        List<Person> persons = Arrays.asList(new Person("bob", 23), new Person("jon", 29), new Person("loki", 450));

        CollectionTester<Person, String> tester = new CollectionTester<Person, String>(persons) {

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
            protected boolean isEquals(Person element, Person expectedElement) {

                return expectedElement.getName().equals(element.getName()) && expectedElement.getAge() == element.getAge();
            }
        };

        Assert.assertTrue(tester.contains(mockPerson("jon", 29)));
    }

    @Test
    public void foundValidElements() throws Exception {

        List<Person> persons = Arrays.asList(new Person("bob", 23), new Person("jon", 29), new Person("loki", 450));

        CollectionTester<Person, String> tester = new CollectionTester<Person, String>(persons) {

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
            protected boolean isEquals(Person element, Person expectedElement) {

                return expectedElement.getName().equals(element.getName()) && expectedElement.getAge() == element.getAge();
            }
        };

        Assert.assertTrue(tester.contains(Arrays.asList(mockPerson("jon", 29), mockPerson("bob", 23))));
    }

    private Person mockPerson(String name, int age) {

        Person person = Mockito.mock(Person.class);

        Mockito.when(person.getName()).thenReturn(name);
        Mockito.when(person.getAge()).thenReturn(age);

        return person;
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