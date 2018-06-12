#!/usr/bin/env nextflow

// shoud be given as arguments:
nGroups = 2
minGroupSize = 3
maxGroupSize =6

sampler =  workflow.scriptName.replace('.nf', '')

deliverableDirPath = "deliverables/$sampler/nGroups$nGroups"

process createDirs{
cache true
output:
file "$deliverableDirPath" into files
"""
mkdir -p $deliverableDirPath
"""
}

process build {
  cache false //originall false
  input:
  file f from files
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
  cache true //originally 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file classpath1
    file jars_hash1
  output:
    file "group-size$i" into data 
  """
  set -e
  java -cp `cat classpath` -Xmx2g matchings.$sampler \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
    --model.nGroups $nGroups \
    --model.groupSize $i \
    --engine Forward
    mkdir -p group-size$i/dataDir
    cp -r samples group-size$i/dataDir
  """
}


process generateSamples {
  echo false
  cache true
  input:
    each i from minGroupSize..maxGroupSize
    file data from data.collect()
    file classpath2
    file jars_hash2
  output:
    file "group-size$i" into samples
  """
  set -e
  tail -n +2 group-size${i}/dataDir/samples/observations.csv | awk -F "," '{print \$2, ",", \$3, ",", \$4}' | sed 's/ //g' > data.csv
  java -cp `cat classpath` -Xmx2g matchings.$sampler \
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
  mkdir -p group-size$i/samplerDir 
  cp -r samples group-size$i/samplerDir
  cp -r monitoring group-size$i/samplerDir
  """   
}



process calculateESS {
  echo false
  publishDir deliverableDirPath
  cache true
  input:
    each i from minGroupSize..maxGroupSize
    file sample from samples.collect()
  output: 
  file "group-size$i" into groupSizeFolders

  script:
  template 'ess.sh'

}

process putTogether {
echo false
cache false

  input:
    file groupSizeFolders
    
  exec:
  ESStable = file ("$deliverableDirPath/ESStable-${sampler}.csv")
  ESStable.text = 'GroupSize, TestFunction, ESS/s\n'
  
  //keep order:
  for (j in minGroupSize..maxGroupSize){
  essCSVFile = file("$deliverableDirPath/group-size$j/ess_per_sec.csv")
  eachTXT = essCSVFile.getText().replace('"', '')
  ESStable << eachTXT}
}
