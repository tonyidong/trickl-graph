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
package com.trickl.graph.planar.generate;

import com.trickl.graph.planar.PlanarGraph;
import com.trickl.graph.planar.PlanarLayout;
import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.VertexFactory;

public class PlanarSquareGraphGenerator<V, E> 
        implements PlanarGraphGenerator<V, E, V>, PlanarLayout<V> {

   private Map<V, Coordinate> positions = new HashMap<V, Coordinate>();

   private ArrayList<V> vertices = new ArrayList<V>();      
   
   private final int size;
   private final double scale;

   public PlanarSquareGraphGenerator(int size)
   {
      this(size, 1);
   }

   public PlanarSquareGraphGenerator(int size, double scale)
   {
      this.size = size;
      this.scale = scale;
   }

   @Override
   public void generateGraph(PlanarGraph<V,E> graph, VertexFactory<V> vertexFactory,
           java.util.Map<java.lang.String,V> resultMap)
   {      
      int width = ((int) Math.sqrt(size - 1)) + 1;

      for (int k = 0; k < size; ++k)
      {
         V vertex = vertexFactory.createVertex();
         vertices.add(vertex);
         graph.addVertex(vertex);
      }

      for (int k = 0; k < size; ++k)
      {         
         V vertex = vertices.get(k);
         
         int i = k % width;
         int j = k / width;

         // Build upwards (positive j = top)
         int k_left = (i - 1) + j * width;
         int k_right = (i + 1) + j * width;
         int k_top = i + (j + 1) * width;
         int k_bottom = i + (j - 1) * width;

         V bottom = null;
         V left = null;
         V top = null;
         V right = null;

         // Add edges cyclically to ensure planarity
         if (j != 0 && k_bottom < size) {
            bottom = vertices.get(k_bottom);
         }
         if (i != 0 && k_left < size) {
            left = vertices.get(k_left);
         }
         if (j != width - 1 && k_top < size) {
            top = vertices.get(k_top);
         }
         if (i != width - 1 && k_right < size) {
            right = vertices.get(k_right);
         }
                  
         if (right != null) graph.addEdge(vertex, right, bottom, null);
         if (top != null) graph.addEdge(vertex, top, right == null ? bottom : right, null);
         if (left != null) graph.addEdge(vertex, left, top, null);
         if (bottom != null) graph.addEdge(vertex, bottom, left, null);
                
         positions.put(vertex, new Coordinate((i - width * 0.5) * scale, (j - width * 0.5) * scale));
      }
   }

   @Override
   public Coordinate getCoordinate(V vertex) {
      return positions.get(vertex);
   }
}
