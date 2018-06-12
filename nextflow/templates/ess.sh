#!/usr/local/bin/Rscript

#needed in the future
if (!require("dplyr")) {
  install.packages("dplyr", repos="http://cran.rstudio.com/") 
  library("dplyr")
}


nGroups = $nGroups
groupSize = $i
sampler_d = paste("group-size", "$i", "/samplerDir/", sep='')
data <- read.csv(paste(sampler_d, "samples/permutations.csv", sep=''))
from_vertex = 1
to_vertex = 2

getTime <- function(){
  timesDir = paste(sampler_d, "monitoring/runningTimeSummary.tsv", sep='')
  rt_sum = read.table(timesDir, 
                      row.names=c("ProcTime", "SampTime"))
  return (rt_sum["SampTime", 2]*0.001)
}

fromTo <- function(from_vertex, to_vertex){
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
 return (x)   
}

computeESS <- function(data = data, from, to){
  #TODO: adapt for more tfs
  x = fromTo(from, to)
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

  return (ess)
}

SAMPLING_TIME = getTime()



testFunctionArgs = list(c(1,2), c(2,1))

groupSize_list = c()
testFunctionNames = c()
ess_per_sec_list = c()
for (args in testFunctionArgs){
  i = args[1]
  j = args[2]
  
  ess_per_sec_list = c(ess_per_sec_list, computeESS(data, i,j) / SAMPLING_TIME)

  name = paste0("from", toString(i), "to", toString(j))
  testFunctionNames = c(testFunctionNames, name)

  groupSize_list = c(groupSize_list, groupSize)
}


df <- data.frame(groupSize_list, 
                testFunctionNames, 
                ess_per_sec_list)

names(df) <- NULL


write.csv(df, file = "group-size$i/ess_per_sec.csv", row.names=FALSE)
