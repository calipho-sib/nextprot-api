package org.nextprot.api.etl.statement.charpipeline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public class Pipes {
	/**
	 * This class contains a test program for the pipe classes below.
	 * It also demonstrates how you typically use these pipes classes.
	 * It is basically another implementation of a Unix-like grep command.
	 * Note that it frivolously passes the output of the grep filter through
	 * two rot13 filters (which, combined, leave the output unchanged).
	 * Then it converts non-ASCII characters to their \U Unicode encodings.
	 *
	 * With the pipe infrastructure defined below, it is easy to define
	 * new filters and create pipes to perform many useful operations.
	 * Other filter possibilities include sorting lines, removing
	 * duplicate lines, and doing search-and-replace.
	 **/
	public static class Test {

		/** This is the test program for our pipes infrastructure */
		public static void main(String[] args) throws IOException {

			// Create a Reader to read data from, and a Writer to send data to.
			Reader in = new BufferedReader(new FileReader("/scratch/Projects/nextprot-api/LICENSE.txt"));
			Writer out = new BufferedWriter(new OutputStreamWriter(System.out));

			// Now build up the pipe, starting with the sink, and working
			// backwards, through various filters, until we reach the source
			WriterPipeSink sink = new WriterPipeSink(out);
			PipeFilter filter3 = new UnicodeToASCIIFilter(sink);
			PipeFilter filter2 = new Rot13Filter(filter3);
			PipeFilter filter1 = new Rot13Filter(filter2);
			ReaderPipeSource source = new ReaderPipeSource(filter1, in);

			// Start the pipe -- start each of the threads in the pipe running.
			// This call returns quickly, since the each component of the pipe
			// is its own thread
			System.out.println("Starting pipe...");
			source.startPipe();

			// Wait for the pipe to complete
			try { source.joinPipe(); } catch (InterruptedException e) {}
			System.out.println("Done.");
		}
	}

	/**
	 * This is another implementation of Filter.  It implements the
	 * trivial rot13 cipher on the letters A-Z and a-z.  Rot-13 "rotates"
	 * ASCII letters 13 characters through the alphabet.
	 **/
	public static class Rot13Filter extends PipeFilter {
		/** Constructor just calls superclass */
		public Rot13Filter(Pipe sink) throws IOException { super(sink); }

		/** Filter characters from in to out */
		public void filter(Reader in, Writer out) throws IOException {
			char[] buffer = new char[1024];
			int chars_read;

			while((chars_read = in.read(buffer)) != -1) { // read a batch of chars
				// Apply rot-13 to each character, one at a time
				for(int i = 0; i < chars_read; i++) {
					if ((buffer[i] >= 'a') && (buffer[i] <= 'z')) {
						buffer[i] = (char) ('a' + ((buffer[i]-'a') + 13) % 26);
					}
					if ((buffer[i] >= 'A') && (buffer[i] <= 'Z')) {
						buffer[i] = (char) ('A' + ((buffer[i]-'A') + 13) % 26);
					}
				}
				out.write(buffer, 0, chars_read);           // write the batch of chars
			}
		}
	}

	/**
	 * This class is a Filter that accepts arbitrary Unicode characters as input
	 * and outputs non-ASCII characters with their \U Unicode encodings
	 **/
	public static class UnicodeToASCIIFilter extends PipeFilter {
		/** Constructor just calls superclass */
		public UnicodeToASCIIFilter(Pipe sink) throws IOException {
			super(sink);
		}

		/**
		 * Read characters from the reader, one at a time (using a BufferedReader
		 * for efficiency).  Output printable ASCII characters unfiltered.  For
		 * other characters, output the \U Unicode encoding.
		 **/
		public void filter(Reader r, Writer w) throws IOException {
			BufferedReader in = new BufferedReader(r);
			PrintWriter out = new PrintWriter(new BufferedWriter(w));
			int c;
			while((c = in.read()) != -1) {
				// Just output ASCII characters
				if (((c >= ' ') && (c <= '~')) || (c=='\t') || (c=='\n') || (c=='\r'))
					out.write(c);
					// And encode the others
				else {
					String hex = Integer.toHexString(c);
					switch (hex.length()) {
						case 1:  out.print("\\u000" + hex); break;
						case 2:  out.print("\\u00" + hex); break;
						case 3:  out.print("\\u0" + hex); break;
						default: out.print("\\u" + hex); break;
					}
				}
			}
			out.flush();  // flush the output buffer we create
		}
	}
}
