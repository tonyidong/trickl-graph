/*
 * This file is part of the Trickl Open Source Libraries.
 *
 * Trickl Open Source Libraries - http://open.trickl.com/
 *
 * Copyright (C) 2011 Tim Gee.
 *
 * Trickl Open Source Libraries are free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trickl Open Source Libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.trickl.graph.planar;

import com.trickl.graph.edges.DirectedEdge;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.EdgeFactory;
import org.jgrapht.VertexFactory;

public class DualGraphVisitor<V1, E1, V2, E2> extends AbstractPlanarFaceTraversalVisitor<V1, E1> {

   protected PlanarGraph<V1, E1> inputGraph;
   protected PlanarGraph<V2, E2> dualGraph;
   protected VertexFactory<V2> vertexFactory;
   protected EdgeFactory<V2, E2> edgeFactory;
   protected V2 dualTarget;
   private Map<DirectedEdge<V1>, V2> faceToVertexMap;

   public DualGraphVisitor(PlanarGraph<V1, E1> inputGraph,
           PlanarGraph<V2, E2> dualGraph,
           VertexFactory<V2> vertexFactory) {
      this(inputGraph, dualGraph, vertexFactory, null);
   }

   public DualGraphVisitor(PlanarGraph<V1, E1> inputGraph,
           PlanarGraph<V2, E2> dualGraph,
           VertexFactory<V2> vertexFactory,
           EdgeFactory<V2, E2> edgeFactory) {
      this.inputGraph = inputGraph;
      this.dualGraph = dualGraph;
      this.vertexFactory = vertexFactory;
      this.edgeFactory = edgeFactory == null
              ? dualGraph.getEdgeFactory() : edgeFactory;
      this.faceToVertexMap = new HashMap<DirectedEdge<V1>, V2>();
   }

   @Override
   public void beginFace(V1 source, V1 target) {
      dualTarget = vertexFactory.createVertex();
      dualGraph.addVertex(dualTarget);
      if (target == null) {
         // There are no actual edges to define the face
         faceToVertexMap.put(new DirectedEdge<V1>(source, target), dualTarget);
      }
   }

   @Override
   public void nextEdge(V1 inputSource, V1 inputTarget) {
      DirectedEdge edge = new DirectedEdge<V1>(inputSource, inputTarget);
      faceToVertexMap.put(edge, dualTarget);

      if (faceToVertexMap.containsKey(new DirectedEdge<V1>(inputTarget, inputSource))) {
         V2 dualSource = faceToVertexMap.get(new DirectedEdge<V1>(inputTarget, inputSource));
         V2 dualBefore = null;
         V1 inputBefore = inputGraph.getNextVertex(inputTarget, inputSource);
         V1 inputLast = inputSource;
         V1 inputVertex = inputBefore;
         do {
            V2 dualVertex = faceToVertexMap.get(new DirectedEdge<V1>(inputVertex, inputLast));
            if (dualVertex != null && dualGraph.containsEdge(dualVertex, dualSource)) {
               // First encountered so break on success
               dualBefore = dualVertex;
               break;
            }

            V1 temp = inputVertex;
            inputVertex = inputGraph.getNextVertex(inputLast, inputVertex);
            inputLast = temp;
         } while (!inputVertex.equals(inputBefore));

         V2 dualAfter = null;
         V1 inputAfter = inputTarget;
         inputLast = inputSource;
         inputVertex = inputAfter;
         do {
            V2 dualVertex = faceToVertexMap.get(new DirectedEdge<V1>(inputVertex, inputLast));
            if (dualVertex != null && dualGraph.containsEdge(dualVertex, dualTarget)) {
               // Last encountered, so overwrite on success
               dualAfter = dualVertex;
            }

            V1 temp = inputVertex;
            inputVertex = inputGraph.getNextVertex(inputLast, inputVertex);
            inputLast = temp;
         } while (!inputVertex.equals(inputAfter));
         
         E2 dualEdge = edgeFactory.createEdge(dualSource, dualTarget);
         dualGraph.addEdge(dualSource, dualTarget, dualBefore, dualAfter, dualEdge);       
      }
   }   

   public Map<DirectedEdge<V1>, V2> getFaceToVertexMap() {
      return faceToVertexMap;
   }
   
   public Map<V2, Set<DirectedEdge<V1>>> getVertexToFaceMap() {
      Set<V2> dualVertices = dualGraph.vertexSet();
      Map<V2, Set<DirectedEdge<V1>>> vertexToFaceMap = new HashMap<V2, Set<DirectedEdge<V1>>>(dualVertices.size());
      for (V2 dualVertex : dualVertices) {
         vertexToFaceMap.put(dualVertex, new HashSet<DirectedEdge<V1>>());
      }
      for (Map.Entry<DirectedEdge<V1>, V2> faceToVertexEntry : faceToVertexMap.entrySet()) {
         vertexToFaceMap.get(faceToVertexEntry.getValue()).add(faceToVertexEntry.getKey());
      }
      return vertexToFaceMap;
   }
}
