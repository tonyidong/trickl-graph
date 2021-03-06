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
package com.trickl.graph;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import com.trickl.matrix.MoorePenrosePseudoInverseAlgorithm;
import com.trickl.matrix.MoorePenrosePseudoInverseBySVD;
import hep.aida.bin.StaticBin1D;
import org.jgrapht.Graph;

/* See Affinity Measures based on the Graph Laplacian */
/* Rao, Yarowsky, Callison-Burch cs.jhu.edu */
/* This is a dense kernel with O(N^2) elements for N vertices */
public class SigmoidCommuteTimeKernelGenerator<V, E> implements VertexKernelGenerator<V, E> {

   private DoubleMatrix2D kernel;
   private LaplacianGenerator<V, E> laplacian;
   private MoorePenrosePseudoInverseAlgorithm pseudoInverseAlgorithm
           = new MoorePenrosePseudoInverseBySVD();

   private double sharpnessFactor = -3.0;

   public SigmoidCommuteTimeKernelGenerator() {
   }

   @Override
   public DoubleMatrix2D getKernel(Graph<V, E> graph) {
      if (kernel == null) {         
         laplacian = new LaplacianGenerator<V, E>(graph);

         // The laplacian has rank n-1, i.e. it is rank-deficient. So
         // We need the Moore-Penrose pseudo-inverse
         DoubleMatrix2D L = laplacian.getLaplacian();
         DoubleMatrix2D K = pseudoInverseAlgorithm.inverse(L);
         StaticBin1D kBin = new StaticBin1D();
         for (int i = 0; i < K.rows(); ++i) {
            for (int j = 0; j < K.columns(); ++j) {
               kBin.add(K.getQuick(i, j));
            }             
         }
         double std = kBin.standardDeviation();

         kernel = new DenseDoubleMatrix2D(K.rows(), K.columns());
         for (int i = 0; i < K.rows(); ++i) {
            for (int j = 0; j < K.columns(); ++j) {
               kernel.setQuick(i, j, 1 / (1 + Math.exp(sharpnessFactor * K.getQuick(i, j) / std)));
            }
         }
      }

      return kernel;
   }

   @Override
   public Integer getIndex(V vertex) {
      return laplacian.getIndex(vertex);
   }

   @Override
   public V getVertex(int index) {
      return laplacian.getVertex(index);
   }

   public double getSharpnessFactor() {
      return sharpnessFactor;
   }

   public void setSharpnessFactor(double sharpnessFactor) {
      this.sharpnessFactor = sharpnessFactor;
   }

   public MoorePenrosePseudoInverseAlgorithm getPseudoInverseAlgorithm() {
      return pseudoInverseAlgorithm;
   }

   public void setPseudoInverseAlgorithm(MoorePenrosePseudoInverseAlgorithm pseudoInverseAlgorithm) {
      this.pseudoInverseAlgorithm = pseudoInverseAlgorithm;
   }
}
