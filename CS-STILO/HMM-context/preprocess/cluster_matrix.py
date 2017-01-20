import glob
from cluster import *
import math
from random import shuffle
import random
from sklearn import decomposition
import time

def get_all_calls(call_set):
	f = open("complete")
	f.readline()
	line = f.readline()
	calls = line.split(",");
	for i in range(0, len(calls)-1): # string after ",", minus 1
#		print calls[i]
		call_set.append(calls[i])
#	print "over"




def get_all_vectors(all_vector):
	f = open("complete")
	line = f.readline()
	m_size = int(line)
	print "matrix size: ", m_size
	# all the calls
	line = f.readline()
	#print line

	#matrix of probs
	for i in range(0, m_size):

		line = f.readline()
		#print line
		prob_str = line.split("\t")
#	print prob_str[0]
#	print prob_str[112]
#	print prob_str[113]
	
		tmp_vector = []
		for j in range(0, m_size):
			tmp_vector.append( float(prob_str[j]) )
			
		all_vector.append(tmp_vector)
#		if sum(tmp_vector) < 14:
#			print "less than 14", sum(tmp_vector), len(segment)


def output(list_list, fname):
	f = open(fname, 'w')
	for list_i in list_list:
		for item in list_i:
			f.write(str(item)+",")
		f.write("\n")


def cosine_distance(x, y):
#	print sum(x)
#	print sum(y)
	if sum(x) == 0 or sum(y) == 0:
		print "cosine distance is 0!", sum(x), sum(y)

	up = 0
	down_x = 0
	down_y = 0
	for i in range(0, len(x)):
		up += x[i]*y[i]
	for i in range(0, len(x)):
		down_x += x[i]*x[i]
	for i in range(0, len(y)):
		down_y += y[i]*y[i]

#	print float(up), up
#	print down_x, down_y
#	print float(math.sqrt(down_x) * math.sqrt(down_y)), math.sqrt(down_x) * math.sqrt(down_y)	
	
	distance =1.0 - float(up) / float(math.sqrt(down_x) * math.sqrt(down_y))	
#	print "distance is: ", distance, float(up)
	return distance


def eu_distance(x, y):
        if sum(x) == 0 or sum(y) == 0:
                print "eu distance is 0!", sum(x), sum(y)

	d = 0;
	for i in range(0, len(x)):
		d += (x[i]-y[i])*(x[i]-y[i])

#	print math.sqrt(d)
	return math.sqrt(d)


def do_Hierarchical(all_vector, threshold):

	cl = HierarchicalClustering(all_vector, eu_distance)
	print "using threshold: ", str(threshold)
	clusters = cl.getlevel(threshold)
	for i in range(0, len(clusters)):
		print "cluster "+str(i)+" size: ", len(clusters[i])
	
def my_equal(x, y):
	for i in range(0, len(x)):
		if x[i] != y[i]:
			return False
	return True


def do_KMeans(all_vector, num):
	print "clustering "+str(len(all_vector))+" vectors"
	for x in all_vector:
		if sum(x) == 0:
			print "found 0!"
	cl = KMeansClustering(all_vector, distance = eu_distance, equality = my_equal)

	clusters = cl.getclusters(num)

	for i in range(0, len(clusters)):
		print "cluster "+str(i)+" size: ", len(clusters[i])
		
#	verify(clusters)
	return clusters


def verify(clusters):
	print "verify clustering results"
	sum_within = 0.0
	count_within = 0.0

	for cluster_i in clusters:
		for i in range(0, len(cluster_i)-1):
			for j in range(i+1, len(cluster_i)):
				sum_within += eu_distance(cluster_i[i], cluster_i[j])
				count_within += 1
	print "within cluster average distance", sum_within/count_within


	sum_between = 0.0
	count_between = 0.0
	for i in range(0, len(clusters)-1):
		for j in range( i+1, len(clusters)):
			for vector_x in clusters[i]:
				for vector_y in clusters[j]:
					sum_between += eu_distance(vector_x, vector_y)
					count_between += 1
	print "between cluser average distance", sum_between/count_between


def find_index(X, prob_i, index_dict):
	for i in range(0, len(X)):
		if (X[i] == prob_i).all() and not index_dict.has_key(i):
			index_dict[i] = "true"
			return i
		else:
			continue
	return -1


def output_clustered_calls(call_set, X, clusters, index_dict):
	f = open("result-cluster", 'w')
	f.close()
	for i in range(0, len(clusters)):
		f = open("result-cluster", 'a')
		for prob_i in clusters[i]:
			index = find_index(X, prob_i, index_dict)
			call = call_set[index]
			f.write(call+",")
		f.write("\n")
		f.close()

	# write the verification file
	#f = open("clusters/veri", 'w')
	#veri_list = list(segment_list)
	#shuffle(veri_list)
	#i = 0
	#while i < len(veri_list)/3:
	#	segment = veri_list[i];
	#	for j in range(0, len(segment)):
	#		f.write(segment[j]+",")
	#	f.write("\n")
	#	i+=1



def main():
	#get call set
	call_set = []
	get_all_calls(call_set)

	print "size of call set: ", len(call_set)

	#get all vector for all segment
	all_vector = []
	get_all_vectors(all_vector)
	print "num of vectors: ", len(all_vector)
	print "size of each vector: ", len(all_vector[0])
	
#	print all_vector

	#clustering
#	shuffle(all_vector)
#	output(all_vector, "all_vector")
#	do_KMeans(all_vector, 10)

#	for vector in all_vector:
#		if sum(vector) < 1:
#			print sum(vector)
	
#	do_Hierarchical(all_vector, 0.1)
#	do_Hierarchical(all_vector, 0.3)
#	do_Hierarchical(all_vector, 0.5)
	
	print "reduce dimension..."
	pca = decomposition.PCA(n_components = 100)
	pca.fit(all_vector)
	X = pca.transform(all_vector)
	
#	print len(X), len(all_vector)
#	print len(X[0]), len(all_vector[0])
#	print X[0]

	print "pca components", pca.components_
	print "variance ratio", pca.explained_variance_ratio_
	
	start_time = time.time()

	clusters = do_KMeans(X, 455)

	print("--- time in seconds ---", (time.time() - start_time))

	#print list(clusters)
	#print X
	print "number of clusters: ", len(clusters)
	#print clusters[0]
	
	index_dict = dict()
	output_clustered_calls(call_set, X, clusters, index_dict)


main()
