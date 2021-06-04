package org.nextprot.api.core.utils;

import jaligner.Alignment;
import jaligner.NeedlemanWunschGotoh;
import jaligner.Sequence;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixLoader;

public class PamAligner {

    private static float openGapPenalty = 10f;
    private static float extendGapPenalty = 0.5f;
    private static Matrix matrix;
    static {
    	try { matrix = MatrixLoader.load("BLOSUM62");
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private Alignment alignment;
    private Sequence s1, s2;
    
    public PamAligner(String name1, String seq1, String name2, String seq2) {

        this.s1 = new Sequence(name1, seq1);
        this.s2 = new Sequence(name2, seq2);
        this.alignment = NeedlemanWunschGotoh.align(s1, s2, matrix, openGapPenalty, extendGapPenalty);   
        this.swapAlignedSequenceIfNecessary();
	}
    
   public Alignment getAlignment() {
	   return this.alignment;
   }

   public int getInnerGapCount() {
	   return countInnerGap(this.alignment.getMarkupLine(), ' '); // gap char in alignment markup is ' '
   }
   
   public SequenceJump getS2Pos(int s1Pos) {
	
	   char[] s1 = this.alignment.getSequence1();
	   int s1Lng = this.alignment.getOriginalSequence1().length();
	   if (s1Pos <= 0 || s1Pos > s1Lng) {
		   return new SequenceJump(s1Pos, -1, -1, '-', '-', SequenceJump.Status.S1_POS_INVALID);
	   }

	   char[] s2 = this.alignment.getSequence2();
	   int s2Lng = this.alignment.getOriginalSequence2().length();
	   int index = 0, currS1Pos = 0, currS2Pos = 0;

	   final char gapChar = '-';
	   for (index=0; index<s1.length; index++) {
		   if (s1[index] != gapChar) currS1Pos++;
		   if (s2[index] != gapChar) currS2Pos++;
		   if (currS2Pos >= s2Lng) {
			   return new SequenceJump(s1Pos, currS2Pos, index, s1[index], '-', SequenceJump.Status.S2_POS_INVALID);
		   }
		   if (currS1Pos == s1Pos) {
			   if (s2[index] == gapChar) {
				   return new SequenceJump(s1Pos, currS2Pos, index, s1[index], s2[index], SequenceJump.Status.S2_POS_ON_GAP);				   
			   } else if (s2[index] != s1[index]) {
				   return new SequenceJump(s1Pos, currS2Pos, index, s1[index], s2[index], SequenceJump.Status.S2_POS_ON_DIFFERENT_CHAR);				   
			   } else {
				   return new SequenceJump(s1Pos, currS2Pos, index, s1[index], s2[index], SequenceJump.Status.OK);				   				   
			   }
		   }
	   }
	   return new SequenceJump(currS1Pos, currS2Pos, index, '-', '-', SequenceJump.Status.UNEXPECTED);
   }

   public int getS1InnerGapCount() {
	   return countInnerGap(this.alignment.getSequence1(), '-'); // gap char in aligned sequence is '-'
   }
   
   public int getS2InnerGapCount() {
	   return countInnerGap(this.alignment.getSequence2(), '-'); // // gap char in aligned sequence is '-'
   }
   
  
   public float getS1Identities() {
	   float lng = this.alignment.getOriginalSequence1().length();
	   float ids = this.alignment.getIdentity();
	   return ids / lng;
   }

   public static int countInnerGap(char[] chars, char gapChar) {
	   int idxFirst, idxLast;
	   for (idxFirst=0;idxFirst<chars.length;idxFirst++) if (chars[idxFirst] != gapChar) break;
	   for (idxLast=chars.length-1;idxLast>=0;idxLast--) if (chars[idxLast] != gapChar) break;
	   int gapCount=0;
	   for (int i=idxFirst;i<=idxLast;i++) if (chars[i] == gapChar) gapCount++;
	   return gapCount;
   }
   
   public static boolean hasInnerGap(char[] chars, char gapChar) {
	   boolean sequenceStarted = false;
	   boolean sequenceEnded = false;
	   for (int i=0;i<chars.length;i++) {
		   char c = chars[i];
		   if (! sequenceStarted && c != gapChar) {
			   sequenceStarted = true;
		   } else if (sequenceStarted && c == gapChar) {
			   sequenceEnded = true;
		   } else if (sequenceEnded && c != gapChar) {
			   return true;
		   }
	   }
	   return false;
   }
   
   /*
    * JAligner sometimes swaps the two sequences given as an input (!?*!!#?!)
    * This method makes sure that getSequence1(), getName1(), getOriginalSequence1(), getStart1()
    * refer to the first sequence argument passed to the align() method.
    * In other words, s1 remains s1 after alignment, s2 remains s2 after aligment too.
    */
   private void swapAlignedSequenceIfNecessary() {
	   
	   if (alignment.getName1().equals(s1.getId())) return;
	   
	   System.out.println("Swapping aligned sequences");
	   
       String name = alignment.getName1();
       alignment.setName1(alignment.getName2());
       alignment.setName2(name);

       Sequence s = alignment.getOriginalSequence1();
       alignment.setOriginalSequence1(alignment.getOriginalSequence2());
       alignment.setOriginalSequence2(s);

       char[] tmp = alignment.getSequence1();
       alignment.setSequence1(alignment.getSequence2());
       alignment.setSequence2(tmp);

       int start = alignment.getStart1();
       alignment.setStart1(alignment.getStart2());
       alignment.setStart2(start);        
   }
   
   public static class SequenceJump {
	   
	   public static enum Status {S1_POS_INVALID, S2_POS_INVALID, S2_POS_ON_GAP, S2_POS_ON_DIFFERENT_CHAR,UNEXPECTED, OK };
	   
	   private int s1Pos, s2Pos, alIdx;
	   private Status status;
	   private char s1Char, s2Char;
	   
	   public SequenceJump(int s1Pos, int s2Pos, int alIdx, char s1Char, char s2Char, Status status) {
		   this.s1Pos=s1Pos;
		   this.s2Pos=s2Pos;
		   this.alIdx = alIdx;
		   this.s1Char = s1Char;
		   this.s2Char = s2Char;
		   this.status = status;
	   }

	   public String toString() {
		   StringBuffer sb = new StringBuffer();
		   sb.append(""   + alIdx);
		   sb.append("\t" + s1Pos);
		   sb.append("\t" + s2Pos);
		   sb.append("\t" + s1Char);
		   sb.append("\t" + s2Char);
		   sb.append("\t" + status);
		   return sb.toString();
	   }
	   
		public int getAlPos() {
			return alIdx;
		}
	
		public int getS1Pos() {
			return s1Pos;
		}
	
		public int getS2Pos() {
			return s2Pos;
		}
	
		public Status getStatus() {
			return status;
		}
	
		public char getS1Char() {
			return s1Char;
		}
	
		public char getS2Char() {
			return s2Char;
		}
	   
	   
   }

}
