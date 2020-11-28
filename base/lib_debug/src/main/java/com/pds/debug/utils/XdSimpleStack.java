package com.pds.debug.utils;

public class XdSimpleStack<T> {
	Object[] _stack;
	int _maximumSize;
	int _num;

	public XdSimpleStack(int size) {
		this._stack = new Object[size];
		_maximumSize = size;
		_num = 0;
	}

	public void push(T obj) {
		if (_num >= _maximumSize) {
			_maximumSize += 128;
			Object[] newStack = new Object[_maximumSize];
			System.arraycopy(_stack, 0, newStack, 0, _num);
			_stack = newStack;
		}

		_stack[_num] = obj;
		++_num;
	}

	public T pop() {
		if (_num <= 0) {
			return null;
		}
		--_num;
		Object obj = _stack[_num];
		_stack[_num] = null;
		return (T) obj;
	}
}
