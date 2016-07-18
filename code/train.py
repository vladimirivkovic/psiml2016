from sklearn.cross_validation import cross_val_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.tree import DecisionTreeClassifier
from numpy import genfromtxt
from sklearn import svm

X = genfromtxt('samples.csv', delimiter=',')
y = genfromtxt('labels.csv', delimiter=',')

clf = DecisionTreeClassifier(max_depth=None, min_samples_split=1, random_state=0)
scores = cross_val_score(clf, X, y)
print('DecisionTreeClassifier: ' + str(scores.mean()))


clf = RandomForestClassifier(n_estimators=20, max_depth=None, min_samples_split=1)
scores = cross_val_score(clf, X, y)
print('RandomForestClassifier: ' + str(scores.mean()))


clf = ExtraTreesClassifier(n_estimators=25, max_depth=None, min_samples_split=1, random_state=0)
scores = cross_val_score(clf, X, y)
print('ExtraTreesClassifier: ' + str(scores.mean()))

clf = svm.SVC(C=1.5, gamma=0.1)
scores = cross_val_score(clf, X, y)
print('SVM: ' + str(scores.mean()))
