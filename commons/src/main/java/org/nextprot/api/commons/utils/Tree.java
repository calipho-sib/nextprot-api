package org.nextprot.api.commons.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tree<T> implements Serializable{
	
	private static final long serialVersionUID = -7335532907518793181L;
	private Node<T> root;

    public Tree(T rootValue) {
        root = new Node<T>(rootValue);
        root.children = new ArrayList<Node<T>>();
        root.parents = new ArrayList<Node<T>>();
    }

    public Node<T> getRoot() {
		return root;
	}

	public void setRoot(Node<T> root) {
		this.root = root;
	}

	public static class Node<T> implements Serializable{
    	
		private static final long serialVersionUID = -6695666155634799223L;

		private T value;
	    private List<Node<T>> parents;
	    private List<Node<T>> children;
	    
	    public Node(T value) {
	    	this.value = value;
	    }
 
        public T getValue() {
			return value;
		}
		public void setValue(T value) {
			this.value = value;
		}
		
		public List<Node<T>> getParents() {
			return parents;
		}
		
		public void setParents(List<Node<T>> parents) {
			this.parents = parents;
		}
		public List<Node<T>> getChildren() {
			return children;
		}
		public void setChildren(List<Node<T>> children) {
			this.children = children;
		}
       
    }
}