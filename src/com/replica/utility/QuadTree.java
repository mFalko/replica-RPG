package com.falko.android.snowball.utility;

import java.util.Iterator;

import android.graphics.RectF;

import com.falko.android.snowball.core.AllocationGuard;

public class QuadTree<T extends HasBounds> extends AllocationGuard {

	private QNode root_;
	private QNodePool qNodePool_;
	private LList<T>.LLNodePool listNodePool_;
	private int minQNodeSize_;
	private static final int MAX_DATA_COUNT = 4;
	private static final int NODE_COUNT = 4;
	private static final int MAX_NODES = 200;
	private final int MAX_ITEMS;

	public QuadTree(int maxItems) {
		super();
		MAX_ITEMS = maxItems;
		root_ = new QNode(MAX_ITEMS + MAX_NODES + 1);
		listNodePool_ = root_.data_.getPool();
		qNodePool_ = new QNodePool(MAX_NODES);
	}

	public void setBounds(float x, float y, float width, float height) {
		reset();
		root_.getBounds().set(x, y, width, height);
		minQNodeSize_ = (int) (width / 16);
	}
	
	public RectF getBounds() {
		return root_.getBounds();
	}

	public boolean add(T item) {
		return add(item, root_);
	}

	private boolean add(T item, QNode node) {

		if (!(node.contains(item) && listNodePool_.canAllocate())) {
			return false;
		}

		if (node.partitioned_) {
			for (int i = 0; i < NODE_COUNT; ++i) {
				final QNode subNode = node.nodes_.get(i);
				if (subNode.contains(item)) {
					return add(item, subNode);
				}
			}

			node.data_.add(item);
			return true;
		}

		node.data_.add(item);
		
		if (node.data_.getLength() >= MAX_DATA_COUNT
				&& node.getBounds().width() > minQNodeSize_
				&& qNodePool_.canAllocate(NODE_COUNT)) {
			
			for (int i = 0; i < NODE_COUNT; ++i) {
				node.nodes_.add(qNodePool_.allocate());
			}
			
			float left = node.getBounds().left;
			float top = node.getBounds().top;
			float right = node.getBounds().right;
			float bottom = node.getBounds().bottom;
			float halfWidth = (right - left) / 2.0f;
			float halfHeight = (top - bottom) / 2.0f;

			node.nodes_.get(0).bounds_.set(left + halfWidth, top                , right           , bottom + halfHeight);
			node.nodes_.get(1).bounds_.set(left            , top                , left + halfWidth, bottom + halfHeight);
			node.nodes_.get(2).bounds_.set(left            , bottom + halfHeight, left + halfWidth, bottom);
			node.nodes_.get(3).bounds_.set(left + halfWidth, bottom + halfHeight, right           , bottom);

			node.partitioned_ = true;

			Iterator<T> iter = node.data_.iterator();
			while (iter.hasNext()) {
				T subItem = iter.next();

				for (int i = 0; i < NODE_COUNT; ++i) {
					final QNode subNode = node.nodes_.get(i);
					if (add(subItem, subNode)) {
						iter.remove();
						continue;
					}
				}
			}
		}
		return true;
	}

	public void Query(RectF area, FixedSizeArray<T> result) {
		addIntersections(area, result, root_);
	}

	private void addIntersections(RectF area, FixedSizeArray<T> result,
			QNode node) {
		if (!(RectF.intersects(node.getBounds(), area))) {
			return;
		}

		Iterator<T> iter = node.data_.iterator();
		while (iter.hasNext()) {
			T subItem = iter.next();
			if (RectF.intersects(subItem.getBounds(), area)) {
				result.add(subItem);
			}
		}

		if (node.partitioned_) {
			for (int i = 0; i < NODE_COUNT; ++i) {
				final QNode n = node.nodes_.get(i);
				if (RectF.intersects(n.getBounds(), area)) {
					addIntersections(area, result, n);
				}
			}
		}
	}

	public void reset() {
		reset(root_);
		root_.data_.clear();
		root_.partitioned_ = false;
		root_.nodes_.clear();
	}

	private void reset(QNode node) {

		if (node.partitioned_) {
			for (int i = 0; i < NODE_COUNT; ++i) {
				final QNode n = node.nodes_.get(i);
				reset(n);
				if (root_ != n)
					qNodePool_.release(n);
			}
		}
	}

	private class QNode extends AllocationGuard implements HasBounds {

		public LList<T> data_;
		public FixedSizeArray<QNode> nodes_;
		public boolean partitioned_ = false;
		private RectF bounds_;

		public QNode(int size) {
			super();
			data_ = new LList<T>(size);
			nodes_ = new FixedSizeArray<QNode>(4);
			bounds_ = new RectF();
		}

		public QNode(LList<T>.LLNodePool pool) {
			super();
			data_ = new LList<T>(pool);
			nodes_ = new FixedSizeArray<QNode>(4);
			bounds_ = new RectF();
		}

		public void reset() {
			data_.clear();
			bounds_.setEmpty();
			nodes_.clear();
			partitioned_ = false;
		}

		public RectF getBounds() {
			return bounds_;
		}

		public void setBounds(RectF bounds) {
			bounds_.set(bounds);
		}

		public boolean contains(HasBounds item) {
			return bounds_.contains(item.getBounds());
		}
	}

	private class QNodePool extends TObjectPool<QNode> {

		public QNodePool(int size) {
			super(size);
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				getAvailable().add(new QNode(listNodePool_));
			}
		}

		public QNode allocate() {
			QNode newNode = super.allocate();
			return newNode;
		}

		public boolean canAllocate(int amount) {
			return getSize() - getAllocatedCount() >= amount;
		}

		public void release(Object entry) {
			@SuppressWarnings("unchecked")
			QNode node = (QNode) entry;
			node.reset();
			super.release(node);
		}

	}

	

}
