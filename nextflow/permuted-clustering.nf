#!/usr/bin/env nextflow

deliverableDir = 'deliverables/' + workflow.scriptName.replace('.nf','')

nGroups = 2
minGroupSize = 3
maxGroupSize = 5

process build {
  echo true
  cache false
  output:
    file 'jars_hash' into jars_hash
    file 'classpath' into classpath
    
  """
  set -e
  current_dir=`pwd`
  cd ../../../..
  ./gradlew build
  ./gradlew printClasspath | grep CLASSPATH-ENTRY | sort | sed 's/CLASSPATH[-]ENTRY //' > \$current_dir/temp_classpath
  for file in `ls build/libs/*jar`
  do
    echo `pwd`/\$file >> \$current_dir/temp_classpath
  done
  cd -
  touch jars_hash
  for jar_file in `cat temp_classpath`
  do
    shasum \$jar_file >> jars_hash
  done
  cat temp_classpath | paste -sd ":" - > classpath
  """
}

jars_hash.into {
  jars_hash1
  jars_hash2
}
classpath.into {
  classpath1
  classpath2
}

process generateData {
  cache 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file classpath1
    file jars_hash1
  output:
    file "generated$i" into data
    
  """
  set -e
  java -cp `cat classpath` -Xmx2g matchings.PermutedClustering \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
    --model.nGroups $nGroups \
    --model.groupSize $i \
    --engine Forward
  mv samples generated$i
  """
}

process runInference {
  cache 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file data from data.collect()
    file classpath2
    file jars_hash2
  output:
    file "generated$i" into samples
    file "runtime$i" into runtime
  """
  set -e 
  tail -n +2 generated${i}/observations.csv | awk -F "," '{print \$2, ",", \$3, ",", \$4}' | sed 's/ //g' > data.csv
  java -cp `cat classpath` -Xmx2g matchings.PermutedClustering \
    --initRandom 123 \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
    --model.nGroups $nGroups \
    --model.groupSize $i \
    --model.observations.file data.csv \
    --engine PT \
    --engine.nScans 2_000 \
    --engine.nThreads MAX \
    --engine.nChains 8
  mv samples generated$i
  mv monitoring runtime$i
  """   
}

process calculateESS {
  input:
    each i from minGroupSize..maxGroupSize
    file samples from samples.collect()
    file runtime from runtime.collect()
  publishDir deliverableDir, mode: 'copy', overwrite: true  

  """
  #!/usr/local/bin/Rscript
  
  nGroups = $nGroups
  groupSize = $i
  from_vertex = 1
  to_vertex = 2
  
  rt_sum = read.table("runtime${i}/runningTimeSummary.tsv",sep='\t',header=TRUE)
  dur = rt_sum[2,'V2']*0.001

  data <- read.csv("generated${i}/permutations.csv")

  x = rep(0,as.integer(dim(data)[1]/(groupSize*nGroups)))
  k = 0
  for (i in 1:as.integer(dim(data)[1]/(groupSize*nGroups))) {
    for (j in 1:groupSize) {
      if (j == from_vertex+1 & as.integer(data[k+j,'value']) == to_vertex) {
        x[i] = 1
      }
    }
    k = k + groupSize*nGroups
  }

  N = length(x)
  v_up = sum((x-sum(x)/N)^2)/(N-1)
  I = x[1:sqrt(N)]
  batch_size = sqrt(N)
  incr = floor(sqrt(N))
  up_idx = floor(sqrt(N))
  num_batch = N%/%incr
  I = rep(0,num_batch)
  i = 1

  while (i<=num_batch) {
    x_batch = x[(up_idx-incr+1):up_idx]
    I[i] = mean(x_batch)
    up_idx = up_idx + incr
    i = i + 1
  }
  
  M = length(I)
  v_down = sum((I-sum(I)/M)^2)/(M-1)
  ess = v_up/v_down*sqrt(N)
  ess_per_sec = ess/dur
  
  cat(paste("ess_per_sec",ess_per_sec),file="../../../deliverables/permuted-clustering/ess_per_sec.txt",sep="\t",append=TRUE)
  """
}