Introducing Iris: the VAO SED Analysis application

${buildDate}

We release the ${project.version} version of Iris, the VAO application for
analysis of spectral energy distributions (SEDs).  Iris can retrieve
SEDs from the NASA Extragalactic Database (NED) or read the user's SED
data from file.  Iris can build and display SEDs, allow the user to select
particular data points for analysis, and fit models to SEDs.

The components of Iris have been contributed by members of the VAO.
NED is a service provided by IPAC at Caltech.  Specview, contributed
by STScI, provides a GUI for displaying SEDs, as
well as defining a model and setting its starting parameter values.
Sherpa, contributed by the Chandra project at the Harvard-Smithsonian
CfA, provides a library of models, fit statistics, and optimization
methods for analyzing SEDs.

SED Builder allows to build SED instances combining data from several sources.

Communication between Specview and Sherpa is managed by a SAMP
connection.  Specview packages SED data, model definitions and
starting parameter values and sends them to Sherpa whenever a fit to a
SED needs to be done.  The goal is to seamlessly combine the power of
Specview's GUI and data manipulation functions with Sherpa's robust
modeling and fitting functions, and also provide easy access to NED's
extensive database of extragalactic SEDs.

Iris provides interoperability features in input (import data from other VO
enabled applications) and output (export and SED to VO enabled applications).


Where To Find Iris:
===================

Tarballs containing the Iris software package can be found at the
following website:

http://cxc.cfa.harvard.edu/contrib/sed/

The tarball is named: 

iris-${project.version}-<plat>-<arch>.tar.gz

where <arch> = "i386" or "x86_64"
      <plat> = "unix" or "macosx"

The tarball includes Sherpa, a new Sherpa SAMP client, Specview, and
SedImporter.

System Requirements:
====================

