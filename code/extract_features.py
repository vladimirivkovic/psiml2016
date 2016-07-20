import numpy as np
import scipy
import scipy.stats

def peak_to_peak(x):
    return max(x) - min(x)
def percentile10(x):
    return np.percentile(x, 10)
def percentile25(x):
    return np.percentile(x, 25)
def percentile50(x):
    return np.percentile(x, 50)
def percentile75(x):
    return np.percentile(x, 75)
def percentile90(x):
    return np.percentile(x, 90)
def interquartile(x):
    return percentile75(x) - percentile25(x)
def autocorrelation(x):
    m = np.mean(x)
    l = len(x)
    num = 0
    den = 0

    for i in range(0, l-1):
        num += (x[i] - m)*(x[i+1] - m)
    for i in range(0, l):
        den += (x[i] - m)*(x[i] - m)

    return num/den
def skewness(x):
    m = np.mean(x)
    l = len(x)
    num = 0
    den = 0

    for i in range(0, l):
        num += (x[i] - m)**3
    for i in range(0, l):
        den += (x[i] - m)*(x[i] - m)

    return (num/l)/((den/l)**1.5)
def kurtosis(x):
    m = np.mean(x)
    l = len(x)
    num = 0
    den = 0

    for i in range(0, l):
        num += (x[i] - m)**4
    for i in range(0, l):
        den += (x[i] - m)*(x[i] - m)

    return (num/l)/((den/l)**3) - 3
def power(x):
    p = 0

    for e in x:
        p += e*e

    return p
def log_energy(x):
    p = 0

    for el in x:
        if el != 0:
            p += np.log(el*el)

    return p
def peak_intensity(x):
    return 0
def zero_crossing(x):
    z = 0

    for i in range(0, len(x)-1):
        if x[i+1]*x[i] < 0:
            z += 1

    return z

def generate_features():
    features = [None] * 17
    features[0] = sum
    features[1] = np.mean
    features[2] = np.std
    features[3] = scipy.stats.variation
    features[4] = peak_to_peak
    features[5] = percentile10
    features[6] = percentile25
    features[7] = percentile50
    features[8] = percentile75
    features[9] = percentile90
    features[10] = interquartile
    features[11] = autocorrelation
    features[12] = skewness
    features[13] = kurtosis
    features[14] = power
    features[15] = log_energy
    features[16] = max
#    features[17] = zero_crossing
#    features[18] = numpy.correlate
    return features

def get_features(data, features):
    ret = [None] * len(features)

    if np.mean(data) == 0:
        data[0] += 0.001

    for i in range(0, len(features)):
        ret[i] = features[i](data)

    return ret

def main():
    x = [[1,2,3],[2,3,4]]
    get_features(x)

if __name__ == '__main__':
    main()
