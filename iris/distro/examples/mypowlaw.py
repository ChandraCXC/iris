import numpy

def mypowlaw(p, x):
    arg = x / p[0]
    arg = p[1] * numpy.power(arg, p[2])

    return arg
