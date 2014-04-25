/*
 * #%L
 * ImageJ OPS: a framework for reusable algorithms.
 * %%
 * Copyright (C) 2014 Board of Regents of the University of
 * Wisconsin-Madison and University of Konstanz.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imagej.ops.threading;

import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Parallel;
import net.imagej.ops.chunker.CursorBasedChunk;
import net.imagej.ops.chunker.InterleavedChunker;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Op.class, name = "doNothing", priority = Priority.LOW_PRIORITY)
public class RunInterleavedChunker<A extends RealType<A>> extends AbstractFunction<IterableInterval<A>, IterableInterval<A>> implements Parallel {

	@Parameter
	private OpService opService;
	
	@Override
	public IterableInterval<A> compute(final IterableInterval<A> input,
			final IterableInterval<A> output) {
		
			opService.run(InterleavedChunker.class, new CursorBasedChunk() {

			@Override
			public void	execute(int startIndex, final int stepSize, final int numSteps)
			{
				final Cursor<A> cursor = input.localizingCursor();
				final Cursor<A> cursorOut = output.localizingCursor();
			
				setToStart(cursor, startIndex);
				setToStart(cursorOut, startIndex);

				int ctr = 0;
				while (ctr < numSteps) {
					cursorOut.get().set(cursor.get());
					
					cursorOut.jumpFwd(stepSize);
					cursor.jumpFwd(stepSize);
					ctr++;
				}
			}
		}, input.size());
	
		return output;
		
	}
}