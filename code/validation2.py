from sklearn import svm
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score
from sklearn.cross_validation import cross_val_score

import numpy as np
from numpy import genfromtxt, savetxt

import validation
import extract_features

def compress_activity(a):
    if a == 2 or a == 3:
        return 1
    elif a == 9 or a == 10:
        return 8
    return a

def calculate_featuresX(filename, a, sw):
    # All samples for activity
    X = genfromtxt(filename, delimiter=' ')
    i = 0
    # Get functions for features
    features = extract_features.generate_features()
    # Calculated features matrix
    outf = None
    while i + sw < X.shape[0]:
        fx = extract_features.get_features(X[i:i+sw,0], features)
        fy = extract_features.get_features(X[i:i+sw,1], features)
        fz = extract_features.get_features(X[i:i+sw,2], features)
        # Concatenate vectores for axis
        feat = np.concatenate((fx, fy, fx, [a]))
        if type(outf).__module__ != np.__name__:
            outf = feat
        else:
            # Concatenate matrices
            outf = np.vstack((outf, feat))
        # Move window
        i += sw/2
    savetxt('../data/huawei-p7/' + filename.split('/')[-1].split('.')[0] + 'X.txt', outf, delimiter=',')

def calculate_features(filename, a, sw):
    # All samples for activity
    X = genfromtxt(filename, delimiter=',')
    i = 0
    # Get functions for features
    features = extract_features.generate_features()
    # Calculated features matrix
    outf = None
    while i + sw < X.shape[0]:
        fx = extract_features.get_features(X[i:i+sw,0], features)
        fy = extract_features.get_features(X[i:i+sw,1], features)
        fz = extract_features.get_features(X[i:i+sw,2], features)
        # Concatenate vectores for axis
        qa = compress_activity(a)
        feat = np.concatenate((fx, fy, fx, [qa]))
        if type(outf).__module__ != np.__name__:
            outf = feat
        else:
            # Concatenate matrices
            outf = np.vstack((outf, feat))
        # Move window
        i += sw/2
    savetxt('../data/usc/' + filename.split('/')[-1].split('.')[0] + 'X.csv', outf, delimiter=',')

def generate_files(sw):
    # Number of activities
    activities = 12
    path = '../USC-HAD/processed_data/samples'

    for i in range(1, activities+1):
        calculate_features(path + str(i) + '.csv', i, sw)

def generate_filesX(sw):
    activities = ['running', 'walking', 'standing', 'upstairs', 'downstairs']

    for i in range(0, len(activities)):
        calculate_featuresX('../data/huawei-p7/' + activities[i] + '.txt', i, sw)

def merge_activity_files():
    activities = 12
    X = None

    for i in range(1, activities+1):
        x = genfromtxt('../data/usc/' + 'samples' + str(i) + 'X.csv', delimiter=',')
        if type(X).__module__ != np.__name__:
            X = x
        else:
            X = np.concatenate((X, x))

    savetxt('../data/usc/all.csv', X, delimiter=',')

def merge_activity_filesX():
    activities = ['running', 'walking', 'standing', 'upstairs', 'downstairs']
    X = None

    for i in range(0, len(activities)):
        x = genfromtxt('../data/huawei-p7/' + activities[i] + 'X.txt', delimiter=',')
        if type(X).__module__ != np.__name__:
            X = x
        else:
            X = np.concatenate((X, x))
    print(X.shape)

    savetxt('../data/huawei-p7/all.csv', X, delimiter=',')

def validate(filename):
    samples = genfromtxt(filename, delimiter=',')
    # Total number of samples
    l = samples.shape[0]
    # Random permutation of [0:l]
    rp = np.random.permutation(l)
    # Permutate samples
    samples = samples[rp, :]

    classificators = {'SVM': svm.SVC(C=1000, gamma=1e-5),
                      'DecisionTree': DecisionTreeClassifier(random_state=0),
                      'RandomForest': RandomForestClassifier(n_estimators=20),
                      'ExtraTrees' : ExtraTreesClassifier(n_estimators=25, random_state=0),
                      'kNN' : KNeighborsClassifier(n_neighbors=12)
                      }

    to = int(l*0.7)

    validation.trainAndValidate(classificators, samples[:, 0:-1], samples[:, -1],
     samples[0:to, 0:-1], samples[0:to, -1],
     samples[to:, 0:-1], samples[to:, -1])

if __name__ == '__main__':
    # generate_files(512)
    # merge_activity_files()
    # validate('../data/usc/all.csv')
    generate_filesX(128)
    merge_activity_filesX()
    validate('../data/huawei-p7/all.csv')
