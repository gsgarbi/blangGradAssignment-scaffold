#!/usr/bin/env nextflow

// shoud be given as arguments:

nGroups = 1
minGroupSize = 3
maxGroupSize = 5

sampler =  workflow.scriptName.replace('.nf', '')

deliverableDirPath = "deliverables/$sampler/nGroups$nGroups"

process createDirs{
// where we will store our results
cache true
output:
file "$deliverableDirPath" into files
"""
mkdir -p $deliverableDirPath
"""
}


process build {
  cache true //originall false
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


process generateSamples {
  echo false
  cache true
  input:
    each i from minGroupSize..maxGroupSize

    file classpath2
    file jars_hash2
  output:
    file "group-size$i" into samples
  """
  set -e
  java -cp `cat classpath` -Xmx2g matchings.$sampler \
    --initRandom 123 \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
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
cache true

  input:
    each i from minGroupSize..maxGroupSize
    file gs from groupSizeFolders.collect()

 
  exec:
  ESStable = file ("$deliverableDirPath/ESStable.csv")
  ESStable.text = 'GroupSize TestFunction ESS/s\n'
  
  essCSVFile = file("$deliverableDirPath/group-size$i/ess_per_sec.csv")
  eachTXT = essCSVFile.getText().replace(",", " ")
  ESStable << eachTXT



}