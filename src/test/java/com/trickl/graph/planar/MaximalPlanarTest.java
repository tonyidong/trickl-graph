package com.trickl.graph.planar;

import com.trickl.graph.edges.UndirectedIdEdge;
import com.trickl.graph.edges.UndirectedIdEdgeFactory;
import com.trickl.graph.planar.DoublyConnectedEdgeList;
import com.trickl.graph.planar.MaximalPlanar;
import com.trickl.graph.planar.PlanarGraph;
import com.trickl.graph.vertices.IdVertex;
import com.trickl.graph.vertices.IdVertexFactory;
import com.trickl.graph.generate.PlanarCircleGraphGenerator;
import static com.trickl.graph.planar.PlanarAssert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class MaximalPlanarTest {

   public MaximalPlanarTest() {
   }

   @Test   
   public void testMinimal() throws InterruptedException, InvocationTargetException {
      IdVertexFactory vertexFactory = new IdVertexFactory();
      PlanarGraph<IdVertex, UndirectedIdEdge<IdVertex>> graph
              = new DoublyConnectedEdgeList<IdVertex,
              UndirectedIdEdge<IdVertex>,
              Object>(new UndirectedIdEdgeFactory<IdVertex>(), Object.class);
      for (int i = 0; i < 5; ++i) graph.addVertex(vertexFactory.createVertex());
      graph.addEdge(vertexFactory.get(0), vertexFactory.get(1));
      graph.addEdge(vertexFactory.get(1), vertexFactory.get(2));
      graph.addEdge(vertexFactory.get(2), vertexFactory.get(0));
      graph.addEdge(vertexFactory.get(1), vertexFactory.get(3));
      graph.addEdge(vertexFactory.get(3), vertexFactory.get(2));
      graph.addEdge(vertexFactory.get(3), vertexFactory.get(4));
      graph.addEdge(vertexFactory.get(4), vertexFactory.get(2));

  
      MaximalPlanar<IdVertex, UndirectedIdEdge<IdVertex>> maximalPlanar = new MaximalPlanar<IdVertex, UndirectedIdEdge<IdVertex>>();
      maximalPlanar.makeMaximalPlanar(graph);
      
      assertEmbeddingEquals(graph, vertexFactory.get(0), "4,2,1,3");
      assertEmbeddingEquals(graph, vertexFactory.get(3), "4,0,1,2");
      assertEmbeddingEquals(graph, vertexFactory.get(4), "0,3,2");
   }

   @Test   
   public void testSmall() throws InterruptedException, InvocationTargetException {
      int vertices = 7;
      IdVertexFactory vertexFactory = new IdVertexFactory();

      PlanarGraph<IdVertex, UndirectedIdEdge<IdVertex>> graph = new DoublyConnectedEdgeList<IdVertex, UndirectedIdEdge<IdVertex>, Object>(new UndirectedIdEdgeFactory<IdVertex>(), Object.class);

      PlanarCircleGraphGenerator generator = new PlanarCircleGraphGenerator<IdVertex, UndirectedIdEdge<IdVertex>>(vertices, 100);
      generator.generateGraph(graph, vertexFactory, null);

      MaximalPlanar<IdVertex, UndirectedIdEdge<IdVertex>> maximalPlanar = new MaximalPlanar<IdVertex, UndirectedIdEdge<IdVertex>>();
      maximalPlanar.makeMaximalPlanar(graph);
      
      assertEmbeddingEquals(graph, vertexFactory.get(2), "1,0,3,6");
   }
}