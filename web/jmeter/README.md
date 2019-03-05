# Load testing with Apache JMeter

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

## Some examples:

* jmeter -n -t plans/examples/on-alpha-querying-titin-by-5-users-twice.jmx -l /tmp/titin.out -e -o /tmp/report
* jmeter -n -t plans/examples/on-alpha-querying-annotations.jmx -Jthreads=1 -Jcount=1 -Jentry=NX_P52701 -Joutputdir=/tmp

## The function view plan

In this scenario, all api calls done from the function view:

jmeter -n -t plans/testing-function-view.jmx -Jrandom_accessions_count=5
jmeter -n -t plans/testing-function-view.jmx -Jusers=5 -Jrampup=10 -Jaccessions_input=plans/input/accessions.txt
jmeter -n -t plans/testing-function-view.jmx -Jusers=10 -Jrampup=1 -Jrandom_accessions_count=10

Params (see also https://www.novatec-gmbh.de/en/blog/how-to-pass-command-line-properties-to-a-jmeter-testplan):
* api: the api (default=alpha-api.nextprot.org)
* users: the number of simulated users executing this plan (default=1)
* rampup (in seconds): the amount of time it takes for jmeter to execute all the users' tasks (default=10")
* ntimes: the number of times this plan is executed by each user (default=1)
* delay (in milliseconds): the pause duration between each api calls 
* delay_random (in milliseconds): the variable adding pause duration between each api calls 
* accessions_input: the file containing one entry accession by line
* random_accessions_count: the number of entry accessions randomly fetched from the api (if accessions_input is not defined) 
* output_dir: the output directory of summary reports testing-function-view.csv

We also could defined params for connect and response timeouts (actuals are 5" and 30")

## How to launch jvisualvm from distant machine (and redirect x11 to local machine)

Let's take the example of uat-web2 (alpha)

1. Connect with ssh (ssh -X npteam@uat-web2)
2. Execute jvisualvm from command line
3. As the GUI opens, you should select the correct jvm
4. run your jmeter plan and profile nextprot-api web app