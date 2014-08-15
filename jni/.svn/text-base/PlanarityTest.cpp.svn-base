#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/boyer_myrvold_planar_test.hpp>
#include <jni.h>

using namespace boost;

#ifdef __cplusplus
extern "C" {
#endif

jboolean  Java_com_project_graplan_GraplanChallenge_planar
(JNIEnv *env, jobject obj, jintArray tails, jintArray heads)
{
	int i;
	typedef adjacency_list < vecS, vecS, undirectedS > UndirectedGraph;

	jsize number_of_edges = env->GetArrayLength(tails);

	jint tails_buf[number_of_edges];
	jint heads_buf[number_of_edges];

    env->GetIntArrayRegion(tails, 0, number_of_edges, tails_buf);
    env->GetIntArrayRegion(heads, 0, number_of_edges, heads_buf);

	UndirectedGraph undigraph;

	for( i = 0 ; i < number_of_edges; i++)
		add_edge((const int)tails_buf[i] , (const int)heads_buf[i] , undigraph);

	return boyer_myrvold_planarity_test(undigraph);
}

#ifdef __cplusplus
}
#endif
