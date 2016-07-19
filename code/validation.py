from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from numpy import genfromtxt
from sklearn import svm
from sklearn.metrics import accuracy_score
from sklearn.cross_validation import cross_val_score
import string
import os

def trainAndValidate(classificators, X, y, X_train, y_train, X_test, y_test):
    # Print table title
    print('-' * 34)
    print('{0:17} | {1:13}'.format('| ' + 'Classificator', 'Precision'.rjust(12)) + '|')
    print('-' * 34)
    # For each classificator, train and validate model
    for name, clf in classificators.items():
        # Cross-validation
        scores = cross_val_score(clf, X, y)
        print('| ' + '{0:15} | {1:10.2f}'.format(name, scores.mean() * 100)  + ' % |')
        # Validation with test samples
        clf.fit(X_train, y_train)
        y_pred = clf.predict(X_test)
        print('| ' +'{0:15} | {1:10.2f}'.format('Fit-Predict ', accuracy_score(y_test, y_pred) * 100) + ' % |')
        print('|' + '-' * 32 + '|')

def main():

    os.system("octave --eval loadData")
    # Cross-validation data
    X = genfromtxt('../processed_data/samples.csv', delimiter=',')
    y = genfromtxt('../processed_data/labels.csv', delimiter=',')

    # Train data. We use fit() and predict()
    X_train = genfromtxt('../processed_data/samples_train.csv', delimiter=',')
    y_train = genfromtxt('../processed_data/labels_train.csv', delimiter=',')
    # Test data
    X_test = genfromtxt('../processed_data/samples_test.csv', delimiter=',')
    y_test = genfromtxt('../processed_data/labels_test.csv', delimiter=',')

    classificators = {'SVM': svm.SVC(C=100, gamma='auto'),
                      'DecisionTree': DecisionTreeClassifier(random_state=0),
                      'RandomForest': RandomForestClassifier(n_estimators=20),
                      'ExtraTrees' : ExtraTreesClassifier(n_estimators=25, random_state=0),
                      'kNN' : KNeighborsClassifier(n_neighbors=15)
                      }
    # Train and validate classificators
    trainAndValidate(classificators, X, y, X_train, y_train, X_test, y_test)

if __name__ == "__main__":
    main()
