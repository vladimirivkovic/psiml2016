import validation
import extract_features
import numpy as np
from numpy import genfromtxt, savetxt

def calculate_features(filename):
    sw = 128
    X = genfromtxt(filename, delimiter=',')
    i = 0
    steps = 0
    features = extract_features.generate_features()
    outf = None
    while i + sw < X.shape[0]:
        fx = extract_features.get_features(X[i:i+sw,0], features)
        fy = extract_features.get_features(X[i:i+sw,1], features)
        fz = extract_features.get_features(X[i:i+sw,2], features)
        feat = np.concatenate((fx, fy, fx))
        if outf == None:
            outf = feat
        else:
            outf = np.vstack((outf, feat))
        i += sw/2
        steps += 1
    savetxt(filename.split('.')[0] + 'X.csv', outf, delimiter=',')
    print(steps)

def main():
    activities = 12
    path = '../USC-HAD/processed_data/samples'

    for i in range(1, activities+1):
        calculate_features(path + str(i) + '.csv')

if __name__ == '__main__':
    main()
