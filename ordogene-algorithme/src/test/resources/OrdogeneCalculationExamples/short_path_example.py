################################
# Generate short path examples #
################################

# Goal : use Sage fonctionalities on graph
# to generate examples and tests for the
# Darwiners...

def generate_json(size):
    r"""
    Generate json text describing an experience of pathfinding.
    `size` will be the numbers of nodes in the graph thus also the
    number of entities in the Darwiners instance.
    """
    # Building random directed graph
    nb_edges = (3*size) // 2;
    D = digraphs.RandomDirectedGNM(size, nb_edges)
    origin = size - 1

    # Let us choose the longest shortest path
    short_paths = D.shortest_path_lengths(origin)
    len_soluce = 0
    goal = -1
    for k in short_paths:
        if short_paths[k] >= len_soluce:
            len_soluce = short_paths[k]
            goal = k

    # Let us start to generate trashly the json
    json = '{\n'
    json += '"snaps" : [5,10,20,100],\n'
    json += '"slots" : ' + str(20*len_soluce) + ',\n'
    json += '"exec_time" : 10000,\n'
    json += '"environment" : [\n'
    for i in range(size-1):
        json += '{"name" : "node_'+str(i)+'", "quantity" : 0},\n'
    json += '{"name" : "node_'+str(size-1)+'", "quantity" : 1},\n'
    json += '{"name" : "energie", "quantity" : '+str(20*len_soluce)+'}\n'
    json += '],\n'
    json += '"actions" : [\n'
    edges = []
    for beg, arr, _ in D.edges():
        edges.append('')
        edges[-1] += '  {"name" : "edges_'+str(beg)+'_'+str(arr)+'", "time" : 1,\n'
	edges[-1] += '     "input" : [\n'
        edges[-1] += '       { "name" : "node_'+str(beg)+'", "quantity" : 1, "relation" : "r" },\n'
        edges[-1] += '       { "name" : "energie", "quantity" : 1, "relation" : "c" }\n'
	edges[-1] += '     ],\n'
	edges[-1] += '     "output" : [\n'
	edges[-1] += '       {"name" : "node_'+str(arr)+'", "quantity" : 1}\n'
	edges[-1] += '     ]\n'
	edges[-1] += '  }'
    json += ',\n'.join(edges)
    json += '\n],\n'
    json += '"fitness" : {\n'
    json += '"type" : "max",\n'
    json += '"operands" : [\n'
    json += '{"name" : "node_'+str(goal)+'", "coef" : '+str(5*len_soluce)+'},\n'
    json += '{"name" : "energie", "coef" : 1}\n'
    json += ']\n}\n}\n'
    
    print(json)

