# How to build Iris from sources

The following instructions assume you have conda installed.
Conda is part of the Anaconda distribution and can be easily installed
through the Miniconda minimal distribution.

````
$ git clone --recursive https://github.com/ChandraCXC/iris
$ conda create -n iris python=2.7 astropy=0.4.4 scipy
$ source activate iris
$ conda install -c https://conda.binstar.org/sherpa sherpa
$ pip install sampy
$ pip install astlib
$ cd sherpa-samp; python setup.py develop; cd ..
$ cd sedstacker; python setup.py develop; cd ..
````

You should also make sure that `sherpa-samp` is working.
After installing `sherpa-samp`, run `sherpa-samp` from the
command line. The program should start and listen for SAMP
connections. After a while the program times out and exits.
It's important that `sherpa-samp` does not exit with errors.

You can also run the Iris smoke test by:

````
$ cd iris/target
$ chmod u+x Iris
$ ./Iris smoketest
````

## How to run the unit and integration tests

### Without coverage analysis

````
$ mvn clean test # Unit tests
$ mvn clean test-compile failsafe:integration-test # Integration tests only
$ mvn clean verify # All tests
````

### With JaCoCo coverage analysis

````
$ mvn -Pjacoco test # Unit tests
$ mvn -Pjacoco verify # All tests
$ mvn -Pjacoco jacoco:report # generate report
````

Note that individual reports will be created in each individual submodule.

### With Sonar

A [[http://www.sonarqube.org/ | SonarQube]] instance must be running.
The configuration for the SonarQube instance must be placed into
the maven local `settings.xml` file for connecting with the database
backing the SonarQube instance.

````
$ mvn -Psonar install # All tests
$ mvn sonar:sonar
````