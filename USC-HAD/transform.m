function transformX
  subjects = 14
  activities = 12
  trials = 5

  if ~exist('processed_data', 'dir')
    mkdir('processed_data');
  end
  
  samples = []
  labels = []
  
  for s = 1:subjects
    for a = 1:activities
      for t = 1:trials
        clear activity
        clear activity_number
        clear age
        clear date
        clear height
        clear sensor_location
        clear sensor_orientation
        clear sensor_readings
        clear subject
        clear title
        clear trial
        clear version
        clear weight
        disp(strcat('Subject', int2str(s), '/a', int2str(a), 't',  int2str(t), '.mat\n'))
        load(strcat('Subject', int2str(s), '/a', int2str(a), 't',  int2str(t), '.mat'))
        samples = [samples; sensor_readings(:,1:3)]
        labels = [labels ones(1, size(sensor_readings)(1))*a]
      end
    end
  end
  
  dlmwrite('processed_data/samples.csv', samples);
  dlmwrite('processed_data/labels.csv', labels);
end