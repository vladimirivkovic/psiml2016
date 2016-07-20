function transformX
  subjects = 14
  activities = 12
  trials = 5

  if ~exist('processed_data', 'dir')
    mkdir('processed_data');
  end
  
  for a = 1:activities
    samples = []
    labels = []
    for s = 1:subjects
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
        load(strcat('Subject', int2str(s), '/a', int2str(a), 't',  int2str(t), '.mat'));
        samples = [samples; sensor_readings(:,1:3)];
        labels = [labels ones(1, size(sensor_readings)(1))*a];
        %dlmwrite(strcat('processed_data/samples', int2str(a), '_',  int2str(s), '_', int2str(t), '.csv'), samples)
        %dlmwrite(strcat('processed_data/labels', int2str(a), '_',  int2str(s), '_', int2str(t), '.csv'), labels)
      end
    end
    dlmwrite(strcat('processed_data/samples', int2str(a), '.csv'), samples)
    dlmwrite(strcat('processed_data/labels', int2str(a), '.csv'), labels)
  end

end