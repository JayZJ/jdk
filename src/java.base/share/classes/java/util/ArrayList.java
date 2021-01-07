package java.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import jdk.internal.access.SharedSecrets;
import jdk.internal.util.ArraysSupport;

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * 默认初始化大小
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * 共享的空数组对象。
     *
     * 在 {@link #ArrayList(int)} 或 {@link #ArrayList(Collection)} 构造方法中，
     * 如果传入的初始化大小或者集合大小为 0 时，将 {@link #elementData} 指向它。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 共享的空数组对象，用于 {@link #ArrayList()} 构造方法。
     *
     * 通过使用该静态变量，和 {@link #EMPTY_ELEMENTDATA} 区分开来，在第一次添加元素时
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     *  元素数组。
     *  当添加新的元素时，如果该数组容量不够，会创建新数组，并将原数组的元素拷贝到新数组。之后，将该变量指向新数组
     */
    transient Object[] elementData; // non-private to simplify nested class access

    /**
     * 数组大小
     */
    private int size;

    /**
     * 用初始化容量初始化一个空数组
     */
    public ArrayList(int initialCapacity) {
        // 初始化容量大于0，则初始化数组
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        // 初始化容量等于0，则指向 EMPTY_ELEMENTDATA
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    /**
     *
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public ArrayList(Collection<? extends E> c) {
        // 将 c 转换成 Object 数组
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // 如果集合元素不是 Object[] 类型，则会创建新的 Object[] 数组，并将 elementData 赋值到其中，最后赋值给 elementData 。
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        // 如果数组大小等于 0 ，则指向 EMPTY_ELEMENTDATA 。
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    /**
     * 缩容
     */
    public void trimToSize() {
        // 修改次数++
        modCount++;
        // 如果有多余的空间，则进行缩容
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA // 大小为 0 时，直接使用 EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size); // 大小大于 0 ，则创建大小为 size 的新数组，将原数组复制到其中。
        }
    }

    /**
     * 主动扩容
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length // 如果 minCapacity 大于数组的容量
            && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
                 && minCapacity <= DEFAULT_CAPACITY)) { // 如果 elementData 是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 的时候，
                                                        // 需要最低 minCapacity 容量大于 DEFAULT_CAPACITY ，因为实际上容量是 DEFAULT_CAPACITY 。
            // 数组修改次数加一
            modCount++;
            // 扩容
            grow(minCapacity);
        }
    }

    /**
     * 扩容
     */
    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        // 如果原容量大于 0 ，或者数组不是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 时，计算新的数组大小，并创建扩容
        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = ArraysSupport.newLength(oldCapacity,
                    minCapacity - oldCapacity, /* minimum growth */
                    oldCapacity >> 1           /* preferred growth */);
            return elementData = Arrays.copyOf(elementData, newCapacity);
        // 如果是 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 数组，直接创建新的数组即可。
        } else {
            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private Object[] grow() {
        // 最小扩容要求
        return grow(size + 1);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @see #indexOf(Object) 基于以上方法实现
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * 查找首个为指定元素的位置
     */
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    int indexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        // 为 null
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        // 非 null
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        // 找不到，返回 -1
        return -1;
    }

    /**
     * 查找最后一个为指定元素的位置
     */
    public int lastIndexOf(Object o) {
        return lastIndexOfRange(o, 0, size);
    }

    int lastIndexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = end - 1; i >= start; i--) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = end - 1; i >= start; i--) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 克隆，elementData 是重新拷贝出来的新的数组，避免和原数组共享
     */
    public Object clone() {
        try {
            // 调用父类，进行克隆
            ArrayList<?> v = (ArrayList<?>) super.clone();
            // 拷贝一个新的数组
            v.elementData = Arrays.copyOf(elementData, size);
            // 设置数组修改次数为 0
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * 将 ArrayList 转换成 [] 数组, 返回为 object 类型
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * 转为 T 类型数组
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // 如果传入的数组小于 size 大小，则直接复制一个新数组返回
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        // 将 elementData 复制到 a 中
        System.arraycopy(elementData, 0, a, 0, size);
        // 如果传入的数组大于 size 大小，则将 size 赋值为 null
        if (a.length > size)
            // 在调用者知道列表中没有null值，需要确定列表长度时使用
            a[size] = null;
        return a;
    }

    // Positional Access Operations

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] es, int index) {
        return (E) es[index];
    }

    /**
     * 获取指定位置的元素
     */
    public E get(int index) {
        Objects.checkIndex(index, size);
        return elementData(index);
    }

    /**
     * 设置指定位置的元素，返回原值
     */
    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    /**
     * 在固定位置添加元素
     */
    private void add(E e, Object[] elementData, int s) {
        // 如果容量不够，进行扩容
        if (s == elementData.length)
            elementData = grow();
        // 将元素放在固定位置，即末尾
        elementData[s] = e;
        // 大小加一
        size = s + 1;
    }

    /**
     * 在数组尾添加一个元素
     */
    public boolean add(E e) {
        // 增加数组修改次数
        modCount++;
        // 添加元素
        add(e, elementData, size);
        // 返回成功
        return true;
    }

    /**
     * 插入元素到固定位置
     */
    public void add(int index, E element) {
        // 校验位置是否在数组范围内
        rangeCheckForAdd(index);
        // 修改次数++
        modCount++;
        // 如果数组大小不够，进行扩容
        final int s;
        Object[] elementData;
        if ((s = size) == (elementData = this.elementData).length)
            elementData = grow();
        // 将 index + 1 位置开始的元素，进行往后挪
        System.arraycopy(elementData, index,
                         elementData, index + 1,
                         s - index);
        // 设置到指定位置
        elementData[index] = element;
        // 数组大小 +1
        size = s + 1;
    }

    /**
     * 移除指定位置元素
     */
    public E remove(int index) {
        // 下标合法性检测
        Objects.checkIndex(index, size);
        final Object[] es = elementData;

        // 记录该位置的原值
        @SuppressWarnings("unchecked") E oldValue = (E) es[index];
        fastRemove(es, index);

        return oldValue;
    }

    /**
     * equals 方法
     */
    public boolean equals(Object o) {
        // 相等直接返回
        if (o == this) {
            return true;
        }

        // 不是 List 的实例
        if (!(o instanceof List)) {
            return false;
        }

        // 获取修改次数，用于校验计算期间集合是否被其他线程修改
        final int expectedModCount = modCount;
        // ArrayList can be subclassed and given arbitrary behavior, but we can
        // still deal with the common case where o is ArrayList precisely
        boolean equal = (o.getClass() == ArrayList.class)
            ? equalsArrayList((ArrayList<?>) o)
            : equalsRange((List<?>) o, 0, size);

        // 校验本集合是否被改变
        checkForComodification(expectedModCount);
        return equal;
    }

    /**
     * 如果实例不是 arrayList, 为其他集合，判断是否相等
     */
    boolean equalsRange(List<?> other, int from, int to) {
        final Object[] es = elementData;
        // to 超出数组边界
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        var oit = other.iterator();
        for (; from < to; from++) {
            // 用迭代器取出 other 的元素一一比较
            if (!oit.hasNext() || !Objects.equals(es[from], oit.next())) {
                return false;
            }
        }
        // 当前集合元素已取完，如果 other 集合还有元素，则返回 false
        // 相当于判断两个集合长度是否一致
        return !oit.hasNext();
    }

    /**
     * 如果实例是 ArrayList，判断是否相等
     */
    private boolean equalsArrayList(ArrayList<?> other) {
        final int otherModCount = other.modCount;
        final int s = size;
        boolean equal;
        if (equal = (s == other.size)) {
            final Object[] otherEs = other.elementData;
            final Object[] es = elementData;
            // 长度发生变化，说明被其他线程修改
            if (s > es.length || s > otherEs.length) {
                throw new ConcurrentModificationException();
            }
            for (int i = 0; i < s; i++) {
                if (!Objects.equals(es[i], otherEs[i])) {
                    equal = false;
                    break;
                }
            }
        }
        // 校验 other 集合是否被改变
        other.checkForComodification(otherModCount);
        return equal;
    }

    private void checkForComodification(final int expectedModCount) {
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * 哈希值
     */
    public int hashCode() {
        // 获取修改次数
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        // 如果修改次数发生改变，则抛出 ConcurrentModificationException 异常
        checkForComodification(expectedModCount);
        return hash;
    }

    int hashCodeRange(int from, int to) {
        final Object[] es = elementData;
        // 如果 to 超过大小，则抛出 ConcurrentModificationException 异常
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        int hashCode = 1;
        // 遍历每个元素，* 31 求哈希
        for (int i = from; i < to; i++) {
            Object e = es[i];
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    /**
     * 移除对象
     */
    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        // 寻找首个为 o 的位置
        int i = 0;
        // found跳出多重循环
        found: {
            if (o == null) {
                // o 为 null 的情况, 找到第一个为 null 的元素
                for (; i < size; i++)
                    if (es[i] == null)
                        break found;
            } else {
                // o 不为 null 的情况, 找到第一个为相等的元素
                for (; i < size; i++)
                    if (o.equals(es[i]))
                        break found;
            }
            // 没找到直接返回 false
            return false;
        }
        // 快速移除
        fastRemove(es, i);
        return true;
    }

    /**
     * 移除对应下标的元素
     */
    private void fastRemove(Object[] es, int i) {
        // 修改次数++
        modCount++;
        final int newSize;
        // 如果移除的元素是最后一位，则不需要移动元素
        if ((newSize = size - 1) > i)
            System.arraycopy(es, i + 1, es, i, newSize - i);
        // 将最后位置元素置为空
        es[size = newSize] = null;
    }

    /**
     * 清空数组
     */
    public void clear() {
        // 修改次数
        modCount++;
        final Object[] es = elementData;
        // 设置 size = 0, 所有元素置为 null
        for (int to = size, i = size = 0; i < to; i++)
            es[i] = null;
    }

    /**
     * 添加集合
     */
    public boolean addAll(Collection<? extends E> c) {
        // 转成 a 数组
        Object[] a = c.toArray();
        // 修改次数++
        modCount++;
        // 如果 a 数组大小为 0 ，返回 ArrayList 数组无变化
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        // 集合剩余空间不足以存储添加进来的元素，则进行扩容
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);
        // 将数组 a 从下标 0 开始到 numNew 位置，复制到集合数组下标 s 到 s + sumNew - 1 位置，共复制 numNew 位
        System.arraycopy(a, 0, elementData, s, numNew);
        size = s + numNew;
        return true;
    }

    /**
     * 插入到指定范围
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        // 校验位置是否在数组范围内
        rangeCheckForAdd(index);

        // 集合数组化
        Object[] a = c.toArray();
        // 修改次数++
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        // 扩容
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);

        // 占用了插入位置，则后移
        int numMoved = s - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index,
                             elementData, index + numNew,
                             numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);
        size = s + numNew;
        return true;
    }

    /**
     * 移除多个元素
     */
    protected void removeRange(int fromIndex, int toIndex) {
        // 下标校验
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    outOfBoundsMsg(fromIndex, toIndex));
        }
        // 修改次数++
        modCount++;
        // 移除 [fromIndex, toIndex) 的多个元素
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    /**
     * 移除区间位置元素（将数组 [lo, hi) 位置赋值为 null）
     */
    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        // 将 es 从 hi 位置开始的元素，移到 lo 位置开始
        System.arraycopy(es, hi, es, lo, size - hi);
        // 将从 [size - hi + lo, size) 的元素置空，因为已经被挪到前面了
        for (int to = size, i = (size -= hi - lo); i < to; i++)
            es[i] = null;
    }

    /**
     * 检测插入位置是否在已有元素的位置，否则抛异常
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * A version used in checking (fromIndex > toIndex) condition
     */
    private static String outOfBoundsMsg(int fromIndex, int toIndex) {
        return "From Index: " + fromIndex + " > To Index: " + toIndex;
    }

    /**
     * 移除集合
     * @see Collection#contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }

    /**
     * 求集合的交集
     * @see Collection#contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }

    /**
     * 批量移除
     * 如果 complement 为 false 时，表示在集合中，就不保留，这显然符合 #removeAll(Collection<?> c) 方法要移除的意图。
     * 如果 complement 为 true 时，表示在集合中，就保留，这符合 #retainAll(Collection<?> c) 方法要求交集的意图
     */
    boolean batchRemove(Collection<?> c, boolean complement,
                        final int from, final int end) {
        // 判空
        Objects.requireNonNull(c);
        final Object[] es = elementData;
        int r;
        // 优化，顺序遍历 elementData 数组，找到第一个不符合 complement ，然后结束遍历
        for (r = from;; r++) {
            // 遍历到尾，都没不符合条件的，直接返回 false 。
            if (r == end)
                return false;
            // 如果包含结果不符合 complement 时，结束
            if (c.contains(es[r]) != complement)
                break;
        }
        // 设置开始写入 w 为 r ，注意不是 r++ 。
        // r++ 后，用于读取下一个位置的元素。因为通过上的优化循环，我们已经 es[r] 是不符合条件的
        int w = r++;
        try {
            // 继续遍历 elementData 数组，如何符合条件，则进行移除
            for (Object e; r < end; r++)
                if (c.contains(e = es[r]) == complement)
                    // 移除的方式，通过将当前值 e 写入到 w 位置，然后 w 跳到下一个位置
                    es[w++] = e;
        } catch (Throwable ex) {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            //  如果 contains 方法发生异常，则将 es 从 r 位置的数据写入到 es 从 w 开始的位置
            System.arraycopy(es, r, es, w, end - r);
            w += end - r;
            throw ex;
        } finally {
            // 修改次数 + 数组元素移动的数量
            modCount += end - w;
            // 将数组 [w, end) 位置赋值为 null
            shiftTailOverGap(es, w, end);
        }
        return true;
    }

    /**
     * 序列化数组
     */
    @java.io.Serial
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out element count, and any hidden stuff
        // 修改次数校验
        int expectedModCount = modCount;
        // 写入非静态属性、非 transient 属性
        s.defaultWriteObject();

        // Write out size as capacity for behavioral compatibility with clone()
        // 写入 size ，主要为了与 clone 方法的兼容
        s.writeInt(size);

        // Write out all elements in the proper order.
        // 逐个写入 elementData 数组的元素
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        // 如果修改次数发生改变，则抛出 ConcurrentModificationException 异常
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * 反序列化数组
     */
    @java.io.Serial
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {

        // Read in size, and any hidden stuff
        // 读取非静态属性、非 transient 属性
        s.defaultReadObject();

        // Read in capacity
        // 读取 size ，不过忽略不用
        s.readInt(); // ignored

        if (size > 0) {
            // like clone(), allocate array based upon size not capacity
            SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, size);
            // 创建 elements 数组
            Object[] elements = new Object[size];

            // Read in all elements in the proper order.
            // 逐个读取
            for (int i = 0; i < size; i++) {
                elements[i] = s.readObject();
            }

            // 赋值给 elementData
            elementData = elements;
        } else if (size == 0) {
            elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new java.io.InvalidObjectException("Invalid size: " + size);
        }
    }

    /**
     * 创建 ListIterator 迭代器
     */
    public ListIterator<E> listIterator(int index) {
        rangeCheckForAdd(index);
        return new ListItr(index);
    }

    /**
     * 创建 ListIterator 迭代器
     */
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * 迭代器
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<E> {
        /**
         *  下一个访问元素的位置，从下标 0 开始。
         */
        int cursor;       // index of next element to return
        /**
         * 上一次访问元素的位置。
         *
         *  1. 初始化为 -1 ，表示无上一个访问的元素
         *  2. 遍历到下一个元素时，lastRet 会指向当前元素，而 cursor 会指向下一个元素。这样，如果我们要实现 remove 方法，移除当前元素，就可以实现了。
         *  3. 移除元素时，设置为 -1 ，表示最后访问的元素不存在了，都被移除咧。
         */
        int lastRet = -1; // index of last element returned; -1 if no such
        /**
         * 创建迭代器时，数组修改次数。
         * 在迭代过程中，如果数组发生了变化，会抛出 ConcurrentModificationException 异常。
         */
        int expectedModCount = modCount;

        // prevent creating a synthetic constructor
        Itr() {}

        // 下标等于 size 则返回 false
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            // 校验
            checkForComodification();
            int i = cursor;
            // 判断如果超过 size 范围，抛出 NoSuchElementException 异常
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            // 判断如果超过 elementData 大小，说明可能被修改了，抛出 ConcurrentModificationException 异常
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // cursor 指向下一个位置
            cursor = i + 1;
            // 返回当前位置元素, 并将 lastRet 指向当前位置
            return (E) elementData[lastRet = i];
        }

        /**
         * 移除当前元素
         */
        public void remove() {
            // 如果 lastRet 小于 0 ，说明没有指向任何元素，抛出 IllegalStateException 异常
            if (lastRet < 0)
                throw new IllegalStateException();
            // 校验是否数组发生了变化
            checkForComodification();

            try {
                // 移除 lastRet 位置的元素
                ArrayList.this.remove(lastRet);
                // cursor 指向 lastRet 位置，因为被移了，所以需要后退下
                cursor = lastRet;
                // lastRet 标记为 -1 ，因为当前元素被移除了
                lastRet = -1;
                // 记录新的数组的修改次数
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * 消费剩余未迭代的元素
         */
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            // 要求 action 非空
            Objects.requireNonNull(action);
            // 获得当前数组大小
            final int size = ArrayList.this.size;
            // 记录 i 指向 cursor
            int i = cursor;
            if (i < size) {
                // 判断如果超过 elementData 大小，说明可能被修改了，抛出 ConcurrentModificationException 异常
                final Object[] es = elementData;
                if (i >= es.length)
                    throw new ConcurrentModificationException();
                for (; i < size && modCount == expectedModCount; i++)
                    action.accept(elementAt(es, i));
                // update once at end to reduce heap write traffic
                // 更新 cursor 和 lastRet 的指向
                cursor = i;
                lastRet = i - 1;
                // 校验数组是否发生变化
                checkForComodification();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        /**
         * 是否有前一个
         */
        public boolean hasPrevious() {
            return cursor != 0;
        }

        /**
         * 下一个
         */
        public int nextIndex() {
            return cursor;
        }

        /**
         * 前一个位置
         */
        public int previousIndex() {
            return cursor - 1;
        }

        /**
         * 前一个元素
         */
        @SuppressWarnings("unchecked")
        public E previous() {
            // 校验是否数组发生了变化
            checkForComodification();
            // 判断如果小于 0 ，抛出 NoSuchElementException 异常
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            // 判断如果超过 elementData 大小，说明可能被修改了，抛出 ConcurrentModificationException 异常
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            // cursor 指向上一个位置
            cursor = i;
            // 返回当前位置的元素, 将 lastRet 指向当前位置
            return (E) elementData[lastRet = i];
        }

        /**
         * 设置当前元素
         */
        public void set(E e) {
            // 如果 lastRet 无指向，抛出 IllegalStateException 异常
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                // 添加元素到当前位置
                int i = cursor;
                ArrayList.this.add(i, e);
                // cursor 指向下一个位置，因为当前位置添加了新的元素，所以需要后挪
                cursor = i + 1;
                // lastRet 标记为 -1 ，因为当前元素并未访问
                lastRet = -1;
                // 记录新的数组的修改次数
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * 创建 ArrayList 的子数组
     * 一定要注意，SubList 不是一个只读数组，而是和根数组 root 共享相同的 elementData 数组，只是说限制了 [fromIndex, toIndex) 的范围
     */
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList<>(this, fromIndex, toIndex);
    }

    private static class SubList<E> extends AbstractList<E> implements RandomAccess {
        private final ArrayList<E> root;
        private final SubList<E> parent;
        private final int offset;
        private int size;

        /**
         * Constructs a sublist of an arbitrary ArrayList.
         */
        public SubList(ArrayList<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        /**
         * Constructs a sublist of another SubList.
         */
        private SubList(SubList<E> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = parent.modCount;
        }

        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E oldValue = root.elementData(offset + index);
            root.elementData[offset + index] = element;
            return oldValue;
        }

        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return size;
        }

        public void add(int index, E element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }

        public void replaceAll(UnaryOperator<E> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }

        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, false);
        }

        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, true);
        }

        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified =
                root.batchRemove(c, complement, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public boolean removeIf(Predicate<? super E> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public Object[] toArray() {
            checkForComodification();
            return Arrays.copyOfRange(root.elementData, offset, offset + size);
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            checkForComodification();
            if (a.length < size)
                return (T[]) Arrays.copyOfRange(
                        root.elementData, offset, offset + size, a.getClass());
            System.arraycopy(root.elementData, offset, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof List)) {
                return false;
            }

            boolean equal = root.equalsRange((List<?>)o, offset, offset + size);
            checkForComodification();
            return equal;
        }

        public int hashCode() {
            int hash = root.hashCodeRange(offset, offset + size);
            checkForComodification();
            return hash;
        }

        public int indexOf(Object o) {
            int index = root.indexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public int lastIndexOf(Object o) {
            int index = root.lastIndexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);

            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = SubList.this.modCount;

                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i < size) {
                        final Object[] es = root.elementData;
                        if (offset + i >= es.length)
                            throw new ConcurrentModificationException();
                        for (; i < size && root.modCount == expectedModCount; i++)
                            action.accept(elementAt(es, offset + i));
                        // update once at end to reduce heap write traffic
                        cursor = i;
                        lastRet = i - 1;
                        checkForComodification();
                    }
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        root.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (root.modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        private void checkForComodification() {
            if (root.modCount != modCount)
                throw new ConcurrentModificationException();
        }

        private void updateSizeAndModCount(int sizeChange) {
            SubList<E> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while (slist != null);
        }

        public Spliterator<E> spliterator() {
            checkForComodification();

            // ArrayListSpliterator not used here due to late-binding
            return new Spliterator<E>() {
                private int index = offset; // current index, modified on advance/split
                private int fence = -1; // -1 until used; then one past last index
                private int expectedModCount; // initialized when fence set

                private int getFence() { // initialize fence to size on first use
                    int hi; // (a specialized variant appears in method forEach)
                    if ((hi = fence) < 0) {
                        expectedModCount = modCount;
                        hi = fence = offset + size;
                    }
                    return hi;
                }

                public ArrayList<E>.ArrayListSpliterator trySplit() {
                    int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                    // ArrayListSpliterator can be used here as the source is already bound
                    return (lo >= mid) ? null : // divide range in half unless too small
                        root.new ArrayListSpliterator(lo, index = mid, expectedModCount);
                }

                public boolean tryAdvance(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int hi = getFence(), i = index;
                    if (i < hi) {
                        index = i + 1;
                        @SuppressWarnings("unchecked") E e = (E)root.elementData[i];
                        action.accept(e);
                        if (root.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                    return false;
                }

                public void forEachRemaining(Consumer<? super E> action) {
                    Objects.requireNonNull(action);
                    int i, hi, mc; // hoist accesses and checks from loop
                    ArrayList<E> lst = root;
                    Object[] a;
                    if ((a = lst.elementData) != null) {
                        if ((hi = fence) < 0) {
                            mc = modCount;
                            hi = offset + size;
                        }
                        else
                            mc = expectedModCount;
                        if ((i = index) >= 0 && (index = hi) <= a.length) {
                            for (; i < hi; ++i) {
                                @SuppressWarnings("unchecked") E e = (E) a[i];
                                action.accept(e);
                            }
                            if (lst.modCount == mc)
                                return;
                        }
                    }
                    throw new ConcurrentModificationException();
                }

                public long estimateSize() {
                    return getFence() - index;
                }

                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
                }
            };
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
            action.accept(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
     * Overriding implementations should document the reporting of additional
     * characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator(0, -1, 0);
    }

    /** Index-based split-by-two, lazily initialized Spliterator */
    final class ArrayListSpliterator implements Spliterator<E> {

        /*
         * If ArrayLists were immutable, or structurally immutable (no
         * adds, removes, etc), we could implement their spliterators
         * with Arrays.spliterator. Instead we detect as much
         * interference during traversal as practical without
         * sacrificing much performance. We rely primarily on
         * modCounts. These are not guaranteed to detect concurrency
         * violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to
         * be worthwhile in practice. To carry this out, we (1) lazily
         * initialize fence and expectedModCount until the latest
         * point that we need to commit to the state we are checking
         * against; thus improving precision.  (This doesn't apply to
         * SubLists, that create spliterators with current non-lazy
         * values).  (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach
         * (the most performance-sensitive method). When using forEach
         * (as opposed to iterators), we can normally only detect
         * interference after actions, not before. Further
         * CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small
         * elementData array given its size(), that could only have
         * occurred due to interference.  This allows the inner loop
         * of forEach to run without any further checks, and
         * simplifies lambda-resolution. While this does entail a
         * number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation
         * occur anywhere other than inside forEach itself.  The other
         * less-often-used methods cannot take advantage of most of
         * these streamlinings.
         */

        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set

        /** Creates new spliterator covering the given range. */
        ArrayListSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            if ((hi = fence) < 0) {
                expectedModCount = modCount;
                hi = fence = size;
            }
            return hi;
        }

        public ArrayListSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
                new ArrayListSpliterator(lo, index = mid, expectedModCount);
        }

        // 单个对元素执行给定的动作，如果有剩下元素未处理返回true，否则返回false
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked") E e = (E)elementData[i];
                action.accept(e);
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((a = elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = modCount;
                    hi = size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked") E e = (E) a[i];
                        action.accept(e);
                    }
                    if (modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    // A tiny bit set implementation

    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return removeIf(filter, 0, size);
    }

    /**
     * Removes all elements satisfying the given predicate, from index
     * i (inclusive) to index end (exclusive).
     */
    boolean removeIf(Predicate<? super E> filter, int i, final int end) {
        Objects.requireNonNull(filter);
        int expectedModCount = modCount;
        final Object[] es = elementData;
        // Optimize for initial run of survivors
        for (; i < end && !filter.test(elementAt(es, i)); i++)
            ;
        // Tolerate predicates that reentrantly access the collection for
        // read (but writers still get CME), so traverse once to find
        // elements to delete, a second pass to physically expunge.
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;   // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test(elementAt(es, i)))
                    setBit(deathRow, i - beg);
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    es[w++] = es[i];
            shiftTailOverGap(es, w, end);
            return true;
        } else {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return false;
        }
    }

    /**
     * 根据传入的值替换
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        replaceAllRange(operator, 0, size);
        // TODO(8203662): remove increment of modCount from ...
        modCount++;
    }

    private void replaceAllRange(UnaryOperator<E> operator, int i, int end) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        for (; modCount == expectedModCount && i < end; i++)
            es[i] = operator.apply(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        modCount++;
    }

    void checkInvariants() {
        // assert size >= 0;
        // assert size == elementData.length || elementData[size] == null;
    }
}