o  Java 6 (preferably Oracle JRE, http://www.java.com)

o  Free Disk Space
     - tarball    ~64MB
     - unpacked   150-160MB


Installing Iris ${project.version}
===================

On Linux:
---------

On Linux machines, first untar the tarball:

% tar -zxf iris-${project.version}-unix-<arch>.tar.gz

At this point, Iris is now installed.

On Mac:
-------

On a Mac, first untar the tarball:

% tar -zxf iris-${project.version}-macosx-<arch>.tar.gz

At this point, Iris is now installed.


Running the Iris smoke test
===========================

The Iris smoke test is a good indication that the software runs
smoothly on your machine.  It will test whether a SAMP connection can
be established and that each SAMP agent can communicate successfully.

To begin the smoke test, start the smoketest script in a terminal
window (Linux or Mac):

% <basedir>/Iris smoketest

The terminal output will provide a record of the test and indicate a
successful termination or failed attempt.


Running Iris ${project.version}
================

The components of Iris are now unified in the
${project.version} release. Iris and Sherpa run simultaneously and
communicate with each other via a SAMP hub.

To begin, first start Iris in a new terminal window (Linux or Mac):

% <basedir>/Iris

Or 

Open a folder manager window on Linux or the Finder on Mac.  Navigate
to the Iris ${project.version} directory and double-click on the "Iris"
executable.


Iris can be shut down by clicking the "File->Exit" option in the File
menu.


The SED Builder
===============

This component of the VAO Iris SED Tool allows users to convert their data,
stored in non-standard formats, to IVOA standard documents so that they can be
used by Iris.

It also allows users to fetch SEDs from the NED SED service.

The user can build a SED out of any number of "segments": each segment
being a whole spectrum, a single photometric point, or a set of
photometric points.

SED Builder can be used in GUI mode.  It is also possible to save a "setup
file" that contains all the information needed by the tool in order to
convert similar files in "batch mode" from the command line.

In the example folder, please find the files 3c273.dat and 3c273.csv,
which are an ASCII and a CSV representation of the 3c273.xml file.
These files can be used to test SED Builder.

To run the SED Builder in batch mode from the command line, provide
the following arguments, in this order:

        setup_file The file which contains the importing setup(s)

        output_file The output file that will contain the new SED

        format The format in which the new SED file must be written. Choose
               between 'vot' and 'fits'. This argument is optional. If omitted
               it will default to 'vot' (VOTABLE).

For example:

% <basedir>/Iris builder setup.ini outputfile.vot vot

Batch mode can be used from the command line, to apply the same setup
to a number of input SED files, and to write out compliant versions of all
the input files.

Analyzing Sample Data from NED
==============================

The Iris ${project.version} package includes sample files in the "examples"
directory.  These are updated SED files that the user could download
by searching NED for SEDs of the relevant objects (e.g., 3C 273, 3C
295, etc.)  Iris can retrieve these SEDs from NED, or read them from
the local disk, when the user has saved the NED SED to file.

The sample ASCII tables in the "examples" directory can be converted
to an SED using SED Builder:

examples/
  3c273.csv
  3c273.dat


The sample NED SED files in the "examples" directory are:

examples/
  3c273.xml
  3c295.xml
  3c66a.xml
  m31.xml
  m87.xml
  ngc1068.xml
  ngc4151.xml


or, read in a SED from NED when starting Iris;

% <basedir>/Iris \
    "http://vo.ned.ipac.caltech.edu/services/accessSED\?REQUEST=getData\&TARGETNAME=3c273"

or, retrieve a SED from NED with SED Builder, by running Iris;

or, download the SED into a file with wget, and start Iris;

% wget -O 3c273.xml \
    'http://vo.ned.ipac.caltech.edu/services/accessSED?REQUEST=getData&TARGETNAME=3c273'

% <basedir>/Iris

To analyze a SED in Iris, start Iris, as instructed in the section
above, "Running Iris ${project.version}".

In this section we will illustrate Iris usage with the SED of 3C 273.
To read the SED into Iris, click the
"Load File" button. After clicking on "Browse", select the "examples"
directory, and then select "3c273.xml".

Click on "Load Spectrum/SED".

The SED of 3C 273 will be plotted, in
units of Jy vs. Hz.  The plot can be resized by clicking and dragging
on the lower-right corner of the plot window.  Different units can be
displayed by clicking the "Units" button.  This opens a menu of
different units that can be selected for the x- and y-axes
respectively--for example, photon flux (photons/s/cm^2/Angstrom)
vs. wavelength (Angstroms).  After the new units have been selected,
click the "Apply" button to recreate the plot, using the new units.
Valid photometric points with an associated uncertainty are displayed
as black squares.  Those points that do not have an associated
uncertainty are shown as magenta diamonds.

However, to do a fit, it is unnecessary to change units; in fact
Iris will always do the fit in a set of standard units, of
photons/s/cm^2/Angstrom vs. Angstrom.  These units are used for the
fit, no matter what units the user has chosen for display.

Fitting in Iris will open up a new set of menu boxes that allow the
user to define a model, set initial parameter values and ranges, and
see fit results at the end.  To fit a model to a SED, click the "Fitting Tool"
button on the Iris Desktop to open the component dialog box.

The component dialog box is populated with a powerlaw model component
by default and the model expression input field is populated with
"c1".  Users are free to add and remove model components as needed.
The model component naming convention uses an ordered component list
from top to bottom starting with "c1".  Removing various model
components from the list will cause remaining components to
potentially be renamed according to their position in the list.

To see the list of available models, click "Add".  Each time the user
clicks "Add", the list of models comes up; models are arranged in two groups:
preset components and custom models. Custom models can be managed by using the
Custom Fit Models Manager tool from the desktop or the Tools menu.

When the user clicks on a
model in the list, that model is added to the list in the
component dialog box.  For example, to fit a broken power-law to 3C
273, click "brokenpowerlaw" in the list.  This will add a model
component in the dialog box, that looks like this:

2 brokenpowerlaw.c2 refer = 5000.0 ampl = 1.0 index1 = 0.1 index2 = -0.1

"brokenpowerlaw" is the type of model, broken power-law.  "c2" is the
name of this particular instance of the broken power-law model.  (It
is in fact possible to define a model expression with more than one
model, even more than one model of the same type; the names given to
instances of each model allows Iris to distinguish between them.)
"index1", etc. are the names of the model parameters, with their
default starting values listed.

The model expression now needs to be defined.  For an expression
containing one model, it is trivial.  In the box labeled "Model
expression", clear any text that may be in the box, and type "c2".
Iris now knows it will fit the model "c2" to the 3C 273 SED.  (When
multiple models are selected, they can be combined in arbitrary model
expressions with arithmetic operators, in the "Model expression" box.)

Even for a single component model, the model expression *must* be
defined.  If it is not, an error will be raised to say that no model
is defined.

A model component's parameters can be edited, to change the starting
parameter values.  In the dialog box, click on the line listing
"brokenpowerlaw.c2" as a model.  Then click the "Edit" button below.
This will raise a new dialog box, in which starting values for the
parameters can be edited.  For example, in the box for "index1",
change the entry to "1.0"; then change "index2" to "0.5.  These
parameter values will also be updated in the component dialog box.

To fit, now press "Fit" in the component dialog box.  This will launch
a new window, with options to select fitting method and statistic.
The default fitting method is "neldermead", and the default statistic
is "leastsq".  We advise doing an initial fit with these defaults, as
they are a robust combination for doing fits over a wide range of
heterogeneous SED data.

Least-squares fitting of course has the limitation that measured
errors on the data points are not taken into account.  In essence, all
data points are weighted equally.  While this should be fine for an
initial fit, the fit may then be refined with a statistic to take
measured errors into account.  To do this, select the "chi2datavar"
statistic.  This is the chi-squared statistic, with variances taken
from the data.  Since measured errors are provided with the input SED,
these measured errors are fed to the chi-squared statistic to use as
the variance.

To improve the fit, examine data points that would seem to unduly bias
the fit; define a filter by clicking on "Select Range"; add new model components, if necessary; and
then fit again.  (The fitting method may left at neldermead.  The
other options are "levmar"--an implementation of the
Levenberg-Marquardt algorithm; and "moncar"--a Monte Carlo method for
finding the global minimum.  For typical fits to SED data, either
neldermead or levmar should be robust, and fast, methods for fitting.)

When fitting with chi-squared, additional fit results are returned.
In addition to "Final fit statistic", which is the chi-squared value
for the best fit, the reduced chi-squared, and the probability that
the fit is consistent with the data, are also calculated.

The reduced statistic value and probability are calculated only for
chi-squared, and for cstat (a maximum likelihood statistic with a
chi-squared-like distribution).  The reduced statistic and probability
are not meaningful for leastsq and cash, so are not calculated for these
statistics--to indicate this, the values are set to "nan" when they
cannot be calculated for least-squares or cash.

(Similarly, confidence limits are calculated only for the chi-squared
and cstat statistics.  The confidence function does not calculate
limits when the fit was done with least-squares or cash, and will
return an error message if am attempt is made to calculate limits with
these statistics.)

When the fit is complete, the model and SED will be overplotted in the
plot window.  The final statistic value will be shown as "statval" in
the "Fit" window.

Finally, the parameter values are updated back in the component dialog
box.

It is then possible to refine the model by setting new parameter
values, or by adding new model components.  Press "Dismiss" in the
"Fit" window to dismiss it.  Then, back in the component dialog box,
press "Add" again to add another model component.  As a very simple
example, add a "const1d" model.  A new line will appear in the dialog
box:

3  const1d.c3 c0 = 1.0

In the "Model expression" box, define a new model that is the sum of
the new constant model, plus the broken power-law: type "c2 + c3" in
the box.  The next time a fit is done, the model fitted will be the
sum of the constant level plus the broken power-law.

Model components can be added and deleted (with the "Delete" button),
and model parameters edited, as outlined above.  The user may iterate
through this process, until satisfied with a model that well fits the
SED.

To calculate 4 sigma confidence limits on the best-fit parameter
values, press the "Fit" button and click on the "Confidence" tab.  The
fit has to have been done with the chi-squared statistic, as stated
above; if this is not true, an error message states that the
confidence function cannot be called for the current statistic (i.e.,
cannot be used with least-squares or cash).  Assuming that the
statistic selected for the fit was "chi2datavar", press "Start" to
begin the confidence calculation.  When confidence completes, the
upper and lower bounds are displayed in the table.  The upper and
lower bounds will also appear the model parameter widgets below the
parameter value.  These widgets are accessible from the Model
component list by selecting a component and pressing "Edit".

To calculate different confidence intervals, input a sigma value and
press "Return".  Run the confidence calculation on the new sigma value
by hitting "Start".  The new confidence limits should appear in the
confidence table.  The confidence calculation can be aborted by
hitting the "Stop" button.


=============
CUSTOM MODELS
=============

Iris 1.1 allows the user to define custom models at runtime, using the
Custom Model Manager GUI.  This directory contains example files that
can be read into Iris 1.1 as user, table and template models via the
GUI.

User Model:
-----------

Here are example settings for installing the user model in the Custom
Model Manager.

Component ID:	my_py_powlaw

Click "Python Function" radio button.

<your_path_to_file>/mypowlaw.py

Function Name: mypowlaw

Names: ref,ampl,index

Values: 5000,1.0,-0.5

Mins: 5000,1e-38,-10

Maxs: 5000,3e38,10

Fixed: True,False,False


Notes:

The Component ID identifies the custom model for storage in Iris.  Any
arbitrary string would do in this example.

The "Python Function" radio button identifies the custom model as a
function that will be imported from Python.

In the next text box, type in "<your_path_to_file>/mypowlaw.py" --
where <your_path_to_file> is the directory where mypowlaw.py, and the
other example files, have been installed.

In the "Function Name" box, type the name of the Python function to be
added as a user model.  For example, within mypowlaw.py, there is a
function definition:

def mypowlaw(p, x):

that implements the power-law in Python.  So, type "mypowlaw" as the
name of the user function here.  (p and x are the arrays of parameter
values and x-values respectively.)

In the "Names" box, type in the names of the parameters, separated by
commas. 

In the "Values" box, type in the initial parameter values listed
above, separated by commas.

In the "Mins" box, type in the minimum allowed values, separated by
commas.

In the "Maxs" box, type in the maximum allowed values, separated by
commas.

In the "Fixed" box, type in Boolean values, indicating whether
parameters are fixed at the initial value during the fit.  (True =
fixed at starting value, False = allowed to vary during the fit.)

Click "Install Model Component" to install the model as a custom
model.  The next time the Fitting Tool is opened, the model can be
selected from the menu of custom model components, under Add ->
Custom Model Components -> functions -> my_py_model


Table Model:
------------

Here are example settings for installing the table model in the Custom
Model Manager.

Component ID:	my_sedtab

Click "Table" radio button.

<your_path_to_file>/sed_temp_data.dat

Function Name:

Names: ampl
 
Values: 1.0

Mins: 1e-38

Maxs: 3e38

Fixed: False


Notes:

The Component ID identifies the custom model for storage in Iris.  Any
arbitrary string would do in this example.

The "Table" radio button identifies the custom model as a table read
from file.  An amplitude parameter can be varied during the fit (i.e.,
a scale factor applied to the tabular data).

In the next text box, type in "<your_path_to_file>/sed_temp_data.dat" --
where <your_path_to_file> is the directory where sed_temp_data.dat, and the
other example files, have been installed.  The table should be an
ASCII file with two columns of numbers as the table.

Leave the "Function Name" box blank.  (In fact, for table models the
user is not allowed to fill in this box.)

In the "Names" box, type in the names of the parameter(s), separated
by commas (unless there is only one parameter, of course).

In the "Values" box, type in the initial parameter value(s) listed
above.

In the "Mins" box, type in the minimum allowed value(s).

In the "Maxs" box, type in the maximum allowed value(s).

In the "Fixed" box, type in Boolean value(s), indicating whether
parameters are fixed at the initial value during the fit.

Click "Install Model Component" to install the model as a custom
model.  The next time the Fitting Tool is opened, the model can be
selected from the menu of custom model components, under Add ->
Custom Model Components -> tables -> my_sedtab


Template Model:
---------------

Here are example settings for installing the template model in the
Custom Model Manager.

Component ID:	my_sedtemp

Click "Template Library" radio button.

<your_path_to_file>/sed_templates.dat

Function Name:

Names: index,refer
 
Values: 0.0,5000

Mins: -0.5,5000

Maxs: 0.0,5000

Fixed: False,False


Notes:

The Component ID identifies the custom model for storage in Iris.  Any
arbitrary string would do in this example.

The "Template Library" radio button identifies the custom model as a
set of templates read from files.  The file sed_templates.dat is a
"table-of-contents" file, listing parameter values associated with
each template file.  The files 

sed_temp_index-0.00.dat
sed_temp_index-0.10.dat
sed_temp_index-0.25.dat
sed_temp_index-0.35.dat
sed_temp_index-0.50.dat

are ASCII files, each with two columns of numbers; each of these files
contains one template.  During the fit, the data are compared to each
template, to determine which template is the closest match to the
data.  The parameters associated with that template are returned as
the best-fit parameters.

In the next text box, type in "<your_path_to_file>/sed_templates.dat"
-- where <your_path_to_file> is the directory where sed_templates.dat,
and the other example files, have been installed.  

Please also edit this file to replace the string <YOUR DIRECTORY PATH
HERE> with the actual path to the directory where these example files
have been installed.  Otherwise, the templates will not be found or
read into Iris.

Leave the "Function Name" box blank.  (In fact, for template models
the user is not allowed to fill in this box.)

In the "Names" box, type in the names of the parameters, separated by
commas.  The names should match the names listed as parameters in the
sed_templates.dat file.  (Note that there must be at least two
parameters listed as columns in sed_templates.dat.  Also note that the
MODELFLAG column in this file is *not* a model parameter.)

In the "Values" box, type in the initial parameter values listed
above.

In the "Mins" box, type in the minimum allowed values.

In the "Maxs" box, type in the maximum allowed values.

In the "Fixed" box, type in Boolean values, indicating whether
parameters are fixed at the initial value during the fit.

Click "Install Model Component" to install the model as a custom
model.  The next time the Fitting Tool is opened, the model can be
selected from the menu of custom model components, under Add ->
Custom Model Components -> templates -> my_sedtemp
