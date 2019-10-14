

import exceptions.InsertionException;
import exceptions.PositionException;
import exceptions.RemovalException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
    An implementation of a directed graph using incidence lists
    for sparse graphs where most things aren't connected.
    @param <V> Vertex element type.
    @param <E> Edge element type.
*/
public class SparseGraph<V, E> implements Graph<V, E> {

    // Class for a vertex of type V

    /**
     * Class to create new vertex node.
     * @param <V> Vertex element type.
     */
    private final class VertexNode<V> implements Vertex<V>,
            Comparable<VertexNode<V>> {
        V data;
        Graph<V, E> owner;
        List<Edge<E>> outgoing;
        List<Edge<E>> incoming;
        Object label;
        double distance;

        /** Constructor for new vertex node. */
        VertexNode(V v) {
            this.data = v;
            this.outgoing = new ArrayList<>();
            this.incoming = new ArrayList<>();
            this.label = null;
        }

        /** Get data value of node. */
        @Override
        public V get() {
            return this.data;
        }

        /** Put new data value in node. */
        @Override
        public void put(V v) {
            this.data = v;
        }

        /**
         * Comparing vertex nodes by distance
         * (for street searcher).
         * @param v vertex node to compare to.
         * @return value from comparison.
         */
        @Override
        public int compareTo(VertexNode<V> v) {
            if (this.distance < v.distance) {
                return -1;
            } else if (this.distance > v.distance) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    /**
     * Class for an edge of type E.
     * @param <E> Edge element type.
     */
    private final class EdgeNode<E> implements Edge<E> {
        E data;
        Graph<V, E> owner;
        VertexNode<V> from;
        VertexNode<V> to;
        Object label;

        /** Constructor for new edge. */
        EdgeNode(VertexNode<V> f, VertexNode<V> t, E e) {
            this.from = f;
            this.to = t;
            this.data = e;
            this.label = null;
        }

        /** Get data for edgenode. */
        @Override
        public E get() {
            return this.data;
        }

        /** Put new data value. */
        @Override
        public void put(E e) {
            this.data = e;
        }
    }

    private List<Vertex<V>> vertices;
    private List<Edge<E>> edges;

    /** Constructor for instantiating a graph. */
    public SparseGraph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Check if owner of vertex node is
     * this graph.
     * @param toTest vertex node to test.
     */
    private void checkOwner(VertexNode<V> toTest) {
        if (toTest.owner != this) {
            throw new PositionException();
        }
    }


    /**
     * Check that owner of edge node is this
     * graph.
     * @param toTest edge node to test.
     */
    private void checkOwner(EdgeNode<E> toTest) {
        if (toTest.owner != this) {
            throw new PositionException();
        }
    }


    /**
     * Convert vertex back to vertex node.
     * @param v vertex given.
     * @return vertex node found.
     * @throws PositionException if vertex is not a
     * vertex or not in the graph.
     */
    private VertexNode<V> convert(Vertex<V> v) throws PositionException {
        try {
            VertexNode<V> gv = (VertexNode<V>) v;
            this.checkOwner(gv);
            return gv;
        } catch (ClassCastException ex) {
            throw new PositionException();
        }
    }


    /**
     * Covert edge back to edge node.
     * @param e edge given.
     * @return edge node corresponding to edge entered.
     * @throws PositionException if edge is not an edge
     * or not in graph.
     */
    private EdgeNode<E> convert(Edge<E> e) throws PositionException {
        try {
            EdgeNode<E> ge = (EdgeNode<E>) e;
            this.checkOwner(ge);
            return ge;
        } catch (ClassCastException ex) {
            throw new PositionException();
        }
    }

    /**
     * Isert new vertex.
     * @param v Element to insert.
     * @return new vertex.
     */
    @Override
    public Vertex<V> insert(V v) {
        VertexNode<V> cur = new VertexNode<>(v);
        cur.owner = this;
        this.vertices.add(cur);
        return cur;
    }

    /**
     * Insert edge.
     * @param from Vertex position where edge starts.
     * @param to Vertex position where edge ends.
     * @param e Element to insert.
     * @return New edge
     * @throws PositionException if either vertex is not
     * in graph or not a vertex.
     * @throws InsertionException if edge will create
     * self cycle or already exists.
     */
    @Override
    public Edge<E> insert(Vertex<V> from, Vertex<V> to, E e)
            throws PositionException, InsertionException {

        VertexNode<V> first = this.convert(from);
        VertexNode<V> second = this.convert(to);

        if (first.equals(second)) {
            throw new InsertionException();
        }

        EdgeNode<E> cur = new EdgeNode<>(first, second, e);
        Iterable<Edge<E>> make = this.edges();
        Iterator<Edge<E>> it = make.iterator();
        while (it.hasNext()) {
            EdgeNode<E> check = this.convert(it.next());
            if (check.from.equals(cur.from) && check.to.equals(cur.to)) {
                throw new InsertionException();
            }
        }

        cur.owner = this;
        this.edges.add(cur);
        first.outgoing.add(cur);
        second.incoming.add(cur);
        return cur;
    }

    /**
     * Remove a vertex.
     * @param v Vertex position to remove.
     * @return data at vertex.
     * @throws PositionException if vertex is not in graph
     * or not a vertex.
     * @throws RemovalException if there are still
     * edges attached to vertex.
     */
    @Override
    public V remove(Vertex<V> v) throws PositionException,
            RemovalException {

        VertexNode<V> cur = this.convert(v);
        if (!cur.outgoing.isEmpty() || !cur.incoming.isEmpty()) {
            throw new RemovalException();
        }

        this.vertices.remove(v);
        return cur.data;
    }

    /**
     * Remove an edge.
     * @param e Edge position to remove.
     * @return data at edge.
     * @throws PositionException if edge is not in graph
     * or not an edge.
     */
    @Override
    public E remove(Edge<E> e) throws PositionException {
        EdgeNode<E> cur = this.convert(e);
        this.edges.remove(e);
        return cur.data;
    }

    /**
     * Create iterable to iterate through
     * all vertices.
     * @return iterable object.
     */
    @Override
    public Iterable<Vertex<V>> vertices() {
        return this.vertices;
    }

    /**
     * Create iterable to iterate through
     * all edges.
     * @return iterable object.
     */
    @Override
    public Iterable<Edge<E>> edges() {
        return this.edges;
    }

    /**
     * Create iterable object to iterate through
     * outoging edges of vertex.
     * @param v Vertex position to explore.
     * @return iterable object.
     * @throws PositionException if vertex not in graph
     * or not a vertex.
     */
    @Override
    public Iterable<Edge<E>> outgoing(Vertex<V> v) throws PositionException {
        VertexNode<V> cur = this.convert(v);
        return cur.outgoing;
    }

    /**
     * Iterable object to iterate through incoming
     * edges to vertex.
     * @param v Vertex position to explore.
     * @return iterable object.
     * @throws PositionException if vertex not in graph
     * or not a vertex.
     */
    @Override
    public Iterable<Edge<E>> incoming(Vertex<V> v) throws PositionException {
        VertexNode<V> cur = this.convert(v);
        return cur.incoming;
    }

    /**
     * Find vertex an edge is going from.
     * @param e Edge position to explore.
     * @return Vertex at start of edge.
     * @throws PositionException if edge is not in graph
     * or not an edge.
     */
    @Override
    public Vertex<V> from(Edge<E> e) throws PositionException {
        EdgeNode<E> cur = this.convert(e);
        return cur.from;
    }

    /**
     * Find vertex edge is going to.
     * @param e Edge position to explore.
     * @return vertex that edge is going to.
     * @throws PositionException if edge is not in graph
     * or not an edge.
     */
    @Override
    public Vertex<V> to(Edge<E> e) throws PositionException {
        EdgeNode<E> cur = this.convert(e);
        return cur.to;
    }

    /**
     * Change label of given vertex.
     * @param v Vertex position to label.
     * @param l Label object.
     * @throws PositionException if vertex is not in graph
     * or not a vertex.
     */
    @Override
    public void label(Vertex<V> v, Object l) throws PositionException {
        VertexNode<V> cur = this.convert(v);
        cur.label = l;
    }

    /**
     * Change label of given edge.
     * @param e Edge position to label.
     * @param l Label object.
     * @throws PositionException if edge is not in graph
     * or not an edge.
     */
    @Override
    public void label(Edge<E> e, Object l) throws PositionException {
        EdgeNode<E> cur = this.convert(e);
        cur.label = l;
    }

    /**
     * Find label of given vertex.
     * @param v Vertex position to query.
     * @return label.
     * @throws PositionException if vertex is not in graph
     * or not a vertex.
     */
    @Override
    public Object label(Vertex<V> v) throws PositionException {
        VertexNode<V> cur = this.convert(v);
        return cur.label;
    }

    /**
     * Find label of edge.
     * @param e Edge position to query.
     * @return label.
     * @throws PositionException if edge is not in graoh
     * or not an edge.
     */
    @Override
    public Object label(Edge<E> e) throws PositionException {
        EdgeNode<E> cur = this.convert(e);
        return cur.label;
    }

    /**
     * Clear all labels of vertices and nodes
     * to null.
     */
    @Override
    public void clearLabels() {
        Iterable<Vertex<V>> make = this.vertices();
        Iterator<Vertex<V>> it = make.iterator();
        while (it.hasNext()) {
            VertexNode<V> cur = this.convert(it.next());
            cur.label = null;
        }

        Iterable<Edge<E>> build = this.edges();
        Iterator<Edge<E>> its = build.iterator();
        while (its.hasNext()) {
            EdgeNode<E> cur = this.convert(its.next());
            cur.label = null;
        }

    }

    /**
     * Convert each vertex to a string.
     * @param v vertex to convert.
     * @return string form of given vertex.
     */
    private String vertexString(Vertex<V> v) {
        return "\"" + v.get() + "\"";
    }

    /**
     * Collecting all the string forms of vertices.
     * @return String representation of vertices.
     */
    private String verticesToString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex<V> v : this.vertices) {
            sb.append("  ").append(vertexString(v)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Getting string of each edge.
     * @param e the edge to convert.
     * @return String form of given edge.
     */
    private String edgeString(Edge<E> e) {
        return String.format("%s -> %s [label=\"%s\"]",
                this.vertexString(this.from(e)),
                this.vertexString(this.to(e)),
                e.get());
    }

    /**
     * Collecting all edges to string form.
     * @return string form of edges.
     */
    private String edgesToString() {
        String edgs = "";
        for (Edge<E> e : this.edges) {
            edgs += "    " + this.edgeString(e) + ";\n";
        }
        return edgs;
    }

    /**
     * Convert graph to a string representation.
     * @return string form.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph {\n")
                .append(this.verticesToString())
                .append(this.edgesToString())
                .append("}");
        return sb.toString();
    }

    /**
     * Find data of given vertex.
     * @param v vertex given.
     * @return data value.
     */
    public V getData(Vertex<V> v) {
        VertexNode<V> find = this.convert(v);
        return find.data;
    }

    /**
     * Find distance of vertex.
     * @param v vertex given.
     * @return distance from source.
     */
    public double getDist(Vertex<V> v) {
        VertexNode<V> find = this.convert(v);
        return find.distance;
    }

    /**
     * Change distance of vertex.
     * @param v Given vertex.
     * @param dist distance to change to.
     */
    public void putDist(Vertex<V> v, double dist) {
        VertexNode<V> add = this.convert(v);
        add.distance = dist;
    }

}
