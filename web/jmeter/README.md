# Apache JMeter

The Apache JMeter™ is designed to load test functional behavior and measure performance.

https://jmeter.apache.org/

# JMeter testing plans

All plans should be defined via jmeter GUI and saved in plans/ directory.
Use the jmeter GUI to define jmeter plans that test a specific scenario.

# Executing plans

Read the best practices https://jmeter.apache.org/usermanual/best-practices.html#lean_mean

YOU SHOULD EXECUTE PLANS VIA NON-GUI JMETER MODE !!!!!

Some plan may need to tune JVM before running jmeter:

`export JVM_ARGS="-Xms1024m -Xmx4G"`

## a real example:

jmeter -n -t plans/on-alpha-querying-titin-by-5-users-twice.jmx -l /tmp/titin.out -e -o /tmp/report

## another example with parameters

jmeter -n -t plans/on-alpha-querying-annotations.jmx -Jthreads=1 -Jcount=1 -Jentry=NX_P52701 -Joutputdir=/tmp
