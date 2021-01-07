import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;

/**
 * @Author: hoo
 * @Date: 2020/12/25 11:29
 */
public class ArrayListDemo {

    public static void main(String[] args) {
        // 类型转换
        Object[] arrays = new Integer[100];
        System.out.println(arrays.getClass());
        arrays = Arrays.copyOf(arrays, arrays.length, Object[].class);
        System.out.println(arrays.getClass());

        // 中断多重循环
        Integer o = new Integer(1);
        found: {
            if (o == null) {
                for (int a = 0; a < 100; a++) {
                    System.out.println(a);
                    if (a == 50) {
                        break found;
                    }
                }
            } else {
                for (int a = 0; a < 100; a++) {
                    System.out.println("我再 else 里面：" + a);
                    for (int b = 0; b < 100; b++) {
                        System.out.println(b);
                        if (b == o.intValue()) {
                            System.out.println("我要跳出循环");
                            break found;
                        }
                    }
                }
            }
        }

        // SubList 不是一个只读数组，而是和根数组 root 共享相同的 elementData 数组，只是说限制了 [fromIndex, toIndex) 的范围
        List<Integer> test = new ArrayList<>();
        test.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        test = test.subList(2, 5);
        System.out.println(test.size());
        List<Integer> testSubList = new ArrayList<>();
        testSubList.addAll(test);
        System.out.println(testSubList);
        // todo
        Spliterator<Integer> spliterator = test.spliterator();
    }

    /**
     * 一个 bug
     */
    public static void test02() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        Object[] array = list.toArray(); // JDK8 返回 Integer[] 数组，JDK9+ 返回 Object[] 数组。
        System.out.println("array className ：" + array.getClass().getSimpleName());

        // 此处，在 JDK8 和 JDK9+ 表现不同，前者会报 ArrayStoreException 异常，后者不会。
        array[0] = new Object();
    }
}
