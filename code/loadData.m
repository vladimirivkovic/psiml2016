function loadData

  % Features used for experimenting. Check 'features.txt' for more.
  %
  % features = [1:6 17:19 38:40];
  % features = [1:19 38:40 201:207];
  % features = [202:205 10:12 4:6 7:15];

  % Indexes of features
  features = [1:80];

  % Loading training and test data. Raw data have 561 feature so we choose
  % only the most relevant features. For features info see 'features.txt'
  % Also load subjects that anticipated in aquiring data. We need subjects for
  % testing dataset aquired from only one subject
  [samples, labels, subjects] = getData('../data/X_train.txt', '../data/y_train.txt', '../data/subject_train.txt', features);
  [samples_t, labels_t, subjects_t] = getData('../data/X_test.txt', '../data/y_test.txt', '../data/subject_test.txt', features);


  % Create folder if doesn't exist
  if ~exist('../processed_data', 'dir')
    mkdir('../processed_data');
  end

  % Save extracted train data
  dlmwrite('../processed_data/samples_train.csv', samples);
  dlmwrite('../processed_data/labels_train.csv', labels);

  % Save extracted test data
  dlmwrite('../processed_data/samples_test.csv', samples_t);
  dlmwrite('../processed_data/labels_test.csv', labels_t);

  % Concatenate test and train data for cross-validation
  smpls = [samples; samples_t];
  lbls = [labels; labels_t];
  subs = [subjects; subjects_t];

  % Save train and test data in one file. Used in cross-validation
  dlmwrite('../processed_data/samples.csv', smpls);
  dlmwrite('../processed_data/labels.csv', lbls);

  % Subject-specific data. Used for validation on only one subject
  subj_no = 25;
  dlmwrite('../processed_data/samples_subject.csv', smpls(subs == subj_no, :));
  dlmwrite('../processed_data/labels_subject.csv', lbls(subs == subj_no, :));

end

function [samples, labels, subjects] = getData (x_train, y_train, subs, features)
  data = dlmread(x_train);
  samples = data(:, features);
  labels = dlmread(y_train);
  subjects = dlmread(subs);
end
