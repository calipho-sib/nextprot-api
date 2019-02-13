# Apache JMeter

The Apache JMeter™ is designed to load test functional behavior and measure performance.

https://jmeter.apache.org/

# JMeter testing plans

All plans should be defined via jmeter GUI and saved in plans/ directory.
Use the jmeter GUI to define jmeter plans that test a specific scenario.

# Executing plans

YOU SHOULD EXECUTE PLANS VIA NON-GUI JMETER MODE !!!!! (https://jmeter.apache.org/usermanual/best-practices.html#lean_mean)

Some plan may need to tune JVM before running jmeter:

`export JVM_ARGS="-Xms1024m -Xmx4G"`

## an example:

jmeter -n -t plans/on-alpha-querying-titin-by-5-users-twice.jmx -l /tmp/titin.out -e -o /tmp/report

